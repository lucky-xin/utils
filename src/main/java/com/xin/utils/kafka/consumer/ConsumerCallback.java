package com.xin.utils.kafka.consumer;


import com.xin.utils.kafka.KafkaException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 自定义消费者消费消息回调
 * @date 18:05 2018-06-25
 **/
public interface ConsumerCallback {
    /**
     * 自定义消费者消费消息回调方法
     *
     * @param record
     * @param <K>
     * @param <V>
     * @throws KafkaException
     */
    <K, V> void handle(ConsumerRecord<K, V> record) throws KafkaException;
}
