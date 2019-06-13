package com.xin.utils.kafka.consumer;

import kafka.common.KafkaException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 默认kafka消费者消费消息回调空接口
 * @date 11:24 2018-06-25
 **/
public class DefaultConsumerCallback implements ConsumerCallback {

    @Override
    public <K, V> void handle(ConsumerRecord<K, V> record) throws KafkaException {

    }
}
