package com.xin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 日期操作工具类
 * @date 2018-08-23 21:46
 */
public class DateUtil {

    private final static String DEFAULT_FORMAT = "yyyyMMdd HH:mm:ss";

    private static final Map<Integer, Character> CHARMAP = new HashMap<>();

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)\\D*(\\d*)\\D*(\\d*)\\D*(\\d*)\\D*(\\d*)\\D*(\\d*)");

    private static final String DATE_REGEX = "^[-+]?\\\\d{13}$";

    static {
        CHARMAP.put(1, 'y');
        CHARMAP.put(2, 'M');
        CHARMAP.put(3, 'd');
        CHARMAP.put(4, 'H');
        CHARMAP.put(5, 'm');
        CHARMAP.put(6, 's');
    }

    private static SimpleDateFormat getSimpleDateFormat(String format) {
        return StringUtil.isEmpty(format) ? new SimpleDateFormat(DEFAULT_FORMAT) : new SimpleDateFormat(format);
    }

    /**
     * 获取当前String日期
     *
     * @param format 返回的日期格式
     * @return
     */
    public static String getDate(String format) {
        return toString(new Date(), format);
    }

    public static String getDate() {
        return toString(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 把java.util.Date对象转换成指定format的String类型
     *
     * @param date   要转换的java.util.Date对象
     * @param format 要转换成String的时间格式
     * @return
     */
    public static String toString(Date date, String format) {
        SimpleDateFormat sdf = getSimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String toString(long date, String format) {
        return toString(new Date(date), format);
    }

    public static String getTimetamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    /**
     * 把字符串日期转换成java.util.Date对象
     * 注意：输入的date格式必须和format一致，否则报错
     * 如 date为2017-01-01则 format 应该为 yyyy-MM-dd
     *
     * @param date   String 日期
     * @param format 此String日期格式
     * @return 返回Date 对象
     */
    public static Date toDate(String date, String format) {
        try {
            SimpleDateFormat pe = new SimpleDateFormat(format);
            return pe.parse(date);
        } catch (ParseException arg2) {
            throw new RuntimeException(arg2);
        }
    }


    /**
     * 任意日期字符串转换为Date，
     * 不包括无分割的纯数字（13位时间戳除外）
     * 日期时间为数字，年月日时分秒，但没有毫秒
     *
     * @param dateString 日期字符串
     * @return Date
     * Exception 如果时间格式不识别会抛出异常
     */
    public static Date toDate(String dateString) throws Exception {
        dateString = dateString.trim().replaceAll("[a-zA-Z]", " ");
        //支持13位时间戳
        if (Pattern.matches(DATE_REGEX, dateString)) {
            return new Date(Long.parseLong(dateString));
        }
        Matcher m = PATTERN.matcher(dateString);
        StringBuilder sb = new StringBuilder(dateString);
        /**
         * 从被匹配的字符串中，充index = 0的下表开始查找能够匹配pattern的子字符串。
         * m.matches()的意思是尝试将整个区域与模式匹配，不一样。
         */
        if (m.find(0)) {
            int count = m.groupCount();
            for (int i = 1; i <= count; i++) {
                for (Map.Entry<Integer, Character> entry : CHARMAP.entrySet()) {
                    if (entry.getKey() == i) {
                        sb.replace(m.start(i), m.end(i), replaceEachChar(m.group(i), entry.getValue()));
                    }
                }
            }
        } else {
            throw new Exception("时间格式不能被识别");
        }
        String format = sb.toString();
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.parse(dateString);
    }

    /**
     * 将指定字符串的所有字符替换成指定字符，跳过空白字符
     *
     * @param s 被替换字符串
     * @param c 字符
     * @return 新字符串
     */
    public static String replaceEachChar(String s, Character c) {
        StringBuilder sb = new StringBuilder("");
        for (Character c1 : s.toCharArray()) {
            if (c1 != ' ') {
                sb.append(String.valueOf(c));
            }
        }
        return sb.toString();
    }

    /**
     * 格式化java.util.Date对象为执行format格式类型的String
     *
     * @param date   要进行格式化的java.util.Date对象
     * @param format 格式化日期的格式
     * @return
     */
    public static String format(Date date, String format) {
        return toString(date, format);
    }

    public static String format(Date date) {
        return toString(date, DEFAULT_FORMAT);
    }

    /**
     * 把某个格式的日期String转换成另外一种日期格式的String 比如 2018-01-02 转换成 2018-01-02 11:22:32
     *
     * @param date           要转换的日期
     * @param originalFormat 原来的日期格式
     * @param targetFormat   转换之后的日期格式
     * @return 转换之后的日期
     */
    public static String transferFormat(String date, String originalFormat, String targetFormat) {
        Date original = toDate(date, originalFormat);
        return format(original, targetFormat);
    }

    /**
     * 把某个格式的日期String转换成另外一种日期格式的String 比如 2018-01-02 转换成 2018-01-02 11:22:32
     *
     * @param date         要转换的日期
     * @param targetFormat 转换之后的日期格式
     * @return 转换之后的日期
     */
    public static String convertFormat(String date, String targetFormat) throws Exception {
        Date original = toDate(date);
        return format(original, targetFormat);
    }

    /**
     * 以当前日期为起点获取相差offset天数的日期，格式为format的String
     *
     * @param offset 与今天相差的天数，正负都可以
     * @param format 返回的String格式
     * @return
     */
    public static String getDayOffset(int offset, String format) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(5, calendar.get(5) + offset);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }

    /**
     * 以输入的date为基准获取相差offset的日期，格式为format
     * date格式必须为yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd
     *
     * @param date   基准日期
     * @param offset 相差天数
     * @param format 返回String日期格式
     * @return
     */
    public static String getDayOffset(String date, int offset, String format) throws Exception {
        return getDayOffset(toDate(date), offset, format);
    }

    /**
     * 以输入的date为基准获取相差offset的日期，格式为format
     *
     * @param date         基准日期
     * @param sourceFormat 基准日期格式
     * @param offset       相差天数
     * @param format       返回String日期格式
     * @return
     */
    public static String getDayOffset(String date, String sourceFormat, int offset, String format) {
        return getDayOffset(toDate(date, sourceFormat), offset, format);
    }


    public static String getDayOffset(Date date, int offset, String format) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        calendar.set(5, calendar.get(5) + offset);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }


    public static String getMonthOffset(int offset, String format) {
        return getDayOffset(new Date(), offset, format);
    }

    /**
     * 以date为基准获取相差offset月的日期，比如下个月的今天或者上个月的今天日期
     *
     * @param date   基准日期
     * @param offset 相差的月数
     * @param format 返回时间格式
     * @return
     */
    public static String getMonthOffset(Date date, int offset, String format) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        calendar.set(2, calendar.get(2) + offset);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }

    /**
     * 返回date1减去date2之差
     *
     * @param type  1为相差多少年，2为相差多少天，3为相差多少小时，4为相差多少分钟，5为相差多少分钟
     * @param date1
     * @param date2
     * @return
     */
    public static long diff(int type, Date date1, Date date2) {
        long time;
        switch (type) {
            case 1:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                time = (long) calendar.get(1);
                calendar.setTime(date2);
                return time - (long) calendar.get(1);
            case 2:
                time = date1.getTime() / 1000L / 60L / 60L / 24L;
                return time - date2.getTime() / 1000L / 60L / 60L / 24L;
            case 3:
                time = date1.getTime() / 1000L / 60L / 60L;
                return time - date2.getTime() / 1000L / 60L / 60L;
            case 4:
                time = date1.getTime() / 1000L / 60L;
                return time - date2.getTime() / 1000L / 60L;
            case 5:
                time = date1.getTime() / 1000L;
                return time - date2.getTime() / 1000L;
            default:
                return date1.getTime() - date2.getTime();
        }
    }

    public static Calendar getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(5, 1);
        return calendar;
    }

    public static Calendar getFirstDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(2, 1);
        calendar.set(5, 1);
        return calendar;
    }

    public static Calendar getMiddleDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(2, 1);
        calendar.set(5, 15);
        return calendar;
    }

    public static Calendar getMiddleDayOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(5, 15);
        return calendar;
    }

    public static Calendar getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int i = calendar.getActualMaximum(5);
        calendar.set(5, i);
        return calendar;
    }

    /**
     * 获取每天定时执行的延迟时间，如果定时时间小于当前返回时间则为明天，在定时任务用到
     *
     * @param hour   定时时间时钟
     * @param minute 定时时间分钟
     * @param second 定时时间秒钟
     * @return
     */
    public static long getDelayTime(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, getDayOfMonth(hour, minute, second));

        return cal.getTime().getTime() - System.currentTimeMillis();
    }


    private static int getDayOfMonth(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 0);
        long diffTime = cal.getTime().getTime() - System.currentTimeMillis();
        return diffTime > 0 ? 0 : 1;
    }

    List<String> allFormat = new ArrayList<>();
}
