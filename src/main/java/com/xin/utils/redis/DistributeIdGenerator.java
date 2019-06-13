package com.xin.utils.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 分布式id生成工具
 * @date 2019-01-03 19:08
 */
public class DistributeIdGenerator {

    private JedisPool redisPool;

    public DistributeIdGenerator(JedisPool redisPool) {
        this.redisPool = redisPool;
    }

    public Long incrBy(String key, long value) {
        Jedis jedis = null;
        long ret = -1;
        try {
            jedis = redisPool.getResource();
            if (jedis == null) {
                return ret;
            }
            ret = jedis.incrBy(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }
}
