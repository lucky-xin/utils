package com.xin.utils.kafka.producer;



import com.xin.utils.StringUtil;
import com.xin.utils.kafka.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 默认生产者工厂类
 * @date 15:23 2018-06-26
 **/
public abstract class AbstractProducerFactory<K, V> implements ProducerFactory<K, V> {

    private Map<String, KafkaProducer<K, V>> producers = new ConcurrentHashMap<>();

    @Override
    public KafkaProducer<K, V> createProducer(String servers) {
        return createProducer(getProducerProperties(servers));
    }

    @Override
    public KafkaProducer<K, V> createProducer() {
        return createProducer(getProducerProperties(null));
    }

    @Override
    public KafkaProducer<K, V> createProducer(Properties properties) {
        String key = StringUtil.toString(properties.getProperty("bootstrap.servers"));
        KafkaProducer<K, V> producer = producers.get(key);
        if (producer == null) {
            producer = new KafkaProducer<K, V>(properties);
            producers.putIfAbsent(key, producer);
        }
        return producer;
    }

    @Override
    public KafkaProducer removeProducer(String servers) {
        return producers.remove(servers);
    }

    @Override
    public void removeAllProducer() {
        Iterator<Map.Entry<String, KafkaProducer<K, V>>> iterator = producers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, KafkaProducer<K, V>> entry = iterator.next();
            KafkaProducer<K, V> consumer = entry.getValue();
            if (consumer != null) {
                consumer.close();
            }
        }
        producers.clear();
        Constants.LOGGER.debug("removeAllProducer succeed");
    }

    /**
     * 获取配置信息
     *
     * @param servers
     * @return
     */
    protected abstract Properties getProducerProperties(String servers);

}
