package com.xin.utils.redis;

import com.xin.utils.AssertUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: redis阻塞队列
 * @date 2018-08-06 10:54
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisBlockingQueue {

    private Jedis client;

    public RedisBlockingQueue(Jedis client) {
        AssertUtil.checkNotNull(client, "Jedis must not be null.");
        this.client = client;
    }

    public List<String> take(String key, int timeout) {
        return client.blpop(timeout, key);
    }

    public long add(String key, String... values) {
        return client.lpush(key, values);
    }
}
