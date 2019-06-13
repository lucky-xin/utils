package com.xin.utils;

import java.util.concurrent.*;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 创建一个线程池，统一管理
 * @date 2018年5月10日 下午2:21:26
 */
public class ExecutorServiceUtil {

    private static ThreadFactory threadFactory = new ThreadFactory() {
        int count = 0;

        @Override
        public Thread newThread(Runnable target) {

            return new Thread(target, "ExecutorServiceUtil_Thread_" + count++);
        }
    };

    private static ExecutorService service = new ThreadPoolExecutor(0, 10000, 10L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), threadFactory);

    public static void execute(Runnable task) {
        service.execute(task);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return service.submit(callable);
    }

    public static void shutdown(ExecutorService service) {
        if (null == service || service.isTerminated()) {
            return;
        }

        try {
            service.shutdown();
            if (!service.awaitTermination(2, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }

}
