package com.xin.utils.kafka.consumer.factory;

import com.xin.utils.AssertUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.kafka.Constants;

import java.util.Properties;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 默认消费者工厂类
 * @date 11:24 2018-06-25
 **/

public class DefaultConsumerFactory<K, V> extends AbstractConsumerFactory<K, V> {

    @Override
    public Properties getConsumerProperties(String servers, String topics, String groupId) {
        AssertUtil.checkNotEmpty(servers, "KafkaConsumer 的[servers]不能为空或null!");
        AssertUtil.checkNotEmpty(topics, "KafkaConsumer 的[topics]不能为空或null!");
        AssertUtil.checkNotEmpty(groupId, "KafkaConsumer 的[groupId]不能为空或null!");
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("kafka.topics", topics);
        props.put("group.id", groupId);
        props.put("client.id", StringUtil.getUUID());
        props.put("enable.auto.commit", Constants.ENABLE_AUTO_COMMIT);
        props.put("enable.auto.commit", false);
        props.put("auto.offset.reset", "earliest");
        props.put("auto.commit.interval.ms", Constants.AUTO_COMMIT_INTERVAL_MS);
        props.put("key.deserializer", Constants.CONSUMER_KEY_DESERIALIZER);
        props.put("value.deserializer", Constants.CONSUMER_VALUE_DESERIALIZER);
        return props;
    }

}
