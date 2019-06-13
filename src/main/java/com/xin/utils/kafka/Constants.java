package com.xin.utils.kafka;

import com.xin.utils.log.LogFactory;
import org.apache.log4j.Logger;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: Kafka 常量类
 * @date 7:51 2018-06-25
 **/
public class Constants {
    /**
     * 自动提交
     */
    public static final boolean ENABLE_AUTO_COMMIT = true;

    /**
     * 自动提交超时时间
     */
    public static final int AUTO_COMMIT_INTERVAL_MS = 5000;

    /**
     * kafka配置文件名
     */
    public static final String KAFKA_CONFIG_FILE_NAME = "config/kafka.properties";

    /**
     * kafka主题名称key
     */
    public static final String KAFKA_CONFIG_TOPIC_NAME = "kafka.topics";

    /**
     * kafka组id的key
     */
    public static final String KAFKA_CONFIG_GROUP_ID_NAME = "group.id";

    /**
     * kafka组服务地址key
     */
    public static final String KAFKA_CONFIG_SERVERS_NAME = "bootstrap.servers";

    /**
     * config.kafka 消费者 key 反序列化器
     */
    public static final String CONSUMER_KEY_DESERIALIZER = "org.apache.config.kafka.common.serialization.StringDeserializer";

    /**
     * config.kafka 消费者 value 反序列化器
     */
    public static final String CONSUMER_VALUE_DESERIALIZER = "org.apache.config.kafka.common.serialization.StringDeserializer";

    /**
     * config.kafka 生产者 key 反序列化器
     */
    public static final String PRODUCER_KEY_SERIALIZER = "org.apache.config.kafka.common.serialization.StringSerializer";
    /**
     * config.kafka 生产者 value 反序列化器
     */
    public static final String PRODUCER_VALUE_SERIALIZER = "org.apache.config.kafka.common.serialization.StringSerializer";

    /**
     * kafka 日志打印
     */
    public static final Logger LOGGER = LogFactory.getLogger("kafka");
}
