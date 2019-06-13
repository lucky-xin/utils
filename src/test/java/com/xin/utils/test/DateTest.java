package com.xin.utils.test;

import com.xin.utils.DateUtil;
import org.junit.Test;

import java.util.Date;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: date工具类测试
 * @date 2018-08-23 22:09
 * @Copyright (C)2018 , Luchaoxin
 */
public class DateTest {

    @Test
    public void test1() {

        Date date1 = DateUtil.toDate("201809251211", "yyyyMMddHHmm");
        Date date2 = DateUtil.toDate("201809251311", "yyyyMMddHHmm");


        System.out.println(DateUtil.toString(date1, "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateUtil.toString(date2, "yyyy-MM-dd HH:mm:ss"));
        System.out.println("---------------");
        System.out.println(DateUtil.getDayOffset(-3, "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateUtil.getMonthOffset(-3, "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateUtil.diff(13, date1, date2));
    }
}
