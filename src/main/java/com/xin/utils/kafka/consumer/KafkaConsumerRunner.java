package com.xin.utils.kafka.consumer;

import com.xin.utils.AssertUtil;
import com.xin.utils.kafka.Constants;
import com.xin.utils.kafka.KafkaException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 消费者监听线程
 * @date 11:53 2018-06-26
 **/
public class KafkaConsumerRunner<K, V> implements Runnable {

    /**
     * config.kafka 消费者
     */
    private Consumer<K, V> consumer;

    /**
     * config.kafka 消费者拉取消息超时时间
     */
    private Duration timeout = Duration.ofSeconds(5);

    /**
     * config.kafka 消费者消费消息空回调接口
     */
    private ConsumerCallback handler = new DefaultConsumerCallback();

    /**
     * config.kafka 消费者消费关闭状态
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * @param consumer config.kafka 消费者
     * @param callback config.kafka 消费者消费消息回调接口
     */
    public KafkaConsumerRunner(Consumer<K, V> consumer, ConsumerCallback callback) {
        this(consumer, callback, 5000L);
    }

    /**
     * @param consumer config.kafka 消费者
     * @param callback config.kafka 消费者消费消息回调接口
     * @param seconds  config.kafka 消费者拉取消息超时时间
     */
    public KafkaConsumerRunner(Consumer<K, V> consumer, ConsumerCallback callback, long seconds) {
        AssertUtil.checkNotNull(consumer, "Consumer must not be null.");
        AssertUtil.checkNotNull(callback, "MessageHandler must not be null.");
        this.consumer = consumer;
        this.handler = callback;
        this.timeout = Duration.ofSeconds(seconds);
    }

    /**
     * 停止当前线程并关闭消费者
     */
    public void shutdown() {
        closed.compareAndSet(false, true);
        consumer.wakeup();
    }

    /**
     * config.kafka 消费者拉取消息然后进行消费
     */
    @Override
    public void run() {
        try {
            while (!closed.get()) {
                ConsumerRecords<K, V> records = consumer.poll(timeout);
                for (ConsumerRecord<K, V> record : records) {
                    try {
                        handler.handle(record);
                    } catch (KafkaException e) {
                        Constants.LOGGER.error("MessageHandler 处理消息异常", e);
                    } catch (Exception e) {
                        Constants.LOGGER.error("MessageHandler 处理消息未知异常", e);
                    } catch (Throwable e) {
                        Constants.LOGGER.error("MessageHandler 处理消息未知异常", e);
                    }
                }
            }
        } catch (WakeupException e) {
            Constants.LOGGER.error("Consumer 关闭或其他异常", e);
        } catch (Exception e) {
            Constants.LOGGER.error("KafkaConsumerRunner 线程未知异常", e);
        } catch (Throwable e) {
            Constants.LOGGER.error("KafkaConsumerRunner 线程未知异常", e);
        } finally {
            closed.compareAndSet(false, true);
            consumer.close();
        }
    }
}
