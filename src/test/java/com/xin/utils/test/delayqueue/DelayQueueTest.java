package com.xin.utils.test.delayqueue;

import com.xin.utils.redis.RedisClient;
import org.junit.Test;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 延时队列学习
 * @date 2018-08-05 18:02
 * @Copyright (C)2018 , Luchaoxin
 */
public class DelayQueueTest {

    redis.clients.jedis.Jedis client = RedisClient.getJedis("192.168.50.130", 6379, "foobared");

    DelayQueue<Element> delayQueue = new DelayQueue<>();
    private List<Tuple> tuple;

    @Test
    public void testMethod1() {
        Element element1 = new Element(10000L, TimeUnit.MILLISECONDS);

        Element element2 = new Element(4000L, TimeUnit.MILLISECONDS);

        Element element3 = new Element(5000L, TimeUnit.MILLISECONDS);
        delayQueue.add(element1);
        delayQueue.add(element2);
        delayQueue.add(element3);
    }

    @Test
    public void testMethod2() {
        try {
            testMethod1();
            Element element1 = delayQueue.take();
            System.out.println(element1);

            Element element2 = delayQueue.take();
            System.out.println(element2);

            Element element3 = delayQueue.take();
            System.out.println(element3);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        String key = "delayQueue";
        client.zadd(key, 2, "element1");
        client.zadd(key, 3, "element2");
        client.zadd(key, 4, "element3");
        client.zadd(key, 5, "element4");
        client.zadd(key, 6, "element5");
        Set<String> elements = client.zrange(key, 0, 10);
        System.out.println(elements);
    }
}
