package com.xin.utils.io;

/**
 * @author Luchaoxin
 * @Description: IO流工具类
 * @date 2018-08-28 8:43
 */
public class IOUtil {

    public static void close(AutoCloseable closeable) {

        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }

    }
}
