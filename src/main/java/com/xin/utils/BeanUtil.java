package com.xin.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param obj                  要转换的bean对象
     * @param convertFieldNameFunc 字段转换function
     * @return 返回map
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map<String, Object> bean2Map(Object obj,
                                               Function<String, String> convertFieldNameFunc) throws Exception {
        return bean2Map(obj, convertFieldNameFunc, false, null);
    }

    public static Map<String, Object> bean2Map(Object obj,
                                               Function<String, String> convertFieldNameFunc,
                                               boolean ignoreNullFileValue,
                                               String[] ignoreFields) throws Exception {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(64);

        if (Objects.isNull(convertFieldNameFunc)) {
            convertFieldNameFunc = t -> t;
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        boolean ignoreFieldsIsNull = ignoreFields == null;
        if (ignoreFieldsIsNull) {
            ignoreFields = new String[]{"class", "serialVersionUID"};
        }
        Set<String> ignoreFieldSet = Stream.of(ignoreFields).collect(Collectors.toSet());
        if (!ignoreFieldsIsNull) {
            ignoreFieldSet.add("class");
            ignoreFieldSet.add("serialVersionUID");
        }
        for (PropertyDescriptor property : propertyDescriptors) {
            String fieldName = property.getName();
            // 过滤class属性
            Iterator<String> iterator = ignoreFieldSet.iterator();
            boolean shouldContinue = false;
            while (iterator.hasNext()) {
                String ignoreField = iterator.next();
                if (Objects.equals(ignoreField, fieldName)) {
                    iterator.remove();
                    shouldContinue = true;
                    break;
                }
            }
            if (shouldContinue) {
                continue;
            }

            // 得到property对应的getter方法
            Method getter = property.getReadMethod();
            if (Objects.nonNull(getter)) {
                getter.setAccessible(true);
                Object value = getter.invoke(obj);
                if (ignoreNullFileValue && null == value) {
                    continue;
                }
                String name = convertFieldNameFunc.apply(fieldName);
                map.put(name, value);
            }
        }
        return map;
    }

    public static <T> T map2Bean(Map<String, Object> map,
                                 Class<T> clazz,
                                 Function<String, String> function) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        T instance = clazz.newInstance();
        if (Objects.isNull(function)) {
            function = t -> t;
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = function.apply(property.getName());
            if (map.containsKey(key)) {
                Object value = map.get(key);
                if (Objects.isNull(value)) {
                    continue;
                }
                // 得到property对应的setter方法
                Method setter = property.getWriteMethod();
                Method getter = property.getReadMethod();
                Class<?> paramType = value.getClass();
                Class<?> fieldType = null;
                try {
                    if (Objects.isNull(setter)) {
                        if (Objects.nonNull(getter)) {
                            fieldType = getter.getReturnType();
                            String setMethodName = getter.getName().replaceFirst("g", "s");
                            setter = clazz.getDeclaredMethod(setMethodName, getter.getReturnType());
                        }
                    }

                    if (Objects.nonNull(setter)) {
                        if (Objects.nonNull(fieldType) && !paramType.equals(fieldType)) {
                            if (Collection.class.isAssignableFrom(fieldType) && Collection.class.isAssignableFrom(paramType)) {
                                if (Set.class.isAssignableFrom(fieldType)) {
                                    value = new LinkedHashSet<>((Collection<Object>) value);
                                } else if (List.class.isAssignableFrom(fieldType)) {
                                    value = new ArrayList<>((Collection<Object>) value);
                                }
                            }
                        }

                        if (setter.getParameterTypes().length != 1
                                || !setter.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
                            continue;
                        }
                        setter.setAccessible(true);
                        setter.invoke(instance, value);
                    }
                } catch (Exception ignore) {

                }

            }
        }
        return instance;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            return field;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据字符串生成驼峰式类名
     * article_info_index类名为ArticleInfoIndex
     *
     * @param str
     * @return
     */
    public static String getClassName(String str) {
        AssertUtil.checkNotEmpty(str, "生成class名称字符串不能为空或者null.");

        String regex = "_";

        if (!str.contains(regex)) {
            return toUpperCaseFirstChar(str);
        }

        StringBuilder className = new StringBuilder();

        String[] names = str.split(regex);
        for (String name : names) {

            if (name.isEmpty()) {
                continue;
            }
            className.append(toUpperCaseFirstChar(name));
        }
        return className.toString();
    }

    private static String toUpperCaseFirstChar(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= lowerA && firstChar <= lowerZ) {
            firstChar -= 32;
            return firstChar + str.substring(1);
        }
        return str;
    }

    public static <T> T deepCopy(T src) {
        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream out = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream in = null;
        try {
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked") T dest = (T) in.readObject();
            return dest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(byteOut);
            close(out);
            close(byteIn);
            close(in);
        }
    }

    private static void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
        }
    }

    public static <T> boolean isEmpty(T object, String... excludeFile) throws Exception {
        if (Objects.isNull(object)) {
            return true;
        }
        Class<T> clazz = (Class<T>) object.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        Stream<PropertyDescriptor> stream = Stream.of(beanInfo.getPropertyDescriptors());
        Set<PropertyDescriptor> descriptorSet = null;
        if (Objects.nonNull(excludeFile)) {
            descriptorSet = stream.filter(item -> {
                for (String s : excludeFile) {
                    if (s.equals(item.getName())) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toSet());
        } else {
            descriptorSet = stream.collect(Collectors.toSet());
        }
        for (PropertyDescriptor propertyDescriptor : descriptorSet) {
            Method getter = propertyDescriptor.getReadMethod();
            if (Objects.nonNull(getter)) {
                Object value = getter.invoke(object);
                if (Objects.nonNull(value)) {
                    return false;
                }
            }
        }
        return true;
    }
}
