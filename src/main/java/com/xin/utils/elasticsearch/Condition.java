package com.xin.utils.elasticsearch;

import com.xin.utils.AssertUtil;
import com.xin.utils.CollectionUtil;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author Luchaoxin
 * @Description: elasticsearch查询条件
 * @date 2018-08-25
 */
public class Condition {

    /**
     * 条件类型
     */
    private ConditionType conditionType;

    /**
     * 对该字段添加条件
     */
    private String[] fields = null;

    /**
     * 包裹查询, 高于设定分数
     */
    private float boost = 1.0F;

    /**
     * 当前条件字段所匹配的所有值
     */
    private Object[] values;

    public Condition(ConditionType conditionType, QueryBuilder queryBuilder) {
        AssertUtil.checkNotNull(conditionType, "ConditionType must not be null.");
        AssertUtil.checkNotNull(conditionType, "QueryBuilder must not be null.");
        init(conditionType, null, new Object[]{queryBuilder});
    }

    public Condition(ConditionType conditionType, String field, Object[] values) {
        AssertUtil.checkNotNull(conditionType, "ConditionType must not be null.");
        AssertUtil.checkNotEmpty(field, "field must not be empty.");
        AssertUtil.checkNotEmpty(values, "values must not be empty.");
        init(conditionType, new String[]{field}, values);
    }

    public Condition(ConditionType conditionType, float boost, String key, Object value) {
        this(conditionType, boost, CollectionUtil.toArray(key), CollectionUtil.toArray(value));
    }

    public Condition(ConditionType conditionType, float boost, String[] fields, Object[] values) {
        AssertUtil.checkNotNull(conditionType, "ConditionType must not be null.");
        AssertUtil.checkNotEmpty(values, "values must not be empty.");
        AssertUtil.checkNotEmpty(fields, "fields must not be empty.");
        init(conditionType, fields, values);
        this.boost = boost;
    }

    public void init(ConditionType conditionType, String[] keys, Object[] values) {
        this.conditionType = conditionType;
        this.fields = keys;
        this.values = values;
    }


    public ConditionType getConditionType() {
        return conditionType;
    }

    public String getField() {
        return fields[0];
    }

    public String[] getFields() {
        return fields;
    }

    public float getBoost() {
        return boost;
    }

    public Object[] getValues() {
        return values;
    }

    public Condition setBoost(float boost) {
        this.boost = boost;
        return this;
    }
}
