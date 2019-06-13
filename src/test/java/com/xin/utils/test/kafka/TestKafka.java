package com.xin.utils.test.kafka;

import com.xin.utils.kafka.KafkaException;
import com.xin.utils.kafka.KafkaClient;
import com.xin.utils.kafka.consumer.ConsumerCallback;
import com.xin.utils.kafka.consumer.CustomKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

public class TestKafka {

    private static String zkConnect = "192.168.50.129:9092";

    private static String topics = "LOVE_XIN";

    private static String groupId = "Luchaoxin1";

    private static String groupId1 = "Luchaoxin1";


    public static void main(String[] args) throws Exception {
        KafkaClient client = KafkaClient.getInstance();

        //创建生产者并生产消息,使用kafka.properties配置消息创建
        String topics = "KAFKA_LEARNING";
        KafkaProducer producer = client.createProducer();
        String key = "key_2018";
        String msg = "发送消息测试";
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topics, key, msg);
        producer.send(record);

        //创建消费者，消费消息
        CustomKafkaConsumer consumer = client.createConsumer("chaoxin", new ConsumerCallback() {
            @Override
            public <K, V> void handle(ConsumerRecord<K, V> record) throws KafkaException {
                System.err.println(record.toString());
            }
        });
        consumer.start();
    }

    @Test
    public void test1() {

    }

}
