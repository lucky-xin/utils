package com.xin.utils.elasticsearch;

import com.xin.utils.CollectionUtil;
import com.xin.utils.DateUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.elasticsearch.model.Index;
import com.xin.utils.model.Field;
import com.xin.utils.model.PageQueryResult;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Luchaoxin
 * @Description: 执行查询操作类
 * @date 2018-08-25
 */
class ElasticSearchSearcher {

    private Client client;

    private ElasticSearchHelper elasticSearchHelper;

    ElasticSearchSearcher(Client client, ElasticSearchHelper elasticSearchHelper) {
        this.client = client;
        this.elasticSearchHelper = elasticSearchHelper;
    }

    public PageQueryResult queryByIds(String datasource, String table, String[] ids) {
        return queryByIds(new String[]{datasource}, table, ids);
    }

    public PageQueryResult queryByIds(String[] datasource, String table, String[] ids) {
        IdsQueryBuilder idsQuery = QueryBuilders.idsQuery(new String[0]);
        idsQuery.addIds(ids);

        SearchRequestBuilder idsReq = this.client.prepareSearch(datasource).setTypes(table);

        idsReq.setQuery(idsQuery)
                .setFrom(0)
                .setSize(ids.length)
                .setTimeout(new TimeValue(120000L));

        SearchResponse idsResp = idsReq.execute().actionGet();

        long useTime = idsResp.getTookInMillis();

        SearchHit[] hits = idsResp.getHits().getHits();

        List list = new ArrayList();

        for (SearchHit hit : hits) {
            Map source = hit.getSource();
            source.put("_id", hit.getId());
            source.put("type", hit.getType());
            source.put("index", hit.getIndex());

            list.add(source);
        }

        return new PageQueryResult(list.size(), useTime, list);
    }

    public PageQueryResult query(String index, String type, Query query) {
        List<Map<String, Object>> list = new ArrayList<>();

        float findWordHitRatio = 0.0F;

        SearchResponse response = doQuery(index, type, query);

        long useTime = response.getTookInMillis();

        float maxScore = response.getHits().getMaxScore();

        long count = response.getHits().getTotalHits();

        String[] fields = query.getQueryFields();

        SearchHit[] hits = response.getHits().getHits();

        for (SearchHit hit : hits) {

            if (type.equalsIgnoreCase(hit.getType())) {
                Map<String, Object> data = getData(hit);
                list.add(data);
            }
        }

        return new PageQueryResult(response.getScrollId(), count, useTime, list);
    }

    public PageQueryResult scrollQuery(String scrollId, long scrollTime) {
        List<Map<String, Object>> data = new ArrayList<>();

        SearchResponse scrollResp = client.prepareSearchScroll(scrollId).setScroll(new TimeValue(scrollTime)).execute().actionGet();
        long count = scrollResp.getHits().getTotalHits();
        long useTime = scrollResp.getTookInMillis();

        for (SearchHit hit : scrollResp.getHits().getHits()) {
            data.add(getData(hit));
        }
        return new PageQueryResult(scrollResp.getScrollId(), count, useTime, data);
    }
    
    private SearchResponse doQuery(String index, String type, Query query) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        QueryBuilder tempBuilder = null;
        //1.初始化查询
        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(query.getSearchType());
        //2.判断是全部查询还是条件查询

        if (query.isSearchAll()) {
            tempBuilder = QueryBuilders.matchAllQuery();
            searchRequestBuilder.setQuery(queryBuilder.must(tempBuilder));
        } else {
            //添加所有条件查询条件
            addQueryCondition(index, type, searchRequestBuilder, query);
        }
        //3.获取返回分页信息
        int pageNo = query.getPageNo();
        int pageSize = query.getPageSize();

        //3.1计算分页信息
        pageNo = pageNo == 0 ? 1 : pageNo;
        int from = (pageNo - 1) * pageSize;

        //4.添加高亮显示字段
        if (query.hasHighlightBuilder()) {
            searchRequestBuilder.highlighter(query.getHighlightBuilder());
        }

        //5.添加聚合查询字段
        if (!StringUtil.isEmpty(query.getAggregation())) {
            addAggregation(query, searchRequestBuilder, pageSize);
        }

        //6.设置排序
        if (!CollectionUtil.isEmpty(query.getSorts())) {
            for (Sort sort : query.getSorts()) {
                Object sortBuilder = SortBuilders.fieldSort(sort.getField()).order(sort.getOrder());
                searchRequestBuilder.addSort((SortBuilder) sortBuilder);
            }
        }

        //7.设置分页信息，超时时间
        Duration scrollTime = query.getScrollTime();
        if (0 == query.getScrollTime().toMillis()) {
            searchRequestBuilder.setTimeout(new TimeValue(query.getTimeout().toMillis()))
                    .setFrom(from)
                    .setSize(pageSize);
        } else {
            searchRequestBuilder.setScroll(new TimeValue(scrollTime.toMillis())).setSize(pageSize);
        }

        //8.执行查询操作
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        return response;
    }

    private void addAggregation(Query query, SearchRequestBuilder searchRequestBuilder, int pageSize) {
        boolean asc = query.getAggregationSort().equals(SortOrder.ASC);

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("aggregation").
                field(query.getAggregation())
                .size(pageSize)
                .order(Terms.Order.count(asc));
        searchRequestBuilder.addAggregation(aggregationBuilder);
    }

    /**
     * 分发请求服务
     *
     * @param searchRequestBuilder
     * @param query
     */
    private void addQueryCondition(String indexName, String type, SearchRequestBuilder searchRequestBuilder, Query query) {

        QueryBuilder tempBuilder;
        QueryBuilder fb;

        BoolQueryBuilder subQuery;

        BoolQueryBuilder queryBuildHolder = QueryBuilders.boolQuery();

        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();

        Index index = elasticSearchHelper.getIndex(indexName, type);
        Map<String, Field> metadata = null;

        if (null != index) {
            metadata = index.getMetaData();
        }
        Field field;
        for (Condition condition : query.getConditions()) {
            String fieldName = condition.getField();
            field = null != metadata ? metadata.get(fieldName) : null;

            Object[] values = condition.getValues();
            float boost = condition.getBoost();
            boolean isDate = null != field && "date".equals(field.getType());

            if (isDate) {
                values = convertValues(field, values);
            }

            switch (condition.getConditionType()) {
                case QUERY_BUILDER:
                    queryBuildHolder.must((QueryBuilder) values[0]);
                    break;

                case EQUAL:
                    subQuery = QueryBuilders.boolQuery();
                    for (Object value : values) {
                        subQuery.should(QueryBuilders.matchPhraseQuery(fieldName, value));
                    }
                    queryBuildHolder.must(subQuery);
                    break;

                case TERMS_QUERY:
                    queryBuildHolder.must(QueryBuilders.termsQuery(fieldName, values));
                    break;

                case DIS_MAX_QUERY:
                    DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
                    for (String conditionKey : condition.getFields()) {
                        disMaxQueryBuilder.add(QueryBuilders.matchQuery(conditionKey, values[0]));
                    }
                    queryBuildHolder.must(disMaxQueryBuilder);
                    break;

                case NOT_EQUAL:
                    queryBuildHolder.mustNot(QueryBuilders.termsQuery(fieldName, values));
                    break;

                case EQUAL_FILTER:
                    filterBuilder.must(QueryBuilders.termsQuery(fieldName, values));
                    break;

                case MORE_LIKE_THIS:
                    MoreLikeThisQueryBuilder.Item[] items = new MoreLikeThisQueryBuilder.Item[1];
                    MoreLikeThisQueryBuilder.Item item = new MoreLikeThisQueryBuilder.Item();
                    items[0] = item;
                    tempBuilder = QueryBuilders.moreLikeThisQuery(CollectionUtil.toArray(fieldName), (String[]) values, items);
                    queryBuildHolder.must(tempBuilder);
                    break;
                case WILDCARD:
                    subQuery = QueryBuilders.boolQuery();
                    for (Object match : condition.getValues()) {
                        tempBuilder = QueryBuilders.wildcardQuery(fieldName, match.toString());
                        subQuery.should(tempBuilder);
                    }
                    queryBuildHolder.must(subQuery);
                    break;

                case WILDCARD_FILTER:
                    for (Object match : values) {
                        filterBuilder.must(QueryBuilders.termQuery(fieldName, StringUtil.toString(match)));
                    }
                    break;
                case FUZZY:
                    tempBuilder = QueryBuilders.fuzzyQuery(fieldName, values[0])
                            .fuzziness(Fuzziness.AUTO);
                    addCondition(queryBuildHolder, tempBuilder, boost);
                    break;

                case RANGE_FILTER:
                case BETWEEN_FILTER:
                    fb = QueryBuilders.rangeQuery(fieldName)
                            .from(values[0])
                            .to(values[1]);
                    filterBuilder.must(fb);
                    break;
                case MATCH_PHRASE:
                    queryBuildHolder.must(QueryBuilders.matchPhraseQuery(fieldName, values[0]));
                    break;
                case QUERY_STRING:
                    queryBuildHolder.must(QueryBuilders.queryStringQuery(StringUtil.toString(values[0])));
                    break;

                case MATCH_QUERY:
                    subQuery = QueryBuilders.boolQuery();
                    for (Object value : values) {
                        tempBuilder = QueryBuilders.matchQuery(fieldName, value);
                        subQuery.should(tempBuilder);
                    }
                    queryBuildHolder.must(subQuery);
                    break;

                /**
                 * 查询范围条件
                 */
                case RANGE:
                    tempBuilder = QueryBuilders.rangeQuery(fieldName).gte(values[0]).lte(values[1]);
                    addCondition(queryBuildHolder, tempBuilder, boost);
                    break;

                case GREATER_THAN_OR_EQUAL:
                    tempBuilder = QueryBuilders.rangeQuery(fieldName).gte(values[0]);
                    addCondition(queryBuildHolder, tempBuilder, boost);
                    break;

                case LESS_THAN:
                    tempBuilder = QueryBuilders.rangeQuery(fieldName).lt(values[0]);
                    addCondition(queryBuildHolder, tempBuilder, boost);
                    break;

                case LESS_THAN_OR_EQUAL:
                    tempBuilder = QueryBuilders.rangeQuery(fieldName).lte(values[0]);
                    addCondition(queryBuildHolder, tempBuilder, boost);
                    break;

                default:
                    break;
            }
        }
        searchRequestBuilder.setQuery(queryBuildHolder).setSearchType(query.getSearchType());
        if (filterBuilder.hasClauses()) {
            searchRequestBuilder.setPostFilter(filterBuilder);
        }
    }

    private Object[] convertValues(Field field, Object[] values) {
        List<Object> result = new ArrayList<>(values.length);
        for (Object value : values) {
            boolean shouldConvert = value instanceof Date;
            if (shouldConvert) {
                Date date = (Date) value;
                value = DateUtil.toString(date, field.getFormat());
            }
            result.add(value);
        }
        return result.toArray();
    }

    private void addCondition(BoolQueryBuilder builder, QueryBuilder queryBuilder, float boost) {
        float boostThreshold = 1.0F;
        if (boost > boostThreshold) {
            builder.must(queryBuilder).boost(boost);
        } else {
            builder.must(queryBuilder);
        }
    }

    private Map<String, Object> getData(SearchHit hit) {

        String id = hit.getId();
        String type = hit.getType();
        String index = hit.getIndex();

        Map<String, Object> map = hit.getSource();

        map.put("score", hit.getScore());
        map.put("_id", id);
        map.put("type", type);
        map.put("index", index);

        return map;
    }

    private String toPercent(float f) {
        if (Float.isNaN(f)) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(f);
    }


}
