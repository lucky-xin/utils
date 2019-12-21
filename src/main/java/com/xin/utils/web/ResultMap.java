package com.xin.utils.web;

import com.xin.utils.CollectionUtil;
import com.xin.utils.StringUtil;

import java.util.*;

/**
 * @author Luchaoxin
 * @Description: web数据查询，操作返回结果数据工具类
 * @date 2018-10-07 11:31
 */
public class ResultMap {

    public static final String STATUS_NAME = "status";

    public static final String MESSAGE_NAME = "message";

    public static final String DATA_NAME = "data";

    public static int SUCCEED_CODE = 1;

    public static int FAILED_CODE = -1;

    public static Map<String, Object> succeed(String msg) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(STATUS_NAME, SUCCEED_CODE);
        if (StringUtil.isEmpty(msg)) {
            msg = "操作成功";
        }
        map.put(MESSAGE_NAME, msg);
        return map;
    }

    public static Map<String, Object> failed(String msg) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(STATUS_NAME, FAILED_CODE);

        if (StringUtil.isEmpty(msg)) {
            msg = "操作失败";
        }
        map.put(MESSAGE_NAME, msg);
        map.put(DATA_NAME, new ArrayList<>(0));
        return map;
    }

    public static Map<String, Object> succeed(Map<String, Object> data) {
        return getResult(data);
    }

    private static <T> Map<String, Object> getResult(Object data) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(STATUS_NAME, SUCCEED_CODE);
        map.put(MESSAGE_NAME, "查询成功");
        map.put(DATA_NAME, data);
        return map;
    }

    public static <T> Map<String, Object> succeed(List<T> data) {
        return getResult(data);
    }

    public static <T> Map<String, Object> result(T object) {
        if (object == null) {
            return queryEmpty();
        }

        if (object instanceof String) {
            return succeed((String) object);
        }

        if (object instanceof Collection || object instanceof Map) {
            return getResult(object);
        }

        if (object instanceof Integer) {
            return result((Integer) object, null);
        }
        try {
            return getResult(object);
        } catch (Exception e) {
            return failed();
        }
    }

    private static Map<String, Object> queryEmpty() {
        Map<String, Object> map = new HashMap<>(3);
        map.put(STATUS_NAME, SUCCEED_CODE);
        map.put(MESSAGE_NAME, "查询成功,数据为空");
        map.put(DATA_NAME, new ArrayList<>(0));
        return map;
    }

    public static <T> Map<String, Object> result(List<T> data) {
        if (CollectionUtil.isEmpty(data)) {
            return queryEmpty();
        }
        return getResult(data);
    }

    public static <K, V> Map<String, Object> result(Map<K, V> data) {
        if (CollectionUtil.isEmpty(data) ) {
            return queryEmpty();
        }
        return getResult(data);
    }

    public static Map<String, Object> succeed() {
        return succeed("");
    }

    public static Map<String, Object> failed() {
        return failed(null);
    }

    public static Map<String, Object> result(int code, String msg) {
        if (code > 0) {
            return succeed(msg);
        }
        return failed(msg);
    }

    public static Map<String, Object> result(int code) {
        if (code > 0) {
            return succeed("");
        }
        return failed(null);
    }

    public static <T> Map<String, Object> page(Long total, Integer current, Integer size, List<T> data) {
        Map<String, Object> map = new HashMap<>(6);
        map.put(STATUS_NAME, SUCCEED_CODE);
        map.put(MESSAGE_NAME, "查询成功");
        map.put("total", total);
        map.put("current", current);
        map.put("size", size);
        map.put("records", data);
        return map;
    }

    public static Map<String, Object> failed(int code, String msg) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(STATUS_NAME, code);
        map.put(MESSAGE_NAME, msg);
        return map;
    }
}

