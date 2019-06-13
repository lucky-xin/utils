package com.xin.utils.kafka.producer;


import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: kafka生产者工厂接口
 * @date 15:23 2018-06-26
 **/
public interface ProducerFactory<K, V> {

    /**
     * 根据服务地址servers创建一个生产者
     * @param servers kafka集群服务地址
     * @return KafkaProducer
     * @author Luchaoxin
     **/
    KafkaProducer<K, V> createProducer(String servers);

    /**
     * 根据resource/config/kafka.properties文件配置信息创建一个生产者
     * @return KafkaProducer
     * @author Luchaoxin
     **/
    KafkaProducer<K, V> createProducer();

    /**
     * 根据properties配置信息创建一个生产者
     * @param properties kafka生产者配置消息
     * @return KafkaProducer
     * @author Luchaoxin
     **/
    KafkaProducer<K, V> createProducer(Properties properties);

    /**
     * 从缓存之中删除生产者
     *
     * @param servers kafka服务地址
     * @return KafkaProducer
     * @author Luchaoxin
     **/
    KafkaProducer<K, V> removeProducer(String servers);

    /**
     * 删除所有生产者
     **/
    void removeAllProducer();

}
