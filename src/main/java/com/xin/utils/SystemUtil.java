package com.xin.utils;

/**
 * 用于获取系统信息
 */
public class SystemUtil {
    /**
     * 判断是否是windows系统
     *
     * @return 是/否
     */
    public static boolean isWindows(){String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("win");
    }
}
