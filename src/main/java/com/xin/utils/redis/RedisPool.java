package com.xin.utils.redis;

import redis.clients.jedis.Jedis;

/**
 * @author Luchaoxin
 * @Description: redis池接口
 * @date 2018-08-26 22:14
 */
public interface RedisPool {
    /**
     * 获取jedis
     *
     * @return 返回一个jedis对象
     */
    Jedis getResource();
}
