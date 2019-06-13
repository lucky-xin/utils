package com.xin.utils.redis;

import com.xin.utils.AssertUtil;
import com.xin.utils.CollectionUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: redis实现延时队列
 * @date 2018-08-05 20:36
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisDelayQueue {

    private Jedis client;

    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    public RedisDelayQueue(Jedis client) {
        AssertUtil.checkNotNull(client, "Jedis must not be null.");
        this.client = client;
    }

    public String take(String key, TimeUnit timeUnit) throws InterruptedException {

        int firstIndex = 0;
        try {
            lock.lock();
            while (true) {
                List<Tuple> tuples = new ArrayList<>(client.zrangeWithScores(key, firstIndex, firstIndex));

                if (CollectionUtil.isEmpty(tuples)) {
                    condition.await(2, TimeUnit.SECONDS);
                } else {
                    Tuple tuple = tuples.get(firstIndex);
                    String ele = tuple.getElement();
                    long delay = (long) tuple.getScore();

                    condition.await(delay, timeUnit);
                    long result = client.zrem(key, ele);
                    return result > 0 ? ele : null;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean add(String key, double score, String member) {
        long result = client.zadd(key, score, member);
        condition.signalAll();
        return result > 0;
    }

    public boolean add(String key, Map<String, Double> params) {
        long result = client.zadd(key, params);
        condition.signalAll();
        return result > 0;
    }

}
