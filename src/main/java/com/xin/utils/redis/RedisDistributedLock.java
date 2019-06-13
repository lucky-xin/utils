package com.xin.utils.redis;

import com.xin.utils.AssertUtil;
import com.xin.utils.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: Redis实现分布式锁
 * @date 2018-08-06 13:30
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisDistributedLock {

    private Jedis client;

    private String successStatus = "OK";

    public RedisDistributedLock(Jedis jedis) {
        AssertUtil.checkNotNull(jedis, "Jedis must not be null.");
        this.client = jedis;
    }

    /**
     * 获取redis 分布式锁，如果能成功获取锁则返回一个uuid
     *
     * @param lockKey        redis 分布式锁名称
     * @param lockExpireTime redis 分布式锁过期时间，避免异常，没有释放锁而其他线程永远获取不到锁
     * @param acquireTimeout 获取锁等待超时时间
     * @param timeUnit       时间单位
     * @return 如果能成功获取锁则返回一个uuid
     */
    public String lock(String lockKey, long lockExpireTime, long acquireTimeout, TimeUnit timeUnit) {

        String uuid = StringUtil.getUUID();
        // 获取锁的超时时间，超过这个时间则放弃获取锁
        long end = System.currentTimeMillis() + timeUnit.toMillis(acquireTimeout);

        do {
            String result = client.set(lockKey, uuid, "NX", "PX", lockExpireTime);
            if (successStatus.equalsIgnoreCase(result)) {
                return uuid;
            }
        } while (System.currentTimeMillis() < end);
        return null;
    }

    /**
     * 删除分布式锁
     *
     * @param lockKey    锁的key
     * @param identifier 锁的值
     * @return 成功就返回true
     */
    public boolean unLock(String lockKey, String identifier) {

        if (StringUtil.isEmpty(lockKey)) {
            return false;
        }

        boolean retFlag = false;
        while (true) {
            /**
             * 监视lock，准备开始事务
             */
            client.watch(lockKey);
            /**
             * 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
             */
            String value = client.get(lockKey);

            if (null != value && value.equals(identifier)) {
                Transaction transaction = client.multi();
                transaction.del(lockKey);
                List<Object> results = transaction.exec();
                if (results == null) {
                    continue;
                }
                retFlag = true;
            }
            client.unwatch();
            break;
        }
        return retFlag;
    }

}
