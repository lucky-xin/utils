package com.xin.utils.test.jvm;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: JVM学习
 * @date 2018-08-04 13:06
 * @Copyright (C)2018 , Luchaoxin
 */
public class JVMLearn {
    private static final int _1MB = 1024 * 1024;

    //-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[5 * _1MB];//1024K*2
//        allocation1 = new byte[8 * _1MB];//1024K*2
//        allocation2 = new byte[3 * _1MB];
        allocation3 = new byte[3 * _1MB];
        allocation4 = new byte[1 * _1MB];
//        allocation4 = new byte[4 * _1MB];
//        allocation4 = new byte[3 * _1MB];
//        allocation4 = new byte[4 * _1MB];
//        allocation4 = new byte[4 * _1MB];
    }
}
