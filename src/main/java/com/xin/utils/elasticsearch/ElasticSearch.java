package com.xin.utils.elasticsearch;


import com.xin.utils.StringUtil;
import com.xin.utils.elasticsearch.exception.ElasticSearchException;
import com.xin.utils.io.IOUtil;
import com.xin.utils.model.PageQueryResult;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Luchaoxin
 * @Description: elasticsearch操作类
 * @date 2018-08-25
 */
public class ElasticSearch {

    private static ElasticSearch elasticSearch;

    private TransportClient client;

    private ElasticSearchSearcher searchSearcher;

    private ElasticSearchIndexManager indexManager;

    private ElasticSearchHelper elasticSearchHelper;

    /**
     * 私有化构造器，单例模式使用
     * 默认配置此util项目已经配置好
     * 1.如果配置都和util的resource下配置一样可以不用进行配置
     * 2.如果配置不一样就在自己项目之中的resource文件夹之中覆盖掉配置文件
     */
    private ElasticSearch() {
        init();
    }


    private void init() {
        InputStream is = null;
        try {
            //1.获取elasticsearch配置文件
            is = ElasticSearch.class.getResourceAsStream("/config/elasticsearch.properties");
            Properties properties = new Properties();
            properties.load(is);
            String esUserName = properties.getProperty("elasticsearch.user");
            String userAndPassword = properties.getProperty("elasticsearch.user.and.password");
            String esHost = properties.getProperty("elasticsearch.host");
            int esPort = StringUtil.toInteger(properties.getProperty("elasticsearch.port"));

            // 2. 设置密钥连接
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put(esUserName, userAndPassword)
                    .build();

            // 3. 初始化Client
            client = new PreBuiltXPackTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddresses.forString(esHost), esPort));

            // 4.读取schema.xml配置文件
            elasticSearchHelper = new ElasticSearchHelper(client);
            elasticSearchHelper.loadSchema("/schema/schema.xml");
            searchSearcher = new ElasticSearchSearcher(client, elasticSearchHelper);
            indexManager = new ElasticSearchIndexManager(client, elasticSearchHelper);
        } catch (Exception e) {
            throw new IllegalAccessError("elasticsearch初始化错误，" + e.getMessage());
        } finally {
            IOUtil.close(is);
        }
    }


    /**
     * 双重检查+锁 实现单列，初始化失败直接抛出IllegalAccessError异常
     *
     * @return ElasticSearch对象
     */
    public static ElasticSearch getInstance() {

        if (null == elasticSearch) {
            synchronized (ElasticSearch.class) {
                if (null == elasticSearch) {
                    elasticSearch = new ElasticSearch();
                }
            }
        }

        return elasticSearch;
    }

    public ElasticSearchHelper getElasticSearchHelper() {
        return elasticSearchHelper;
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
        indexManager.indexAll(index, type, idFieldName, list);
    }

    /**
     * 为list之中的每个对象建立文档并且为对象每个属性建立索引
     *
     * @param index 索引名称
     * @param type  索引type
     * @param list  存放要建立索引的对象
     * @throws ElasticSearchException
     */
    public <T> void indexObject(String index, String type, List<T> list) throws ElasticSearchException {
        indexManager.indexAll(index, type, list);
    }


    /**
     * 为map建立文档并且为map之中的每个key建立索引并存储数据
     *
     * @param index       索引名称
     * @param type        索引type
     * @param idFieldName 每一个map的id,其值为map之中key为idFieldName的value
     * @param row         存放要建立索引的字段为map的key，对应索引的值为map的value
     * @throws ElasticSearchException
     */
    public void index(String index, String type, String idFieldName, Map<String, Object> row) throws ElasticSearchException {
        indexManager.indexAll(index, type, idFieldName, new ArrayList<Map<String, Object>>() {{
            add(row);
        }});
    }

    public void indexJson(String index, String type, String json) {
        indexManager.indexJson(index, type, json);
    }

    /**
     * 根据需要，自己封装所有查询调价在{@link Query} 之中，然后es根据Query所有的调价进行查询，支持分页查询
     *
     * @param index 要查询的索引
     * @param type  要查询的类型
     * @param query 封装条件查询对象
     * @return 返回分页查询数据
     * @throws ElasticSearchException
     */
    public PageQueryResult query(String index, String type, Query query) {
        return searchSearcher.query(index, type, query);
    }

    /**
     * 根据id查询es数据
     *
     * @param index 要查询的索引
     * @param type  要查询的索引的type
     * @param ids   所有要查询的id
     * @return 返回查询数据
     * @throws ElasticSearchException
     */
    public PageQueryResult queryByIds(String index, String type, String[] ids) {
        return searchSearcher.queryByIds(new String[]{index}, type, ids);
    }

    /**
     * 根据id查询es数据
     *
     * @param indexes 要查询的所有索引
     * @param type    要查询的索引的type
     * @param ids     所有要查询的所有id
     * @return 返回查询数据
     * @throws ElasticSearchException
     */
    public PageQueryResult queryByIds(String[] indexes, String type, String[] ids) {
        return searchSearcher.queryByIds(indexes, type, ids);
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
        indexManager.delete(index, type, row);
    }

    /**
     * 根据索引 type id 删除elasticsearch一条记录
     *
     * @param index 要删除的index
     * @param type  要删除的type
     * @param id    要删除的id
     * @return 删除成功返回true
     */
    public void delete(String index, String type, String id) {
        indexManager.delete(index, type, id);
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
        indexManager.delete(index, type, ids);
    }


    public TransportClient getClient() {
        return this.client;
    }

}