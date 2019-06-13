package com.xin.utils.kafka.consumer;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: kafka消费者拓展接口
 * @date 9:01 2018-06-25
 **/
public interface CustomConsumer {
    /**
     * 启动消费者
     */
    void start();

    /**
     * 停止消费者
     */
    void stop();

    /**
     * 消费者是否还在运行之中
     *
     * @return 返回true表示正在运行，否则不在运行
     */
    boolean isRunning();
}
