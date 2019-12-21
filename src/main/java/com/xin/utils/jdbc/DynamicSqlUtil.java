package com.xin.utils.jdbc;

import com.xin.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 动态sql工具类
 * @date 2018-08-12 11:52
 * @Copyright (C)2018 , Luchaoxin
 */
public class DynamicSqlUtil {

    /**
     * 根据成员属相生成sql更新语句
     * 注意：使用驼峰式命名时，字段名必须符合以下规则
     * 如：字段名称为 userName 则数据库字段为 user_name
     *
     * @param fieldNames 成员名称数组
     * @return sql语句
     */
    public static String getAndStmt(String[] fieldNames) {
        String column = null;
        String value = null;

        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < fieldNames.length; i++) {

            column = StringUtil.lowerCamelToUnderline(fieldNames[i]);
            value = "#{" + fieldNames[i] + "}";
            sql.append(" and ").append(column).append("=").append(value);
            if (fieldNames.length - 1 != i) {
                sql.append(",");
            }
        }
        return null;
    }

    /**
     * 根据已经初始化的对象来创建sql更新语句，如果对象成员不为null更新
     *
     * @param instance 对象实例
     * @return 返回如下 name=#{namd},sex=#{sex}
     * @throws Exception
     */
    public static String getUpdateStmt(Object instance) throws Exception {
        return getUpdateStmt(getNotNullFields(instance));
    }

    public static String getUpdateStmt(String[] fieldNames) {

        String column = null;
        String value = null;

        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < fieldNames.length; i++) {

            column = StringUtil.lowerCamelToUnderline(fieldNames[i]);
            value = "#{" + fieldNames[i] + "}";

            sql.append(column).append("=").append(value);
            if (fieldNames.length - 1 != i) {
                sql.append(",");
            }
        }
        return sql.toString();
    }

    public static String getInStmt(String column, Object[] values) {
        if (StringUtil.isEmpty(column) || values.length == 0) {
            return "";
        }
        StringBuilder sql = new StringBuilder(" ").append(column).append(" in ( ");
        for (int i = 0; i < values.length; i++) {
            sql.append("'").append(values[i]).append("'");
            if (values.length - 1 != i) {
                sql.append(",");
            } else {
                sql.append(")");
            }
        }
        return sql.toString();
    }

    public static String getInCondition(String column, Object[] values) {
        return " and " + getInStmt(column, values);
    }

    public static String getLikeCondition(String column, Object value) {
        return " and " + getLikeStmt(column, value);
    }

    public static String getLikeStmt(String column, Object value) {
        return new StringBuilder(" ").append(column).append(" like '%").append(value).append("%'").toString();
    }

    public static String getOrCondition(String column, Object value) {
        return new StringBuilder(" or ").append(column).append(" = ").append(value).toString();
    }


    public static String[] getNotNullFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        List<String> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (null != getFieldValue(instance, field.getName())) {
                fields.add(field.getName());
            }
        }
        return fields.toArray(new String[]{});
    }


    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        Class<?> clazz = instance.getClass();
        String firstChar = String.valueOf(fieldName.charAt(0));

        String getMethodName = "get" + fieldName.replaceFirst(firstChar, firstChar.toUpperCase());
        return clazz.getMethod(getMethodName).invoke(instance);
    }

    public static String getSqlInParams(String str) {
        String[] params = str.split(",");
        return getSqlInParams(((Object[]) (params)));
    }

    public static String getSqlInParams(Object[] params) {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("(");
        for (int i = 0; i < params.length; i++) {
            strbuf.append("?,");
        }

        strbuf.deleteCharAt(strbuf.length() - 1);
        strbuf.append(")");
        return strbuf.toString();
    }


}
