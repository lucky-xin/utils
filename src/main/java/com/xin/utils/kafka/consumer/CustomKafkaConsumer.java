package com.xin.utils.kafka.consumer;


import com.xin.utils.AssertUtil;
import com.xin.utils.CollectionUtil;
import com.xin.utils.kafka.Constants;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 自定义kafka消费者
 * @date 17:55 2018-06-25
 **/
public class CustomKafkaConsumer<K, V> extends AbstractCustomKafkaConsumer<K, V> {

    /**
     * 消费者所消费的主题
     */
    private String topics;

    /**
     * 消费者所在的组id
     */
    private String groupId;

    /**
     * config.kafka 服务地址
     */
    private String servers;

    /**
     * 消费者消费消息线程池
     */
    private ExecutorService threadPool;

    /**
     * 消费者消费消息线程
     */
    private KafkaConsumerRunner<K, V> runner;

    /**
     * 消费者是否还在运行状态
     */
    private static AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 构造器
     *
     * @param servers config.kafka 服务地址
     * @param topics  消费者所消费的主题
     * @param groupId 消费者所在的组id
     * @param handler 消费者消费消息回调接口
     */
    public CustomKafkaConsumer(String servers, String topics, String groupId, ConsumerCallback handler) {
        this(getProperties(servers, topics, groupId), handler);
    }

    /**
     * 构造器
     *
     * @param properties config.kafka 消费者配置消息
     */
    public CustomKafkaConsumer(Properties properties, ConsumerCallback callback) {
        super(properties);
        this.topics = properties.getProperty(Constants.KAFKA_CONFIG_TOPIC_NAME);
        this.groupId = properties.getProperty(Constants.KAFKA_CONFIG_GROUP_ID_NAME);
        this.servers = properties.getProperty(Constants.KAFKA_CONFIG_SERVERS_NAME);

        AssertUtil.checkNotEmpty(topics, "topics must not be empty!");
        AssertUtil.checkNotNull(callback, "KafkaConsumer 的消息处理[handler]不能为null!");
        /**
         * 获取分区数目,然后使用和分数数相同的线程消费消息，多出都会没有消费消息，会浪费资源
         */
        Duration duration = Duration.ofSeconds(2);
        int partitions = CollectionUtil.size(partitionsFor(topics, duration));

        ThreadFactory threadFactory = (r) -> new Thread(r, "kafka消费者线程");

        threadPool = new ThreadPoolExecutor(partitions,
                partitions,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);

        runner = new KafkaConsumerRunner<K, V>(this, callback);
        Constants.LOGGER.info("create consumer succeed." + getConsumerInfo());
    }


    @Override
    public void start() {
        if (isRunning()) {
            Constants.LOGGER.debug("Consumer has been close." + getConsumerInfo());
            return;
        }
        isRunning.compareAndSet(false, true);
        threadPool.submit(runner);
        Constants.LOGGER.info("start consumer succeed." + getConsumerInfo());
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }


    @Override
    public void stop() {
        if (!isRunning()) {
            Constants.LOGGER.debug("Consumer has been close." + getConsumerInfo());
            return;
        }
        try {
            isRunning.compareAndSet(true, false);
            runner.shutdown();
            threadPool.shutdown();
            threadPool.awaitTermination(5000, TimeUnit.MILLISECONDS);
            threadPool.shutdownNow();
            Constants.LOGGER.info("close consumer succeed." + getConsumerInfo());
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Constants.LOGGER.error("close consumer error.", e);
        }
    }

    public String getTopics() {
        return topics;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getServers() {
        return servers;
    }

    public String getConsumerInfo() {
        StringBuilder info = new StringBuilder("  consumer info [");
        info.append(" servers:")
                .append(getServers())
                .append(" topics:")
                .append(getTopics())
                .append(" groupId:")
                .append(getGroupId())
                .append("]");
        return info.toString();
    }
}
