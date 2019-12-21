package com.xin.utils.io;

import lombok.extern.log4j.Log4j;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 文件压缩工具类
 * @date 2018-12-12 11:48
 */
@Log4j
public class ZipFileUtil {

    public static void compress(String sourceFilePath, String zipFilePath, String zipFileName) throws IOException {
        File sourceFile = new File(sourceFilePath);
        String zipPath = "";

        String endPrefix = ".zip";
        if (!zipFileName.endsWith(endPrefix)) {
            zipFileName += endPrefix;
        }

        if (zipFilePath.charAt(zipFilePath.length() - 1) != File.separatorChar) {
            zipPath = zipFilePath + File.separator + zipFileName;
        } else {
            zipPath = zipFilePath + zipFileName;
        }

        if (!sourceFile.exists()) {
            log.error("待压缩的文件目录：" + sourceFilePath + "不存在.");
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(zipPath);
             CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
             ZipOutputStream zos = new ZipOutputStream(cos)) {
            String basedir = "";
            compress(sourceFile, zos, basedir);
        }
    }

    private static void compress(File file, ZipOutputStream out, String basedir) throws IOException {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            log.debug("压缩：" + basedir + file.getName());
            compressDirectory(file, out, basedir);
        } else {
            log.debug("压缩：" + basedir + file.getName());
            compressFile(file, out, basedir);
        }
    }

    /**
     * 压缩一个目录
     */
    private static void compressDirectory(File dir, ZipOutputStream out, String basedir) throws IOException {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            compress(file, out, basedir + dir.getName() + "/");
        }

    }

    /**
     * 压缩一个文件
     */
    private static void compressFile(File file, ZipOutputStream out, String basedir) throws IOException {
        if (!file.exists()) {
            return;
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte[] data = new byte[1024 * 1024 * 2];
            while ((count = bis.read(data, 0, data.length)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        }
    }
}