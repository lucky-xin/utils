package com.xin.utils.test.redis;

import com.xin.utils.redis.RedisClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: redis阻塞队列测试
 * @date 2018-08-05 21:45
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisBlockingQueueTest {

    redis.clients.jedis.Jedis client = RedisClient.getJedis("192.168.50.130", 6379, "foobared");

    @Test
    public void test1() {
        String key = "asyncQueueKey";
        //阻塞等待60s
        List<String> asyncQueue = client.blpop(60, key);
        System.out.println(asyncQueue);
    }

    @Test
    public void test2() {
        String key = "asyncQueueKey";
        client.rpush(key, "dsd", "djkj");
    }

    @Test
    public void test3() {
        List<String> lockObjects = new ArrayList<String>();
        String key = "";
        int index = Collections.binarySearch(lockObjects, key);
    }
}
