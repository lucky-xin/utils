package com.xin.utils.redis.impl;

import com.xin.utils.StringUtil;
import com.xin.utils.redis.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Luchaoxin
 * @Description: RedisPool实现类，为了拓展JedisPool
 * @date 2018-08-26 22:16
 */
public class RedisPoolImpl extends JedisPool implements RedisPool {

    private String password;

    public RedisPoolImpl(JedisPoolConfig config, String ip, int port, String password) {
        super(config, ip, port);
        this.password = password;
    }

    @Override
    public Jedis getResource() {
        Jedis jedis = super.getResource();
        if (!StringUtil.isEmpty(password)) {
            jedis.auth(password);
        }
        return jedis;
    }
}
