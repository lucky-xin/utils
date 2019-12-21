package com.xin.utils.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xin.utils.RegexUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * 文件工具类，支持读写文件、重命名、删除等
 *
 * @author 岑忠满 陆朝新
 * @date 2018/9/25 14:09
 */
@Log4j
public class FileUtil {
    /**
     * 指定格式读取文件
     *
     * @param in      输入流
     * @param charset 文件编码
     * @return 文件的行
     */
    public static List<String> readLines(InputStream in, Charset charset) {
        List<String> lineList = new ArrayList<>();

        if (in == null) {
            throw new NullPointerException("InputStream is empty!");
        }

        BufferedReader bufferedReader = null;
        String line;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in, charset));
            while ((line = bufferedReader.readLine()) != null) {
                lineList.add(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return lineList;
    }

    /**
     * 读取文件
     *
     * @param in 输入流
     * @return 文件的所有行
     */
    public static List<String> readLines(InputStream in) {
        return readLines(in, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件
     *
     * @param file 文件
     * @return 文件的行
     */
    public static List<String> readLines(File file) throws FileNotFoundException {
        return readLines(new FileInputStream(file));
    }

    /**
     * 指定格式读取文件
     *
     * @param file    文件
     * @param charset 文件编码
     * @return 文件的行
     */
    public static List<String> readLines(File file, Charset charset) throws FileNotFoundException {
        return readLines(new FileInputStream(file), charset);
    }

    /**
     * 指定格式读取文件
     *
     * @param path    文件路径
     * @param charset 文件编码
     * @return 文件的行
     */
    public static List<String> readLines(String path, Charset charset) throws FileNotFoundException {
        return readLines(new FileInputStream(new File(path)), charset);
    }

    /**
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件的行
     */
    public static List<String> readLines(String path) throws FileNotFoundException {
        return readLines(new FileInputStream(new File(path)));
    }

    /**
     * 读取文件
     *
     * @param in      输入流
     * @param charset 文件编码
     * @return 文件的内容
     */
    public static String read(InputStream in, Charset charset) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, charset));
        try {
            int value;
            while ((value = bufferedReader.read()) != -1) {
                stringBuilder.append((char) value);
            }
        } catch (IOException ignored) {
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 读取文件
     *
     * @param in 输入流
     * @return 文件的内容
     */
    public static String read(InputStream in) {
        return read(in, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件
     *
     * @param path    文件路径
     * @param charset 文件编码
     * @return 文件的内容
     */
    public static String read(String path, Charset charset) throws FileNotFoundException {
        return read(new FileInputStream(new File(path)), charset);
    }

    /**
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件的内容
     */
    public static String read(String path) throws FileNotFoundException {
        return read(path, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件
     *
     * @param file 文件
     * @return 文件的内容
     */
    public static String read(File file) throws FileNotFoundException {
        return read(new FileInputStream(file), StandardCharsets.UTF_8);
    }

    /**
     * 读取文件
     *
     * @param file    文件
     * @param charset 文件编码
     * @return 文件的内容
     */
    public static String read(File file, Charset charset) throws FileNotFoundException {
        return read(new FileInputStream(file), charset);
    }

    /**
     * 写入文件
     *
     * @param out     输入流
     * @param content 内容
     * @param charset 编码
     */
    private static void write(OutputStream out, String content, Charset charset) {
        BufferedWriter bufferedWriter;
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, charset));
        try {
            bufferedWriter.write(content);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                bufferedWriter.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写入文件
     *
     * @param path    写出的路径，若不存在则自动创建
     * @param content 内容
     * @param charset 编码
     */
    public static void write(String path, String content, Charset charset, Boolean appEnd) {
        // 文件夹不存在时创建文件夹，然后再写
        String dirPath = path.replaceAll("[^/\\\\]+$", "");
        if (!exists(dirPath)) {
            if (mkdirs(dirPath)) {
                try {
                    write(new FileOutputStream(new File(path), appEnd), content, charset);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                log.error("Make dir fail!");
            }
        } else {
            // 文件夹存在直接写
            try {
                write(new FileOutputStream(new File(path), appEnd), content, charset);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 写入文件
     *
     * @param file    写出的文件，若不存在则自动创建
     * @param content 内容
     * @param charset 编码
     */
    public static void write(File file, String content, Charset charset, Boolean appEnd) {
        write(file.getAbsolutePath(), content, charset, appEnd);
    }

    /**
     * 写入文件
     *
     * @param path    写出的路径，若不存在则自动创建
     * @param content 内容
     * @param charset 编码
     */
    public static void write(String path, String content, Charset charset) {
        write(path, content, charset, false);
    }

    /**
     * 写入文件
     *
     * @param file    写出的文件，若不存在则自动创建
     * @param content 内容
     * @param charset 编码
     */
    public static void write(File file, String content, Charset charset) {
        write(file.getAbsolutePath(), content, charset);
    }

    /**
     * 写入文件
     *
     * @param file    写出的文件，若不存在则自动创建
     * @param content 内容
     */
    public static void write(File file, String content) {
        write(file, content, StandardCharsets.UTF_8, false);
    }

    /**
     * 写入文件
     *
     * @param file  写出的文件，若不存在则自动创建
     * @param lines 内容行
     */
    public static void write(File file, List<String> lines) {
        write(file, lines, StandardCharsets.UTF_8, false);
    }

    /**
     * 写入文件
     *
     * @param filePath 写出的文件，若不存在则自动创建
     * @param lines    内容行
     */
    public static void write(String filePath, List<String> lines) {
        write(new File(filePath), lines);
    }

    /**
     * 写入文件
     *
     * @param filePath 写出的文件，若不存在则自动创建
     * @param lines    内容行
     */
    public static void write(String filePath, List<String> lines, Charset charset, Boolean append) {
        write(new File(filePath),lines,charset,append);
    }


    /**
     * 写入文件
     *
     * @param file  写出的文件，若不存在则自动创建
     * @param lines 内容行
     */
    public static void write(File file, List<String> lines, Charset charset, Boolean append) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.endsWith("\n") || line.endsWith("\r")) {
                sb.append(line);
            } else {
                sb.append(line).append("\n");
            }
        }

        write(file, sb.toString(), charset, append);
    }

    /**
     * 写入文件
     *
     * @param path    写出的路径，若不存在则自动创建
     * @param content 内容
     */
    public static void write(String path, String content) {
        write(path, content, StandardCharsets.UTF_8, false);
    }

    /**
     * 列举当前文件夹下的所有文件
     *
     * @param file 文件夹
     * @return 若为文件夹，返回所有文件Array，否则返回空Array
     */
    public static List<File> listFiles(File file) {
        List<File> files = new ArrayList<>();
        if (isDir(file)) {
            files = Arrays.asList(Objects.requireNonNull(file.listFiles()));
        }
        return files;
    }

    /**
     * 列举当前文件夹下的所有文件
     *
     * @param path 文件夹路径
     * @return 若为文件夹，返回所有文件Array，否则返回空Array
     */
    public static List<File> listFiles(String path) {
        return listFiles(new File(path));
    }

    /**
     * 递归列举文件
     *
     * @param path 文件路径
     * @return 所有文件
     */
    public static List<File> listAllFiles(String path) {
        return listAllFiles(new File(path));
    }


    /**
     * 递归列举文件
     *
     * @param file 文件
     * @return 所有文件
     */
    public static List<File> listAllFiles(File file) {
        List<File> rstFiles = new ArrayList<>();

        if (isDir(file)) {
            List<File> files = listFiles(file);
            for (File f : files) {
                rstFiles.addAll(listAllFiles(f));
            }
        } else {
            rstFiles.add(file);
        }
        return rstFiles;
    }

    /**
     * 判断是否文件夹
     *
     * @param path 文件夹路径
     * @return 若为文件夹返回True
     */
    public static boolean isDir(String path) {
        return new File(path).isDirectory();
    }

    /**
     * 判断是否文件夹
     *
     * @param file 文件夹
     * @return 若为文件夹返回True
     */
    public static boolean isDir(File file) {
        return file.isDirectory();
    }

    /**
     * 判断是否文件
     *
     * @param path 文件夹路径
     * @return 若为文件返回True
     */
    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    /**
     * 判断是否文件
     *
     * @param file 文件
     * @return 若为文件返回True
     */
    public static boolean isFile(File file) {
        return file.isFile();
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param file 文件
     * @return 若存在返回True
     */
    public static boolean exists(File file) {
        return file.exists();
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param path 文件路径
     * @return 若存在返回True
     */
    public static boolean exists(String path) {
        return exists(new File(path));
    }

    /**
     * 删除文件或文件夹
     *
     * @param path 文件路径
     * @return 成功删除返回True
     */
    public static boolean delete(String path) {
        return delete(new File(path));
    }

    /**
     * 删除文件或文件夹
     * 递归实现
     *
     * @param file 文件
     * @return 成功删除返回True
     */
    public static boolean delete(File file) {
        if (isDir(file)) {
            for (File file1 : listFiles(file)) {
                delete(file1);
            }
        }
        return file.delete();
    }

    /**
     * 重命名文件或文件夹
     *
     * @param srcPath 原名
     * @param dstPath 新名
     * @return 成功重命名返回True
     */
    public static boolean renameTo(String srcPath, String dstPath) {
        File file1 = new File(srcPath);
        File file2 = new File(dstPath);
        return file1.renameTo(file2);
    }

    /**
     * 重命名文件或文件夹
     *
     * @param srcFile 原名
     * @param dstFile 新名
     * @return 成功重命名返回True
     */
    public static boolean renameTo(File srcFile, File dstFile) {
        return srcFile.renameTo(dstFile);
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹
     * @return 成功创建返回True
     */
    public static boolean mkdir(String path) {
        return new File(path).mkdir();
    }

    /**
     * 创建文件夹
     *
     * @param file 文件夹
     * @return 成功创建返回True
     */
    public static boolean mkdir(File file) {
        return file.mkdir();
    }

    /**
     * 递归创建文件夹
     *
     * @param path 文件夹
     * @return 成功创建返回True
     */
    public static boolean mkdirs(String path) {
        return new File(path).mkdirs();
    }

    /**
     * 递归创建文件夹
     *
     * @param file 文件夹
     * @return 成功创建返回True
     */
    public static boolean mkdirs(File file) {
        return file.mkdirs();
    }

    /**
     * 复制文件
     *
     * @param srcFile 原文件
     * @param dstFile 新文件
     */
    public static void copyFile(File srcFile, File dstFile) throws IOException {
        if (!exists(dstFile.getParent())) {
            mkdirs(dstFile.getParent());
        }
        Files.copy(srcFile.toPath(), dstFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 复制文件
     *
     * @param srcPath 原路径
     * @param dstPath 新路径
     */
    public static void copyFile(String srcPath, String dstPath) throws IOException {
        copyFile(new File(srcPath), new File(dstPath));
    }

    /**
     * 复制文件夹
     *
     * @param srcFile 原文件夹
     * @param dstFile 新文件夹
     */
    public static void copyDir(File srcFile, File dstFile) throws IOException {
        FileUtils.copyDirectory(srcFile, dstFile);
    }

    /**
     * 复制文件夹
     *
     * @param srcPath 原路径
     * @param dstPath 新路径
     */
    public static void copyDir(String srcPath, String dstPath) throws IOException {
        FileUtils.copyDirectory(new File(srcPath), new File(dstPath));
    }


    /**
     * 移动文件夹
     *
     * @param srcFile 原文件夹
     * @param dstFile 新文件夹
     */
    public static void moveDir(File srcFile, File dstFile) throws IOException {
        FileUtils.moveDirectory(srcFile, dstFile);
    }

    /**
     * 移动文件夹
     *
     * @param srcPath 原路径
     * @param dstPath 新路径
     */
    public static void moveDir(String srcPath, String dstPath) throws IOException {
        FileUtils.moveDirectory(new File(srcPath), new File(dstPath));
    }

    /**
     * 获取文件夹名
     *
     * @param file 文件或文件夹路径
     * @return 文件或文件夹名, 若为文件带后缀
     */
    public static String getName(File file) {
        return file.getName();
    }

    /**
     * 获取文件夹名
     *
     * @param path 文件或文件夹路径
     * @return 文件或文件夹名, 若为文件带后缀
     */
    public static String getName(String path) {
        if (path.isEmpty()) {
            return "";
        }
        return getName(new File(path));
    }


    /**
     * 获取文件夹名, 且不带后缀
     *
     * @param file 文件或文件夹
     * @return 文件或文件夹名, 若为文件不带后缀
     */
    public static String getNameWithOutType(File file) {
        return getName(file).replaceAll("\\.[a-zA-Z]+?$", "");
    }

    /**
     * 获取文件夹名, 且不带后缀
     *
     * @param path 文件或文件夹路径
     * @return 文件或文件夹名, 若为文件不带后缀
     */
    public static String getNameWithOutType(String path) {
        return getNameWithOutType(new File(path));
    }

    /**
     * 获取文件类型
     */
    public static String getType(String path) {
        return RegexUtil.extractFirst(path, "\\.[a-zA-Z]+?$").replace(".", "");
    }

    /**
     * 获取文件的大小
     *
     * @param file 文件
     */
    public static long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }

    /**
     * 判断文件是否有内容
     *
     * @param file 文件
     */
    public static boolean isEmpty(File file) {
        return getFileSize(file) < 0;
    }

    /**
     * MappedByteBuffer方式写文件
     *
     * @param file
     * @param bytes
     * @throws IOException
     */
    public static void nioWrite(File file, byte[] bytes) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel();) {
            MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            mbb.put(bytes);
        }
    }

    /**
     * MappedByteBuffer方式写文件
     *
     * @param file
     * @param is
     * @throws IOException
     */
    public static void nioWrite(File file, InputStream is) throws IOException {
        nioWrite(file, convertInputStreamToByte(is));
    }

    /**
     * MappedByteBuffer方式写文件
     *
     * @param filePath
     * @param bytes
     * @throws IOException
     * @author Luchaoxin
     */
    public static void nioWrite(String filePath, byte[] bytes) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
             FileChannel fileChannel = raf.getChannel();) {
            MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            mbb.put(bytes);
        }
    }

    /**
     * MappedByteBuffer方式写文件
     *
     * @param filePath
     * @param is
     * @throws IOException
     * @author Luchaoxin
     */
    public static void nioWrite(String filePath, InputStream is) throws IOException {
        nioWrite(filePath, convertInputStreamToByte(is));
    }

    /**
     * MappedByteBuffer方式读取文件
     *
     * @param filePath
     * @return
     * @throws IOException
     * @author Luchaoxin
     */
    public static byte[] nioRead(String filePath) throws IOException {
        MappedByteBuffer mappedByteBuffer = null;
        try (RandomAccessFile fis = new RandomAccessFile(new File(filePath), "r");
             FileChannel channel = fis.getChannel()) {
            long size = channel.size();
            // 构建一个只读的MappedByteBuffer
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);

            byte[] bytes = new byte[(int) size];
            if (mappedByteBuffer.remaining() > 0) {
                mappedByteBuffer.get(bytes, 0, mappedByteBuffer.remaining());
            }
            clean(mappedByteBuffer);
            return bytes;
        }
    }

    /**
     * 清理MappedByteBuffer句柄
     *
     * @param buffer
     */
    public static void clean(final MappedByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        PrivilegedAction<Object> action = () -> {
            try {
                Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                if (getCleanerMethod != null) {
                    getCleanerMethod.setAccessible(true);
                    Object cleaner = getCleanerMethod.invoke(buffer, new Object[0]);
                    Method cleanMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                    if (cleanMethod != null) {
                        cleanMethod.setAccessible(true);
                        cleanMethod.invoke(cleaner, new Object[0]);
                    }
                }
            } catch (Exception e) {
                log.error("关闭MappedByteBuffer异常", e);
            }
            return null;
        };
        AccessController.doPrivileged(action);
    }

    /**
     * 把InputStream转化为byte数组
     *
     * @param is
     * @return
     * @throws IOException
     * @author Luchaoxin
     */
    public static byte[] convertInputStreamToByte(InputStream is) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();) {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] byteArray = buffer.toByteArray();
            return byteArray;
        }
    }

    /**
     * 读取磁盘的.json文件转化为map
     *
     * @param jsonPath .json文件的绝对路径
     * @return
     * @throws IOException
     * @author Luchaoxin
     */
    public static Map<String, Object> readJson(String jsonPath) throws IOException {
        byte[] bytes = nioRead(jsonPath);
        Type type = new TypeReference<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = JSON.parseObject(bytes, type);
        return map;
    }

}
