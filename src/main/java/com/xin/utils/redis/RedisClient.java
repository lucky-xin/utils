package com.xin.utils.redis;

import com.xin.utils.StringUtil;
import com.xin.utils.redis.impl.RedisPoolImpl;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: redis 操作工具类
 * @date 2018-06-26 19:42
 */
@Log4j
public class RedisClient {
    private static final Map<String, JedisPool> JEDIS_POOLS = new ConcurrentHashMap<>(50);
    public static final Properties CONFIG = new Properties();

    public static Jedis getJedis(String ip, int port) {
        return getJedis(ip, port, null);
    }

    public static Jedis getJedis(String ip, int port, String password) {
        JedisPool jedisPool = getJedisPool(ip, port, password);
        return jedisPool.getResource();
    }

    /**
     * 根据配置文件获取JedisPool
     *
     * @return
     * @throws RedisException
     */
    public static JedisPool getJedisPool() throws RedisException {
        if (CONFIG.isEmpty()) {
            try (InputStream in = RedisClient.class.getResourceAsStream("/config/redis.properties")) {
                CONFIG.load(in);
            } catch (Exception e) {
                log.info("读取redis配置文件异常", e);
                throw new RedisException("读取redis配置文件异常，请检查/config/redis.properties" + e.getMessage());
            }
        }

        return getJedisPool(CONFIG.getProperty("redis.servers"),
                StringUtil.toInteger(CONFIG.getProperty("redis.port")),
                CONFIG.getProperty("redis.password"));
    }

    public static JedisPool getJedisPool(String ip, int port, String password) {
        String key = ip + "_" + port;
        JedisPool jedisPool = JEDIS_POOLS.get(key);
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(30);
            //最大空闲数
            config.setMaxIdle(10);
            config.setMaxWaitMillis(10 * 1000);
            jedisPool = getJedisPool(config, ip, port, password);
        }
        return jedisPool;
    }

    public static Jedis getJedis(JedisPoolConfig config, String ip, int port, String password) {
        JedisPool jedisPool = getJedisPool(config, ip, port, password);
        return jedisPool.getResource();
    }


    public static JedisPool getJedisPool(JedisPoolConfig config, String ip, int port, String password) {
        RedisPoolImpl redisPool = new RedisPoolImpl(config, ip, port, password);
        return redisPool;
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
