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
public class DateTimeUtil {
    private final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String LINK = "[T_]";
    private static final String DATE_REGEX_S = "[\\D]*[\\d]{10}[\\D]*";
    private static final String DATE_REGEX_MS = "[^\\d]*[\\d]{13}[^\\d]*";

    private static final Map<Integer, Character> DEFAULT_CHAR_MAP = new HashMap<>();

    static {
        DEFAULT_CHAR_MAP.put(1, 'y');
        DEFAULT_CHAR_MAP.put(2, 'M');
        DEFAULT_CHAR_MAP.put(3, 'd');
        DEFAULT_CHAR_MAP.put(4, 'H');
        DEFAULT_CHAR_MAP.put(5, 'm');
        DEFAULT_CHAR_MAP.put(6, 's');
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

    public static long timeStamp(Date date) {
        return date.getTime();
    }

    /**
     * 将String转成Date
     *
     * @param dateStr 时间字符串
     * @param format  格式
     * @return Date
     * @throws ParseException 解析异常
     */
    public static Date toDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.parse(dateStr);
    }

    /**
     * 将String转成Date
     *
     * @param dateStr 时间字符串
     * @param format  格式
     * @param locale  枚举类型
     * @return Date
     * @throws ParseException 解析异常
     */
    public static Date toDate(String dateStr, String format, Locale locale) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(format, locale);
        return sf.parse(dateStr);
    }

    /**
     * 注意：不支持同时有多个时间的格式
     *
     * @param dateStr 含有时间的String
     * @return 标准时间格式
     * @throws ParseException 时间格式不支持
     */
    public static Date toDate(String dateStr) throws ParseException {
        if (StringUtil.isEmpty(dateStr)) {
            throw new ParseException("输入为空", -1);
        }
        return (Date) toDateWithSource(dateStr).get("date");
    }


    /**
     * 将String转成Date，同时携带代码中的源String
     *
     * @param dateStr 时间字符串，支持10位、13位时间戳、yyyy/MM/dd hh/mm/ss这种顺序的格式(yyyy/MM/dd这种都是支持的)
     * @return 转化后的日期
     * @throws ParseException 解析异常
     */
    public static Map<String, Object> toDateWithSource(String dateStr) throws ParseException {
        if ("".equals(dateStr)) {
            throw new ParseException("参数为空", 0);
        }

        Map<String, Object> rstMap = new HashMap<>();
        Date rstDate = null;
        String rstSource = null;

        // 去掉除了月份和数字以外的末尾信息
        if (!RegexUtil.isMatch(dateStr, "(Jan|January|Feb|February|Mar|March|Apr|April|May|Jun|June|Jul|July|Aug|August|Sept|September|Oct|October|Nov|November|Dec|December)")
                || RegexUtil.extract(dateStr, "\\d").size() > 4) {
            dateStr = dateStr.replaceAll("([^年|^月|^日|^时|^分|^秒|^0-9])*$", "");
        }

        // 支持13位时间戳
        if (RegexUtil.isMatch(dateStr, DATE_REGEX_MS)) {
            rstSource = RegexUtil.extractFirst(dateStr, "[\\d]{13}");
            rstDate = new Date(Long.parseLong(rstSource));
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 支持10位时间戳
        if (RegexUtil.isMatch(dateStr, DATE_REGEX_S)) {
            rstSource = RegexUtil.extractFirst(dateStr, "\\d{10}");
            rstDate = new Date(Long.parseLong(rstSource) * 1000);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 支持dd/MMM/yyyy hh:mm:ss
        // 20 Apr 2015
        String re1 = "(\\d{1,2})[^a-zA-Z0-9]+([a-zA-Z]{3,})[^a-zA-Z0-9]+(\\d{2,4})\\D*(\\d*):*(\\d*):*(\\d*)";
        if (RegexUtil.isMatch(dateStr, re1)) {
            Map<Integer, Character> map = new HashMap<>();
            map.put(1, 'd');
            map.put(2, 'M');
            map.put(3, 'y');
            map.put(4, 'h');
            map.put(5, 'm');
            map.put(6, 's');
            rstSource = RegexUtil.extractFirst(dateStr, re1);
            rstDate = toDate(rstSource, re1, map, Locale.ENGLISH);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 支持MMM/dd/yyyy hh:mm:ss
        // October 25, 2018
        String re3 = "([a-zA-Z]{3,})[^a-zA-Z0-9]+(\\d{1,2})[^a-zA-Z0-9]+(\\d{2,4})\\D*(\\d*):*(\\d*):*(\\d*)";
        if (RegexUtil.isMatch(dateStr, re3)) {
            Map<Integer, Character> map = new HashMap<>();
            map.put(1, 'M');
            map.put(2, 'd');
            map.put(3, 'y');
            map.put(4, 'h');
            map.put(5, 'm');
            map.put(6, 's');
            rstSource = RegexUtil.extractFirst(dateStr, re3);
            rstDate = toDate(rstSource, re3, map, Locale.US);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 支持dd/MM/yyyy hh:mm:ss
        // 20 06 2018
        String re2 = "(\\d{1,2})[^a-zA-Z0-9]+(\\d{1,2})[^a-zA-Z0-9]+(\\d{4})\\D*(\\d*):*(\\d*):*(\\d*)";
        if (RegexUtil.isMatch(dateStr, re2)) {
            Map<Integer, Character> map = new HashMap<>();
            map.put(1, 'd');
            map.put(2, 'M');
            map.put(3, 'y');
            map.put(4, 'h');
            map.put(5, 'm');
            map.put(6, 's');
            rstSource = RegexUtil.extractFirst(dateStr, re2);
            rstDate = toDate(rstSource, re2, map);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // yyyy/MM/dd/ hh:mm:ss这种顺序的格式
        // 2018年10月25日
        String re = "(\\d{4})\\D+(\\d{1,2})\\D+(\\d{1,2})\\D*(\\d*)\\D*(\\d*)\\D*(\\d*)";
        if (RegexUtil.isMatch(dateStr, re)) {
            rstSource = RegexUtil.extractFirst(dateStr, re);
            rstDate = toDate(rstSource, re, DEFAULT_CHAR_MAP);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // Jan 1
        String re5 = "(Jan|January|Feb|February|Mar|March|Apr|April|May|Jun|June|Jul|July|Aug|August|Sept|September|Oct|October|Nov|November|Dec|December)\\D+(\\d{1,2})";
        if (RegexUtil.isMatch(dateStr, re5)) {
            String dateStr1 = RegexUtil.extractFirst(dateStr, re5);
            rstSource = dateStr1;
            dateStr1 = dateStr1 + " " + 2018;
            rstDate = toDate(dateStr1);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 12月12日
        String re6 = "\\d{1,2}月\\d{1,2}日";
        if (RegexUtil.isMatch(dateStr, re6)) {
            String dateStr1 = RegexUtil.extractFirst(dateStr, re6);
            rstSource = dateStr1;
            dateStr1 = "2019年" + dateStr1;
            rstDate = toDate(dateStr1);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 2018-01
        String re7 = "\\d{4}-\\d{2}";
        if (RegexUtil.isMatch(dateStr, re7)) {
            String dateStr1 = RegexUtil.extractFirst(dateStr, re7);
            rstSource = dateStr1;
            dateStr1 = dateStr1 + "-01";
            rstDate = toDate(dateStr1);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        // 2018年
        String re8 = "[1,2]\\d{3}(?!\\d)";
        if (RegexUtil.isMatch(dateStr, re8)) {
            String dateStr1 = RegexUtil.extractFirst(dateStr, re8);
            rstSource = dateStr1;
            dateStr1 = dateStr1 + "-01-01";
            rstDate = toDate(dateStr1);
            rstMap.put("date", rstDate);
            rstMap.put("source", rstSource);
            return rstMap;
        }

        throw new ParseException("Can not find Date String!" + dateStr, 0);
    }


    private static Date toDate(String dateStr, String regex, Map<Integer, Character> characterMap, Locale locale) throws ParseException {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(dateStr);
        StringBuilder sb = new StringBuilder(dateStr);
        if (m.find(0)) {
            int count = m.groupCount();
            for (int i = 1; i <= count; i++) {
                for (Map.Entry<Integer, Character> entry : characterMap.entrySet()) {
                    if (entry.getKey() == i) {
                        sb.replace(m.start(i), m.end(i), replaceEachChar(m.group(i), entry.getValue()));
                    }
                }
            }
        }
        String format = sb.toString().replaceAll(LINK, " ");
        return toDate(dateStr.replaceAll(LINK, " "), format.replaceAll(LINK, " "), locale);
    }


    private static Date toDate(String dateStr, String regex, Map<Integer, Character> characterMap) throws ParseException {
        return toDate(dateStr, regex, characterMap, Locale.CHINA);
    }

    /**
     * 将指定字符串的所有字符替换成指定字符，跳过空白字符
     *
     * @param s 被替换字符串
     * @param c 字符
     * @return 新字符串
     */
    public static String replaceEachChar(String s, Character c) {
        StringBuilder sb = new StringBuilder();
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
    public static String transferFormat(String date, String originalFormat, String targetFormat) throws ParseException {
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
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offset);
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
    public static String getDayOffset(String date, String sourceFormat, int offset, String format) throws ParseException {
        return getDayOffset(toDate(date, sourceFormat), offset, format);
    }


    public static String getDayOffset(Date date, int offset, String format) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offset);
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
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + offset);
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
                time = (long) calendar.get(Calendar.YEAR);
                calendar.setTime(date2);
                return time - (long) calendar.get(Calendar.YEAR);
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
        calendar.set(Calendar.DATE, 1);
        return calendar;
    }

    public static Calendar getFirstDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        return calendar;
    }

    public static Calendar getMiddleDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 15);
        return calendar;
    }

    public static Calendar getMiddleDayOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.DATE, 15);
        return calendar;
    }

    public static Calendar getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int i = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, i);
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

    public static boolean isExpired(Date startDate, Date endDate) {
        boolean startIsNull = Objects.isNull(startDate);
        boolean endIsNull = Objects.isNull(endDate);
        if (startIsNull && endIsNull) {
            return false;
        }
        Date now = new Date();
        // 当前日期大于结束日期，则已经过期
        if (!endIsNull && endDate.compareTo(now) < 0) {
            return true;
        }
        // 当前日期小于于开始日期，则已经过期
        if (!startIsNull && startDate.compareTo(now) < 0) {
            return true;
        }
        return false;
    }

}
