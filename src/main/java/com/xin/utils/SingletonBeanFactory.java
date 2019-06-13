package com.xin.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 单例对象工厂类
 * @date 2018年6月13日 上午7:54:43
 */
public final class SingletonBeanFactory {

    private static ConcurrentHashMap<Class<?>, Object> singletonObjects = new ConcurrentHashMap<Class<?>, Object>();

    private static SingletonBeanFactory singletonBeanFactory;

    private SingletonBeanFactory() {
        if (null != singletonBeanFactory) {
            throw new RuntimeException("A singleton object cannot be created manually.");
        }
    }

    public static SingletonBeanFactory getInstance() {
        if (null == singletonBeanFactory) {
            synchronized (SingletonBeanFactory.class) {
                if (null == singletonBeanFactory) {
                    singletonBeanFactory = new SingletonBeanFactory();
                }
            }
        }
        return singletonBeanFactory;
    }

    /**
     * 以当前clazz对象为锁对象,每个clazz不一样锁对象也不一样，并发性较高
     *
     * @param clazz    所要要创建对象的class，必须有无参构造器
     * @param isCreate 当对象不存在缓存之中是否创建对象
     * @return 返回根据clazz反射生成的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz, boolean isCreate) {
        AssertUtil.checkNotNull(clazz, "Class must not be null.");
        Object instance = singletonObjects.get(clazz);
        if (null == instance) {
            synchronized (clazz) {
                instance = singletonObjects.get(clazz);
                if (null == instance && isCreate) {
                    try {
                        instance = clazz.newInstance();
                        singletonObjects.put(clazz, instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return (T) instance;
    }

    public static <T> void registerBean(Class<T> clazz, Map<String, Object> params) throws InvocationTargetException, IllegalAccessException {
        AssertUtil.checkNotNull(clazz, "Class must not be null.");
        AssertUtil.checkNotEmpty(params, "Map must not be Empty or null.");
        if (!singletonObjects.containsKey(clazz)) {
            synchronized (clazz) {
                if (!singletonObjects.containsKey(clazz)) {
                    T bean = BeanUtil.builder(clazz, params);
                    singletonObjects.put(clazz, bean);
                }
            }
        }
    }

    /**
     * 根据Class生成对象
     *
     * @param clazz 生成对象的Class
     * @param <T>
     * @return 返回根据clazz反射生成的对象
     */
    public <T> T getBean(Class<T> clazz) {
        return getBean(clazz, true);
    }

    public <T> void removeBean(Class<T> clazz) {
        singletonObjects.remove(clazz);
    }
}
