package com.xin.utils.elasticsearch;

import org.elasticsearch.search.sort.SortOrder;

/**
 * @author Luchaoxin
 * @Description: 封装某个排序的字段和排序规则
 * @date 2018-08-25
 */

public class Sort {

    private String field;

    private SortOrder order;

    public Sort(String field) {
        this.field = field;
        this.order = SortOrder.DESC;
    }

    public Sort(String field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

    public Sort(String field, String order) {
        this.field = field;

        if (order.equalsIgnoreCase(SortOrder.ASC.toString())) {
            this.order = SortOrder.ASC;
        } else {
            this.order = SortOrder.DESC;
        }
    }

    public String getField() {
        return field;
    }

    public SortOrder getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "Sort{" +
                "field='" + field + '\'' +
                ", order=" + order +
                '}';
    }
}
