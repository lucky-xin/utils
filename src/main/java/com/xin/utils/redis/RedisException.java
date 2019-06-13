package com.xin.utils.redis;

/**
 * @author Luchaoxin
 * @Description: redis异常
 * @date 2018-08-26 21:57
 */
public class RedisException extends Exception {

    public RedisException(String errorMsg) {
        super(errorMsg);
    }
}
