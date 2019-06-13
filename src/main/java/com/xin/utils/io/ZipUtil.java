package com.xin.utils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public void fileToZip(String zipFilePath, File[] files) throws Exception {
        File file = new File(zipFilePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            byte[] buffer = new byte[1024];
            fos = new FileOutputStream(zipFilePath);
            zos = new ZipOutputStream(fos);
            for (int i = 0; i < files.length; i++) {
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.flush();
            }
        } finally {
//            IOUtils.close(fos);
//            IOUtils.close(zos);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
            } catch (Exception e) {
            }
        }
    }

}
