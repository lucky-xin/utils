package com.xin.utils.test.redis;

import com.xin.utils.StringUtil;
import com.xin.utils.redis.RedisClient;
import org.junit.Test;
import redis.clients.jedis.Transaction;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 分布式锁测试
 * @date 2018-08-05 21:55
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisDistributedLockTest {

    redis.clients.jedis.Jedis client = RedisClient.getJedis("192.168.50.130", 6379, "foobared");
    @Test
    public void test1() {
        String distributedKey = "DistributedKeyName";
        Transaction transaction = client.multi();
        transaction.setnx(distributedKey, StringUtil.getUUID());
        transaction.expire(distributedKey, 30);
        transaction.exec();
    }

    @Test
    public void test2() {
        System.out.println(client.set("DistributedKeyName", StringUtil.getUUID(), "NX", "PX", 10 * 1000));
    }


}
