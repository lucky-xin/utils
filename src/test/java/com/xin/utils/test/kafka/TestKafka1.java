package com.xin.utils.test.kafka;

import com.xin.utils.kafka.KafkaException;
import com.xin.utils.kafka.KafkaClient;
import com.xin.utils.kafka.consumer.ConsumerCallback;
import com.xin.utils.kafka.consumer.CustomKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

public class TestKafka1 {

    private static String topics = "KAFKA_LEARNING";

    private static String groupId = "Luchaoxin";

    private static String groupId1 = "Luchaoxin1";


    public static void main(String[] args) throws Exception {
        System.setProperty("APP_HOME", "E:\\develop\\learn");

        KafkaClient client = KafkaClient.getInstance();

        String message = "Hello, luchaoxin2018";
        KafkaProducer<String, String> producer = client.createProducer(topics);
        String messageKey = "hello2018";
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topics, messageKey, message);

        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                System.out.println(recordMetadata);
            }
        });

        ConsumerCallback handler = new ConsumerCallback() {
            @Override
            public <K, V> void handle(ConsumerRecord<K, V> record) throws KafkaException {
                System.out.println("消费者收到消息：" + record.value());
            }
        };

        CustomKafkaConsumer consumer = client.createConsumer(topics, groupId, handler);
        consumer.start();
//        Thread.sleep(5000L);


        ConsumerCallback handler1 = new ConsumerCallback() {
            @Override
            public <K, V> void handle(ConsumerRecord<K, V> record) throws KafkaException {
                System.out.println("消费者1收到消息：" + record.value());
            }
        };
//        CustomKafkaConsumer consumer1 = client.createConsumer(topics, groupId1, handler1);
//        consumer1.start();
//        producer.close();
//        client.removeConsumer(handler.hashCode(), topics, groupId);
    }

    @Test
    public void test1() {

    }

}
