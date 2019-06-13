package com.xin.utils.kafka;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: Kafka 异常
 * @date 7:51 2018-06-25
 **/
public class KafkaException extends Exception {

    public KafkaException(String errorMessage) {
        super(errorMessage);
    }
}
