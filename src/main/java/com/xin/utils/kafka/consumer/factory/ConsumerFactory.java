package com.xin.utils.kafka.consumer.factory;


import com.xin.utils.kafka.consumer.ConsumerCallback;
import com.xin.utils.kafka.consumer.CustomKafkaConsumer;

import java.util.Properties;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: kafka消费者工厂
 * @date 8:52 2018-06-25
 **/
public interface ConsumerFactory<K, V> {

    /**
     * 创建一个消费者，如果缓存之中已经存在该topics和groupId的对象则从缓存之中获取
     *
     * @param properties 创建kafka消费者配置信息
     * @param callback   kafka消费者消费消息回调接口
     * @return CustomKafkaConsumer
     * @author Luchaoxin
     */
    CustomKafkaConsumer<K, V> createConsumer(Properties properties, ConsumerCallback callback);

    /**
     * 获取 kafka消费者 如果在缓存中找到则返回该消费者
     *
     * @param servers  kafka集群服务地址
     * @param topics   kafka消费者消费topics
     * @param groupId  kafka消费者消费分组id
     * @param callback kafka消费者消费消息回调接口
     * @return CustomKafkaConsumer
     **/
    default CustomKafkaConsumer<K, V> createConsumer(String servers, String topics, String groupId, ConsumerCallback callback) {
        return this.createConsumer(getConsumerProperties(servers, topics, groupId), callback);
    }


    /**
     * 删除某一个消费者
     *
     * @param topic   kafka消费者消费topics
     * @param groupId kafka消费者消费分组id
     * @author Luchaoxin
     **/
    void removeConsumer(String topic, String groupId);

    /**
     * 删除所有kafka消费者
     *
     * @return CustomKafkaConsumer
     * @author Luchaoxin
     * @Date 17:35 2018-06-25
     **/
    void removeAllConsumer();


    /**
     * 获取消费者Properties配置文件
     *
     * @param servers 对应bootstrap.servers
     * @param topics  消费者主题
     * @param groupId 消费者组id
     * @return
     */
    Properties getConsumerProperties(String servers, String topics, String groupId);

}
