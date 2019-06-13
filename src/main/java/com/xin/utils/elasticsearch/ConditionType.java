package com.xin.utils.elasticsearch;

/**
 * @author Luchaoxin
 * @Description: 查询条件所有类型
 * @date 2018-08-25
 */
public enum ConditionType {
    /**
     * 相等条件
     **/
    EQUAL,
    /**
     * like条件
     **/
    LIKE,
    /**
     * 范围条件
     **/
    RANGE,

    /**
     * 通配符条件
     */
    WILDCARD,

    /**
     * 模糊查询
     */
    FUZZY,

    /**
     * 相等过滤条件
     */
    EQUAL_FILTER,

    /**
     * 范围过滤条件
     */
    RANGE_FILTER,

    /**
     * between过滤条件
     */
    BETWEEN_FILTER,

    /**
     * 通配符过滤条件
     */
    WILDCARD_FILTER,

    /**
     * 为QueryBuilders的queryStringQuery条件
     */
    QUERY_STRING,

    /**
     * 不相等条件
     **/
    NOT_EQUAL,

    /**
     * 大于条件
     **/
    GREATER_THAN,

    /**
     * 大于等于条件
     **/
    GREATER_THAN_OR_EQUAL,

    /**
     * 小于条件
     **/
    LESS_THAN,

    /**
     * 小于等于条件
     **/
    LESS_THAN_OR_EQUAL,

    /**
     * QueryBuilder条件
     **/
    QUERY_BUILDER,

    /**
     * 多个值匹配某一个字段
     */
    MORE_LIKE_THIS,

    /**
     * 短语匹配
     */
    MATCH_PHRASE,

    /**
     * termsQuery条件
     */
    TERMS_QUERY,

    /**
     * 添加matchQuery条件
     */
    MATCH_QUERY,
    /**
     * disMaxQuery条件
     */
    DIS_MAX_QUERY;
}
