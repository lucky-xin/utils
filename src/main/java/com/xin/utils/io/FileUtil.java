package com.xin.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 文件操作工具类
 * @date 7:47 2018-06-25
 **/

public class FileUtil {

    private static String charset = "UTF-8";

    public static String getCharset() {
        return charset;
    }

    /**
     * 读取jar包之中的文件
     *
     * @return
     * @author Luchaoxin
     * @Param
     **/
    public static InputStream getJarFileInputStream(String jarFilePath, String entryPath) throws IOException {
        File file = new File(jarFilePath);
        String absolutePath = file.getAbsolutePath();
        @SuppressWarnings("resource")
        JarFile jarFile = new JarFile(absolutePath);
        return jarFile.getInputStream(jarFile.getEntry(entryPath));
    }

    public static void close(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception e) {

            }
        }
    }

}
