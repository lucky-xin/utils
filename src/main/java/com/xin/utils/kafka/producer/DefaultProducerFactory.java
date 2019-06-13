package com.xin.utils.kafka.producer;

import com.xin.utils.AssertUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.kafka.Constants;

import java.util.Properties;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 默认生产者工厂类
 * @date 15:23 2018-06-26
 **/
public class DefaultProducerFactory<K, V> extends AbstractProducerFactory<K, V> {

    @Override
    protected  Properties getProducerProperties(String servers) {
        AssertUtil.checkNotEmpty(servers, "KafkaConsumer 的[servers]不能为空或null!");
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("key.serializer", Constants.PRODUCER_KEY_SERIALIZER);
        props.put("value.serializer", Constants.PRODUCER_VALUE_SERIALIZER);
        props.put("acks", "all");
        props.put("client.id", StringUtil.getUUID());
        props.put("retries", 1);
        return props;
    }


}
