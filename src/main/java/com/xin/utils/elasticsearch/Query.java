package com.xin.utils.elasticsearch;

import com.xin.utils.AssertUtil;
import com.xin.utils.CollectionUtil;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.time.Duration;
import java.util.*;

/**
 * @author Luchaoxin
 * @Description: 封装所有查询信息类，包括所有的查询条件，以及分页信息
 * @date 2018-08-25
 */
public class Query {

    /**
     * 第几页
     */
    private int pageNo;

    /**
     * 每页最多显示数量
     */
    private int pageSize;

    /**
     * 所有排序的字段和排序规则
     */
    private List<Sort> sorts = new ArrayList();

    /**
     * 对结果设置高亮显示
     * 对应SearchRequestBuilder的HighlightBuilder的fields
     */
    private Set<String> highlights = new HashSet();

    /**
     * 需要查询的字段，也就是返回的字段
     */
    private String[] queryFields;

    /**
     * 所有的条件
     */
    private List<Condition> conditions = new ArrayList();

    /**
     * 超时时间
     */
    private Duration timeout = Duration.ofSeconds(5);

    /**
     * scroll分页超时时间
     */
    private Duration scrollTime = Duration.ZERO;

    /**
     * 搜索类型 具体请看{@link org.elasticsearch.action.search.SearchType}
     */
    private SearchType searchType = SearchType.QUERY_THEN_FETCH;

    /**
     * 聚合查询父聚合
     */
    private String aggregation;

    /**
     * 聚合查询子聚合
     */
    private String subAggregation;

    /**
     * 聚合排序规则
     */
    private SortOrder aggregationSort = SortOrder.DESC;

    /**
     * 构造器
     *
     * @param pageNo   获取第几页信息
     * @param pageSize 分页数量
     */
    public Query(int pageNo, int pageSize) {
        AssertUtil.checkCondition(pageNo > 0, "elasticsearch查询返回页数必须大于零");
        AssertUtil.checkCondition(pageSize > 0, "elasticsearch查询分页数量必须大于零");
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    /**
     * 构造器
     *
     * @param pageNo      获取第几页信息
     * @param pageSize    分页数量
     * @param queryFields 查询返回的所有字段
     */
    public Query(int pageNo, int pageSize, String[] queryFields) {
        AssertUtil.checkCondition(pageNo > 0, "elasticsearch查询返回页数必须大于零");
        AssertUtil.checkCondition(pageSize > 0, "elasticsearch查询分页数量必须大于零");
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.queryFields = queryFields;
    }

    /**
     * 添加一个QueryBuilder
     *
     * @param queryBuilder
     * @return 返回当前调用Query
     */
    public Query addQueryBuilder(QueryBuilder queryBuilder) {
        conditions.add(new Condition(ConditionType.QUERY_BUILDER, queryBuilder));
        return this;
    }

    /**
     * 添加相等条件
     *
     * @param fieldName 相等条件字段
     * @param values    相等条件字段的所有值
     * @return 返回当前调用Query
     */
    public Query termsQueryCondition(String fieldName, Object[] values) {
        return termsQueryCondition(fieldName, values, 1);
    }

    /**
     * 添加termsQuery条件，如果查询的整个语句被分词了可能会查询不到记录，请用equalCondition
     *
     * @param fieldName 相等条件字段
     * @param values    相等条件字段的所有值
     * @param boost     termsQuery的boost
     * @return 返回当前调用Query
     */
    public Query termsQueryCondition(String fieldName, Object[] values, float boost) {
        conditions.add(new Condition(ConditionType.TERMS_QUERY, boost, fieldName, values));
        return this;
    }

    /**
     * 添加相等条件,为精确查找，不会进行分词匹配，如果elasticsearch对field进行了分词很有可能查询不到值
     *
     * @param fieldName 相等条件字段
     * @param values    相等条件字段的所有值
     * @return 返回当前调用Query
     */
    public Query equalCondition(String fieldName, Object[] values) {
        conditions.add(new Condition(ConditionType.EQUAL, fieldName, values));
        return this;
    }

    /**
     * 添加matchQuery条件
     *
     * @param fieldName matchQuery条件字段
     * @param values    matchQuery条件字段的所有值
     * @return 返回当前调用Query
     */
    public Query matchQueryCondition(String fieldName, Object[] values) {
        conditions.add(new Condition(ConditionType.MATCH_QUERY, fieldName, values));
        return this;
    }

    /**
     * disMaxQuery条件为elasticsearch的dis_max.
     * 筛选匹配term在不同地方出现最多次排在前面，
     * 而不是term分词之后每个词出现次数较高的获得较高的分数（排在前面）
     * 如： 需要匹配某篇文章,title,和content的字符"Brown fox"
     * 默认筛选可会分别将"Brown"和"fox"分开进行帅选，两个单词都在title,和content出现
     * 分数就比较高，可是我们想要同时出现"Brown fox"同时出现的排在前面,此时就使用disMaxQuery
     * 具体请看{@link org.apache.lucene.search.DisjunctionMaxQuery}
     *
     * @param fieldNames 所有匹配的字段名称
     * @param value      匹配字段的值
     * @return 返回当前调用Query
     */
    public Query disMaxQueryCondition(String[] fieldNames, Object value) {
        conditions.add(new Condition(ConditionType.DIS_MAX_QUERY, 1F, fieldNames, CollectionUtil.toArray(value)));
        return this;
    }

    /**
     * 添加相等条件，使用map封装字段和值
     *
     * @param equalParams map的key为相等条件字段，value为相等的值
     * @return 返回当前调用Query
     */
    public Query equalCondition(Map<String, Object> equalParams) {

        for (Map.Entry<String, Object> entry : equalParams.entrySet()) {
            conditions.add(new Condition(ConditionType.EQUAL,
                    entry.getKey(),
                    CollectionUtil.toArray(entry.getValue())));
        }
        return this;
    }

    /**
     * 添加不相等条件
     *
     * @param key    不相等条件字段名
     * @param values 不相等条件字段名对应的所有值
     * @return 返回当前调用Query
     */
    public Query notEqualCondition(String key, Object[] values) {
        this.conditions.add(new Condition(ConditionType.NOT_EQUAL, key, values));
        return this;
    }

    /**
     * 添加不相等条件
     *
     * @param notEquals 不相等条件字段名->不相等条件字段值 对应map的 key->value
     * @return 返回当前调用Query
     */
    public Query notEqualCondition(Map<String, Object> notEquals) {
        for (Map.Entry<String, Object> entry : notEquals.entrySet()) {
            this.conditions.add(new Condition(ConditionType.NOT_EQUAL,
                    entry.getKey(),
                    CollectionUtil.toArray(entry.getValue())));
        }
        return this;
    }

    /**
     * 添加like条件
     *
     * @param key    like条件字段名
     * @param values like条件字段名对应的值
     * @return 返回当前调用Query
     */
    public Query likeCondition(String key, Object[] values) {
        this.conditions.add(new Condition(ConditionType.LIKE, key, values));
        return this;
    }

    /**
     * 多个值匹配某一个字段
     *
     * @param key    需要匹配的字段名称
     * @param values 所有匹配的值
     * @return 返回当前调用Query
     */
    public Query moreLikeCondition(String key, String[] values) {
        this.conditions.add(new Condition(ConditionType.MORE_LIKE_THIS, key, values));
        return this;
    }

    /**
     * 添加查询范围条件
     *
     * @param key  查询范围字段
     * @param from 从from字段值开始
     * @param to   到to字段值结束
     * @return 返回当前调用Query
     */
    public Query rangeCondition(String key, long from, long to) {
        this.conditions.add(new Condition(ConditionType.RANGE, key, CollectionUtil.toArray(from, to)));
        return this;
    }

    /**
     * 添加查询范围条件
     *
     * @param key  查询范围字段
     * @param from 从from字段值开始
     * @param to   到to字段值结束
     * @return 返回当前调用Query
     */
    public Query rangeCondition(String key, Object from, Object to) {
        this.conditions.add(new Condition(ConditionType.RANGE, key, CollectionUtil.toArray(from, to)));
        return this;
    }

    /**
     * 添加大于条件
     *
     * @param key  大于该字段的字段名称
     * @param from 大于from这个值
     * @return 返回当前调用Query
     */
    public Query greaterThanCondition(String key, Object from) {
        this.conditions.add(new Condition(ConditionType.GREATER_THAN, key, CollectionUtil.toArray(from)));
        return this;
    }

    /**
     * 添加大于等于条件
     *
     * @param key  大于等于该字段的字段名称
     * @param from 大于等于from这个值
     * @return 返回当前调用Query
     */
    public Query greaterThanOrEqualCondition(String key, Object from) {
        this.conditions.add(new Condition(ConditionType.GREATER_THAN_OR_EQUAL, key, CollectionUtil.toArray(from)));
        return this;
    }

    /**
     * 添加小于条件
     *
     * @param key  小于该字段的字段名称
     * @param from 小于from这个值
     * @return 返回当前调用Query
     */
    public Query lessThanCondition(String key, Object from) {
        this.conditions.add(new Condition(ConditionType.LESS_THAN, key, CollectionUtil.toArray(from)));
        return this;
    }

    /**
     * 添加小于等于条件
     *
     * @param key  小于等于该字段的字段名称
     * @param from 小于等于from这个值
     * @return 返回当前调用Query
     */
    public Query lessThanOrEqualCondition(String key, Object from) {
        this.conditions.add(new Condition(ConditionType.LESS_THAN_OR_EQUAL, key, CollectionUtil.toArray(from)));
        return this;
    }

    /**
     * 添加查询范围条件,查询为从from日期到to日期
     *
     * @param key  查询范围日期字段
     * @param from 从from日期开始
     * @param to   到to日期结束
     * @return 返回当前调用Query
     */
    public Query rangeCondition(String key, Date from, Date to) {
        this.conditions.add(new Condition(ConditionType.RANGE, key, CollectionUtil.toArray(from, to)));
        return this;
    }

    /**
     * 添加通配符查询条件
     *
     * @param key    通配符匹配的字段名称
     * @param values 通配符匹配的所有值
     * @return 返回当前调用Query
     */
    public Query wildcardCondition(String key, Object[] values) {
        this.conditions.add(new Condition(ConditionType.WILDCARD, key, values));
        return this;
    }

    /**
     * 添加模糊查询条件
     *
     * @param key   模糊查询字段名称
     * @param value 模糊查询值
     * @return 返回当前调用Query
     */
    public Query fuzzyCondition(String key, Object value) {
        this.conditions.add(new Condition(ConditionType.FUZZY, key, CollectionUtil.toArray(value)));
        return this;
    }

    /**
     * 添加QueryString查询条件
     *
     * @param key   QueryString字段名称
     * @param value QueryString查询值
     * @return 返回当前调用Query
     */
    public Query queryStringCondition(String key, Object value) {
        this.conditions.add(new Condition(ConditionType.QUERY_STRING, key, CollectionUtil.toArray(value)));
        return this;
    }


    public HighlightBuilder getHighlightBuilder() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String highlight : highlights) {
            highlightBuilder.field(highlight);
        }
        return highlightBuilder;
    }

    public boolean hasHighlightBuilder() {
        return highlights.size() != 0;
    }

    public Set<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(Set<String> highlights) {
        this.highlights = highlights;
    }

    public void addHighlight(String... highlightField) {
        for (String field : highlightField) {
            this.highlights.add(field);
        }
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public boolean isSearchAll() {
        return conditions.isEmpty();
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getSubAggregation() {
        return subAggregation;
    }

    public void setSubAggregation(String subAggregation) {
        this.subAggregation = subAggregation;
    }

    public SortOrder getAggregationSort() {
        return aggregationSort;
    }

    public void setAggregationSort(SortOrder aggregationSort) {
        this.aggregationSort = aggregationSort;
    }

    public void addSort(String field, SortOrder order) {
        this.sorts.add(new Sort(field, order));
    }

    public void addSort(String field, String order) {
        this.sorts.add(new Sort(field, order));
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(Duration scrollTime) {
        this.scrollTime = scrollTime;
    }
}
