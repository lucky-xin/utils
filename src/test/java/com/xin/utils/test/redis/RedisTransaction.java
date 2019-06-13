package com.xin.utils.test.redis;

import com.xin.utils.redis.RedisClient;
import org.junit.Test;
import redis.clients.jedis.Transaction;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: transaction
 * @date 2018-08-05 09:50
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisTransaction {
    redis.clients.jedis.Jedis jedis = RedisClient.getJedis("192.168.50.130", 6379, "foobared");

    @Test
    public void testMethod1() {
//        jedis.watch()
        Transaction transaction = jedis.multi();
        transaction.exec();
    }
}
