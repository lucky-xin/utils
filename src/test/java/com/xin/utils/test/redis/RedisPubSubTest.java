package com.xin.utils.test.redis;

import com.xin.utils.redis.RedisClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: RedisPubSubTest
 * @date 2018-08-05 09:26
 * @Copyright (C)2018 , Luchaoxin
 */
public class RedisPubSubTest {

    redis.clients.jedis.Jedis jedis = RedisClient.getJedis("192.168.50.130", 6379, "foobared");

//    /*
//     * 测试订阅
//     */
//    @Test//订阅者1
//    public void test0() throws IOException {
//        MyJedisSub myJedisSub = new MyJedisSub();
//        jedis.subscribe(myJedisSub, "china-1", "china-2", "china-3");
//        System.in.read();//持续接收消息的状态，按回车线程才会结束。
//
//    }
//
//    @Test//订阅者2
//    public void test1() throws IOException {
//        MyJedisSub myJedisSub = new MyJedisSub();
//        jedis.subscribe(myJedisSub, "china-1", "china-2", "china-3");
//        System.in.read();
//    }
//
//
//    /*
//     * 测试发布消息
//     */
//    @Test//发布者1
//    public void test2(){
//        jedis.publish("china-1", "正在播放新闻联播");
//    }
//    @Test//发布者2
//    public void test3(){
//        jedis.publish("china-2", "正在播放射雕英雄传");
//    }
//    @Test//发布者3
//    public void test4(){
//        jedis.publish("china-3", "正在播放还珠格格");
//    }


}
