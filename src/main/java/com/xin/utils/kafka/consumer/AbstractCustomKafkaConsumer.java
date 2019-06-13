package com.xin.utils.kafka.consumer;

import com.xin.utils.AssertUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.kafka.Constants;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 自定义kafka消费者抽象类
 * @date 8:58 2018-06-25
 **/

public abstract class AbstractCustomKafkaConsumer<K, V> extends KafkaConsumer<K, V> implements CustomConsumer {

    public AbstractCustomKafkaConsumer(Properties properties) {
        super(properties);
        String topics = properties.getProperty(Constants.KAFKA_CONFIG_TOPIC_NAME);
        subscribe(Arrays.asList(topics.split(",")));
    }

    public AbstractCustomKafkaConsumer(String servers, String topics, String groupId) {
        super(getProperties(servers, topics, groupId));
    }

    protected static Properties getProperties(String servers, String topics, String groupId) {
        AssertUtil.checkNotEmpty(servers, "KafkaConsumer 的[servers]不能为空或null!");
        AssertUtil.checkNotEmpty(topics, "KafkaConsumer 的[topics]不能为空或null!");
        AssertUtil.checkNotEmpty(groupId, "KafkaConsumer 的[groupId]不能为空或null!");
        Properties props = new Properties();
        props.put("kafka.topics", topics);
        props.put("bootstrap.servers", servers);
        props.put("group.id", groupId);
        props.put("client.id", StringUtil.getUUID());
        props.put("enable.auto.commit", Constants.ENABLE_AUTO_COMMIT);
        props.put("auto.commit.interval.ms", Constants.AUTO_COMMIT_INTERVAL_MS);
        props.put("key.deserializer", Constants.CONSUMER_KEY_DESERIALIZER);
        props.put("value.deserializer", Constants.CONSUMER_VALUE_DESERIALIZER);
        return props;
    }
}
