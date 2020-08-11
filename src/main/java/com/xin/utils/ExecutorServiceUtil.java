package com.xin.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 线程池工具类
 * @date 2018-12-14 8:28
 */
public class ExecutorServiceUtil {

    static int availableProcessors = Runtime.getRuntime().availableProcessors();

    private static ExecutorService executorService = new ThreadPoolExecutor(
            availableProcessors * 2,
            availableProcessors * 10,
            2L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(20000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return executorService.submit(callable);
    }
}
