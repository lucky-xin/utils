package com.xin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 深度拷贝对象工具类
 * @date 2018年5月10日 下午2:21:26
 */
public class DeepCopyUtil {

    public static <T> T deepCopy(T src) {
        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream out = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream in = null;
        try {
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked") T dest = (T) in.readObject();
            return dest;
        } catch (Exception e) {
            throw new RuntimeException("深拷贝对象异常！" + e);
        } finally {
            close(byteOut);
            close(out);
            close(byteIn);
            close(in);
        }
    }

    private static void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
        }
    }
}
