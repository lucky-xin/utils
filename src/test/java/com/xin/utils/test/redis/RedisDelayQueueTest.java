package com.xin.utils.test.redis;

import com.xin.utils.redis.RedisClient;
import com.xin.utils.redis.RedisDelayQueue;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: redis实现延时队列
 * @date 2018-08-05 21:44
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisDelayQueueTest {

    redis.clients.jedis.Jedis client = RedisClient.getJedis("192.168.50.130", 6379, "foobared");

    @Test
    public void test2() {
        String key = "delayQueue";
        try {
            String result = new RedisDelayQueue(client).take(key, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
