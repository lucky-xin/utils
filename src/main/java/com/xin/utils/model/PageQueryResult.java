package com.xin.utils.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luchaoxin
 * @Description: 分页查询结果类
 * @date 2018-08-26
 */
public class PageQueryResult {

    /**
     * 查询结果所有数目
     */
    private long count;

    /**
     * 查询花费时间
     */
    private long useTime;

    /**
     * 查询结果所有数据
     */
    private List<Map<String, Object>> resultSet;

    private String queryId;

    public PageQueryResult(long count, List<Map<String, Object>> resultSet) {
        this.count = count;
        this.resultSet = resultSet;
    }

    public PageQueryResult(long count, long useTime, List<Map<String, Object>> resultSet) {
        this.count = count;
        this.useTime = useTime;
        this.resultSet = resultSet;
    }

    public PageQueryResult(String queryId, long count, long useTime, List<Map<String, Object>> resultSet) {
        this.queryId = queryId;
        this.count = count;
        this.useTime = useTime;
        this.resultSet = resultSet;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(0);
        map.put("count", count);
        map.put("useTime", useTime);
        map.put("records", resultSet);
        return map;
    }

    public List<Map<String, Object>> getResultSet() {
        return this.resultSet;
    }

    public long getTotalSize() {
        return this.count;
    }

    public long getUseTime() {
        return this.useTime;
    }

    public String getQueryId() {
        return queryId;
    }

    @Override
    public String toString() {
        return "PageQueryResult{" +
                "count=" + count +
                ", useTime=" + useTime +
                ", resultSet=" + resultSet +
                '}';
    }
}
