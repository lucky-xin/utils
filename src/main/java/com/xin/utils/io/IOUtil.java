package com.xin.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

    public static boolean delete(String path) {
        try {
            File file = new File(path);
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] convertInputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = null;
        try {
            buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] byteArray = buffer.toByteArray();
            return byteArray;
        } finally {
            close(buffer);
        }
    }
}
