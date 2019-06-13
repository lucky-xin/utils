package com.xin.utils.kafka;


import com.xin.utils.DeepCopyUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.kafka.consumer.ConsumerCallback;
import com.xin.utils.kafka.consumer.CustomKafkaConsumer;
import com.xin.utils.kafka.consumer.factory.ConsumerFactory;
import com.xin.utils.kafka.consumer.factory.DefaultConsumerFactory;
import com.xin.utils.kafka.producer.DefaultProducerFactory;
import com.xin.utils.kafka.producer.ProducerFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.zookeeper.common.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: Kafka 工具类，用于创建生产者和消费者
 * @date 7:51 2018-06-25
 **/
public class KafkaClient {

    /**
     * KafkaClient 单例对象
     */
    private static KafkaClient kafkaClient;

    /**
     * kafka配置
     */
    private Properties properties = new Properties();

    /**
     * kafka消费者工厂
     */
    private ConsumerFactory<String, String> consumerFactory = new DefaultConsumerFactory<String, String>();

    /**
     * kafka生产者工厂
     */
    private ProducerFactory<String, String> producerFactory = new DefaultProducerFactory<String, String>();

    /**
     * 此构造器一定要在resource/config.kafka/config.kafka-config.properties配置kafka
     * 否则会出现异常
     *
     * @throws KafkaException
     */
    private KafkaClient() {
        try {
            initConfig();
        } catch (KafkaException e) {
            throw new IllegalAccessError("kafka 初始化错误" + e.getMessage());
        }
    }

    /**
     * 双重检查+锁 实现单列，初始化失败直接抛出异常
     *
     * @return ElasticSearch对象
     * @throws IOException
     */
    public static KafkaClient getInstance() {
        if (kafkaClient == null) {
            synchronized (KafkaClient.class) {
                if (kafkaClient == null) {
                    kafkaClient = new KafkaClient();
                }
            }
        }
        return kafkaClient;
    }

    /**
     * 获取kafka配置文件,把配置文件放在resource/kafka目录下面名字为kafka-config.properties
     *
     * @throws KafkaException
     */
    private void initConfig() throws KafkaException {
        InputStream in = null;
        try {
            in = KafkaClient.class.getResourceAsStream("/config/kafka.properties");
            properties.load(in);
            Constants.LOGGER.debug("properties config info:" + properties);
        } catch (FileNotFoundException e) {
            Constants.LOGGER.error("config.kafka config file not found error.", e);
            throw new KafkaException("config.kafka config file not found error." + e.getMessage());
        } catch (IOException e) {
            Constants.LOGGER.error("config.kafka properties load error.", e);
            throw new KafkaException("config.kafka properties load error." + e.getMessage());
        } catch (Exception e) {
            Constants.LOGGER.error("config.kafka init error.", e);
            throw new KafkaException("config.kafka init error." + e.getMessage());
        } finally {
            IOUtils.closeStream(in);
        }

    }

    /**
     * 创建一个消费者
     *
     * @param groupId 消费者所在组id
     * @return 返回一个kafka消费者对象
     */
    public CustomKafkaConsumer<String, String> createConsumer(String groupId) {
        return consumerFactory.createConsumer(getProperties(null, null, groupId), null);
    }

    /**
     * 创建一个消费者
     *
     * @param groupId  消费者所在组id
     * @param callback 消费者消费消息回调接口
     * @return 返回一个kafka消费者对象
     */
    public CustomKafkaConsumer<String, String> createConsumer(String groupId, ConsumerCallback callback) {
        return consumerFactory.createConsumer(getProperties(null, null, groupId), callback);
    }

    /**
     * 创建一个消费者
     *
     * @param topics   消费者主题
     * @param groupId  消费者所在组id
     * @param callback 消费者消费消息回调接口
     * @return 返回一个kafka消费者对象
     */
    public CustomKafkaConsumer<String, String> createConsumer(String topics, String groupId, ConsumerCallback callback) {
        return consumerFactory.createConsumer(getProperties(null, topics, groupId), callback);
    }

    /**
     * 创建一个kafka生产者对象
     *
     * @param servers kafka服务ip和端口
     * @return 返回一个kafka生产者对象
     */
    public KafkaProducer<String, String> createProducer(String servers) {
        return producerFactory.createProducer(getProperties(servers, null, null));
    }

    /**
     * 创建一个kafka生产者对象
     *
     * @return 返回一个kafka生产者对象
     */
    public KafkaProducer<String, String> createProducer() {
        return producerFactory.createProducer(getProperties());
    }

    /**
     * 删除缓存之中某个消费者
     *
     * @param topic   消费者主题
     * @param groupId 消费者组id
     */
    public void removeConsumer(String topic, String groupId) {
        consumerFactory.removeConsumer(topic, groupId);
    }

    /**
     * 删除缓存之中所有消费者
     */
    public void removeAllConsumer() {
        consumerFactory.removeAllConsumer();
    }

    /**
     * 删除缓存之中某个生产者者
     *
     * @param servers 生产者服务地址
     */
    public KafkaProducer<String, String> removeProducer(String servers) {
        return producerFactory.removeProducer(servers);
    }

    /**
     * 删除缓存之中所有生产者者
     */
    public void removeAllProducer() {
        producerFactory.removeAllProducer();
    }

    public void close() {
        consumerFactory.removeAllConsumer();
        producerFactory.removeAllProducer();
    }

    /**
     * 使用深度拷贝拷贝配置Properties,这样在多线程环境之下，配置文件永远都不会改变
     * 每个线程获取的只是一个拷贝对象
     *
     * @param servers config.kafka 服务地址
     * @param topics  主题
     * @param groupId 组id
     * @return Properties
     */
    private Properties getProperties(String servers, String topics, String groupId) {
        Properties properties = DeepCopyUtil.deepCopy(getProperties());

        if (!StringUtil.isEmpty(servers)) {
            properties.put("bootstrap.servers", servers);
        }

        if (!StringUtil.isEmpty(topics)) {
            properties.put("kafka.topics", topics);
        }

        if (!StringUtil.isEmpty(groupId)) {
            properties.put("group.id", groupId);
        }

        return properties;
    }

    public Properties getProperties() {
        return properties;
    }
}
