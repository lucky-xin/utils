package com.xin.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 创建bean 对象工具类
 * @date 2017-08-12 11:52
 */
public class BeanUtil {

    /**
     * 给某一个class的所有属性赋值，赋值形式为map之中key为属性名称，value为属性值
     *
     * @param clazz  要创建对象的class
     * @param params 所有属性和属性值
     * @param <T>
     * @return
     */
    public static <T> T builder(Class<T> clazz, Map<String, Object> params) throws InvocationTargetException, IllegalAccessException {
        AssertUtil.checkNotNull(clazz, "clazz must not be null");
        AssertUtil.checkNotEmpty(params, "params must not be Empty");
        Map<String, Method> methods = new HashMap<>(16);
        for (Method method : clazz.getDeclaredMethods()) {
            methods.put(method.getName(), method);
        }
        T object = null;
        try {
            object = clazz.newInstance();
        } catch (Exception e) {
        }
        AssertUtil.checkNotNull(object, "反射创建方法失败，请检查是否有无参构造方法！！！");
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            String setMethodName = getSetMethodName(fieldName);
            Method method = methods.get(setMethodName);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(object, params.get(fieldName));
            }
        }
        return object;
    }

    private static char lowerA = 'a';

    private static char lowerZ = 'z';

    /**
     * 根据属性名称获取get方法名称
     *
     * @param fieldName 属性名称
     * @return get方法名称
     */
    public static String getGetMethodName(String fieldName) {
        return getMethodName("get", fieldName);
    }

    /**
     * 根据属性名称获取set方法名称
     *
     * @param fieldName 属性名称
     * @return set方法名称
     */
    public static String getSetMethodName(String fieldName) {
        return getMethodName("set", fieldName);
    }

    private static String getMethodName(String prefixName, String fieldName) {
        char firstChar = fieldName.charAt(0);
        if (firstChar >= lowerA && firstChar <= lowerZ) {
            firstChar -= 32;
        }
        String getMethodName = prefixName + firstChar + fieldName.substring(1);
        return getMethodName;
    }

    /**
     * 通过反射把bean对象消息转换成map,对象属性为key,对象属性值为value
     *
     * @param object 要转换的bean对象
     * @return 返回map
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map<String, Object> toMap(Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = object.getClass();
        Method getValueMethod;
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> objValueMap = new HashMap<>(fields.length);

        for (Field field : fields) {
            String fieldName = field.getName();
            String getMethodName = BeanUtil.getGetMethodName(fieldName);

            getValueMethod = clazz.getMethod(getMethodName);
            Object fieldValue = getValueMethod.invoke(object);

            if (fieldValue instanceof Date) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = (Date) fieldValue;
                fieldValue = format.format(date);
            }
            objValueMap.put(fieldName, fieldValue);
        }
        return objValueMap;
    }

}
