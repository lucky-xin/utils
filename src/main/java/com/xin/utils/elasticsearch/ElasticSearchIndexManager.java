package com.xin.utils.elasticsearch;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xin.utils.CollectionUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.elasticsearch.exception.ElasticSearchException;
import com.xin.utils.log.LogFactory;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Luchaoxin
 * @Description: elasticsearch索引管理类类不提供给外部使用，主要对index进行插入，删除，更新
 * @date 2018-08-25
 */
class ElasticSearchIndexManager {

    private Logger logger = LogFactory.getLogger(ElasticSearch.class.getSimpleName());

    /**
     * 批量操作超时时间
     */
    private static final TimeValue TIMEOUT_BAT = TimeValue.timeValueMillis(10000L);

    /**
     * 一般超时时间
     */
    private static final TimeValue TIMEOUT = new TimeValue(3000L);

    /**
     * 批量提交数量
     */
    private static final int BATCH_COMMIT_SIZE = 500;

    private Client client;

    private ElasticSearchHelper elasticSearchHelper;

    ElasticSearchIndexManager(Client client, ElasticSearchHelper elasticSearchHelper) {
        this.client = client;
        this.elasticSearchHelper = elasticSearchHelper;
    }

    /**
     * 为对象每个属性建立index,存储数据
     *
     * @param index   索引名称
     * @param type    索引type
     * @param objects 存放要建立索引的对象
     * @throws ElasticSearchException
     */
    public <T> void indexAll(String index, String type, List<T> objects) throws ElasticSearchException {
        if (CollectionUtil.isEmpty(objects)) {
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(objects);
        List<Map<String, Object>> list = new ArrayList<>(objects.size());
        list = gson.fromJson(json, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        indexAll(index, type, null, list);
    }

    /**
     * 为list之中的每个map建立文档，并且为map之中每个key建立索引
     *
     * @param index       索引名称
     * @param type        索引type
     * @param idFieldName 每一个map的id,其值为map之中key为idFieldName的value
     * @param list        存放要建立索引的字段为map的key，对应索引的值为map的value,使用list封装所有map
     * @throws ElasticSearchException
     */
    public void indexAll(String index, String type, String idFieldName, List<Map<String, Object>> list) throws ElasticSearchException {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        prepareMapping(index, type);
        BulkRequestBuilder request = client.prepareBulk();

        try {
            //如果大于1000则分批次提交
            boolean shouldInBatch = list.size() > BATCH_COMMIT_SIZE;

            for (int i = 0; i < list.size(); i++) {

                Map<String, Object> map = list.get(i);

                String id = StringUtil.toString(map.get(idFieldName));
                IndexRequestBuilder requestBuilder;
                if (StringUtil.isEmpty(id)) {
                    requestBuilder = client.prepareIndex(index, type).setSource(map);
                } else {
                    requestBuilder = client.prepareIndex(index, type, id).setSource(map);
                }

                request.add(requestBuilder);
                //如果是1000的整数倍或者已经遍历完毕
                boolean shouldCommit = (i % BATCH_COMMIT_SIZE == 0L) || (i == list.size() - 1);

                if (shouldInBatch && shouldCommit) {
                    doCommit(request);
                    request = this.client.prepareBulk();
                }
            }

            if (!shouldInBatch) {
                doCommit(request);
            }
            logger.info("ElasticSearch批量入库成功 index[" + index + "] type[" + type + "]");
        } catch (Exception e) {
            String errorMsg = "ElasticSearch 提交数据异常 " + e.getMessage();
            logger.error(errorMsg);
            throw new ElasticSearchException(errorMsg);
        }
    }

    public void indexJson(String index, String type, String json) {
        IndexResponse response = client.prepareIndex(index, type)
                .setSource(json, XContentType.JSON)
                .get();
        if (response.status().equals(RestStatus.OK)) {

        }
    }

    private void doCommit(BulkRequestBuilder request) throws ElasticSearchException {
        BulkResponse response = request.setTimeout(TIMEOUT_BAT).execute().actionGet();
        if (response.hasFailures()) {
            String errorMsg = response.buildFailureMessage();
            logger.error("ElasticSearch 入库异常 " + errorMsg);
            throw new ElasticSearchException(errorMsg);
        }
    }

    // TODO 具体校验字段还未完成，可以配置一个Schema文件，配置属性名和map的key一样
    private Map<String, Object> verifyValues(Map<String, Object> data) {

        return data;
    }

    /**
     * 根据索引 type id 删除elasticsearch一条记录
     *
     * @param index 要删除的index
     * @param type  要删除的type
     * @param id    要删除的id
     * @return
     */
    public boolean delete(String index, String type, String id) {
        logger.info("Delete one doc: " + id);
        DeleteResponse response = this.client.prepareDelete(index, type, id).setTimeout(TIMEOUT).execute().actionGet();
        if (response.status() == RestStatus.OK) {
            logger.debug("Document(" + id + ") deleted");
            return true;
        } else {
            logger.debug("Document(" + id + ") not found");
        }
        return false;
    }

    /**
     * 根据索引 type id 删除elasticsearch一条记录
     *
     * @param index 要删除的index
     * @param type  要删除的type
     * @param row   key-value 对应字段名和字段值
     * @return
     */
    public void delete(String index, String type, Map<String, Object> row) throws ElasticSearchException {
        logger.info("Delete one doc: " + row);

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        QueryBuilder query = null;
        //1.先查询获取所有要删除的id
        for (Map.Entry<String, Object> entry : row.entrySet()) {

            query = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
            builder.must(query);
        }

        SearchRequestBuilder req = this.client.prepareSearch(CollectionUtil.toArray(index))
                .setTypes(CollectionUtil.toArray(type))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        req = req.setQuery(builder).setTimeout(TIMEOUT).setFrom(0).setSize(100000);

        SearchResponse response = req.execute().actionGet();
        SearchHit[] hits = response.getHits().getHits();
        if (CollectionUtil.isEmpty(hits)) {
            return;
        }
        //2.遍历结果获取所有查询id
        List<String> ids = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            if (type.equalsIgnoreCase(hit.getType())) {
                ids.add(hit.getId());
                logger.info("Document deleting Id " + hit.getId());
            }
        }
        //3.根据id进行批量删除
        delete(index, type, ids);
    }

    /**
     * 根据ElasticSearch的id进行批量删除
     *
     * @param index 要进行批量删除的索引
     * @param type  要进行批量删除type
     * @param ids   要进行批量的所有id
     * @throws ElasticSearchException
     */
    public void delete(String index, String type, List<String> ids) throws ElasticSearchException {
        BulkRequestBuilder builder = client.prepareBulk();
        for (String id : ids) {
            builder.add(client.prepareDelete(index, type, id).request());
        }
        BulkResponse bulkResponse = builder.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new ElasticSearchException(bulkResponse.buildFailureMessage());
        }
        logger.info("批量删除数据成功 删除数目" + ids.size());
    }

    private void prepareMapping(String index, String type) throws ElasticSearchException {
        elasticSearchHelper.createMapping(index, type);
    }

}