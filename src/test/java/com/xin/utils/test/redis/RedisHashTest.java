package com.xin.utils.test.redis;

import com.xin.utils.redis.RedisClient;
import org.junit.Test;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: hash存储测试
 * @date 2018-08-05 21:48
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisHashTest {

    redis.clients.jedis.Jedis client = RedisClient.getJedis("192.168.50.130", 6379, "foobared");

    @Test
    public void testMethod9() {
        String hashKey = "HashKey";
        String value = "47448673";
        client.hset(hashKey, "field1", value);
        client.hset(hashKey, "field2", value);
        client.hset(hashKey, "field3", value);
        System.out.println(client.hget(hashKey, "field1"));
    }
}

