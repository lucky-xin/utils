package com.xin.utils;

import com.xin.utils.redis.RedisClient;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class HttpTest {


    @Test
    public void test() {
        Jedis jedis = RedisClient.getJedis("192.168.5.20", 6379, "Data*2019*");
        Long sceneId = 1L;
        Long stategyId = 2L;
        String userId;
        int stategyCount = 5;
        int userCount = 500 * 10000;
        for (int i = 0; i < stategyCount; i++) {
            for (int i1 = 0; i1 < userCount; i1++) {
                String field = String.format("%d:%d", i, i1);
                Map<String, String> data = new HashMap<String, String>() {{
                    put(field, StringUtil.getUUID());
                }};
                System.out.println(data);
                jedis.hmset("1", data);
            }
        }

    }
}
