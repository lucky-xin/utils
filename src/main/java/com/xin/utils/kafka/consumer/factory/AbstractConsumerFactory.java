package com.xin.utils.kafka.consumer.factory;



import com.xin.utils.kafka.Constants;
import com.xin.utils.kafka.KeyGeneration;
import com.xin.utils.kafka.consumer.ConsumerCallback;
import com.xin.utils.kafka.consumer.CustomKafkaConsumer;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 默认消费者工厂类
 * @date 11:24 2018-06-25
 **/

public abstract class AbstractConsumerFactory<K, V> implements ConsumerFactory<K, V> {

    protected Map<String, CustomKafkaConsumer<K, V>> consumers = new ConcurrentHashMap<>();

    /**
     * 创建一个消费者，如果缓存之中已经存在该topics和groupId的对象则从缓存之中获取
     *
     * @param properties
     * @param callback
     * @return
     */
    @Override
    public CustomKafkaConsumer<K, V> createConsumer(Properties properties, ConsumerCallback callback) {
        String topics = properties.getProperty(Constants.KAFKA_CONFIG_TOPIC_NAME);
        String groupId = properties.getProperty(Constants.KAFKA_CONFIG_GROUP_ID_NAME);
        String key = KeyGeneration.getKey(this.hashCode(), topics, groupId);

        CustomKafkaConsumer<K, V> consumer = consumers.get(key);
        if (consumer == null) {
            synchronized (this) {
                consumer = consumers.get(key);
                if (consumer == null) {
                    consumer = new CustomKafkaConsumer<K, V>(properties, callback);
                    consumers.put(key, consumer);
                }
            }
        }
        return consumer;

    }

    @Override
    public void removeConsumer(String topics, String groupId) {
        String key = KeyGeneration.getKey(this.hashCode(), topics, groupId);
        CustomKafkaConsumer<K, V> consumer = consumers.get(key);
        if (null != consumer) {
            consumer.stop();
            consumers.remove(key);
        }
    }

    @Override
    public void removeAllConsumer() {
        Iterator<Map.Entry<String, CustomKafkaConsumer<K, V>>> iterator = consumers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CustomKafkaConsumer<K, V>> entry = iterator.next();
            CustomKafkaConsumer<K, V> consumer = entry.getValue();
            if (consumer != null) {
                consumer.stop();
            }
        }
        consumers.clear();
        Constants.LOGGER.debug("removeAllProducer succeed");
    }

}
