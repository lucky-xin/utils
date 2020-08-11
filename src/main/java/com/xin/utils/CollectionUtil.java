package com.xin.utils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Luchaoxin
 * @version V 4.0.0
 * @Description java集合, 数组操作util
 * @date 2017年9月27日 下午4:28:50
 */

public class CollectionUtil {

    public static <E> boolean isNull(E object) {
        return object == null;
    }

    public static <E> boolean isEmpty(Collection<E> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return (map == null || map.isEmpty());
    }

    public static <E> boolean isEmpty(E[] elements) {
        return (elements == null || elements.length == 0);
    }

    public static <E> boolean isNotEmpty(Collection<E> collection) {
        return !isEmpty(collection);
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <E> boolean isNotEmpty(E[] elements) {
        return !isEmpty(elements);
    }

    public static <E> int size(List<E> list) {
        return isEmpty(list) ? 0 : list.size();
    }

    public static int size(Object[] objects) {
        return isEmpty(objects) ? 0 : objects.length;
    }

    public static <K, V> int size(Map<K, V> map) {
        return isEmpty(map) ? 0 : map.size();
    }

    /**
     * 获取map之中指定key的String值，如果key不存在或者map为null或者空则返空串
     *
     * @param map 从该map之中获取值
     * @param key 从该map之中获取某个String值的key
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return 返回指定map之中某个key的String类型值
     */
    public static <K, V> String getStringValue(Map<K, V> map, K key) {
        return getStringValue(map, key, "");
    }

    /**
     * 获取map之中指定key的String值，如果key不存在或者map为null或者空则返回默认值defaultValue
     *
     * @param map          从该map之中获取值
     * @param key          从该map之中获取某个String值的key
     * @param defaultValue 给定默认值key不存在或者map为空则返回默认值
     * @param <K>          key的泛型
     * @param <V>          value的泛型
     * @return 返回指定map之中某个key的String类型值
     */
    public static <K, V> String getStringValue(Map<K, V> map, K key, String defaultValue) {
        return isEmpty(map) ? defaultValue : StringUtil.toString(map.get(key), defaultValue);
    }

    /**
     * 获取map之中指定key的int值，如果key不存在或者map为null或者空则返回-1
     *
     * @param map 指定获取int值的map
     * @param key 从指定map之中获取int值的map
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return 返回指定map之中某个key的int类型值
     */
    public static <K, V> Integer getIntegerValue(Map<K, V> map, K key) {
        return getIntegerValue(map, key, -1);
    }

    /**
     * 获取map之中指定key的int值，如果key不存在或者map为null或者空则返回默认值defaultValue
     *
     * @param map          指定获取int值的map
     * @param key          从指定map之中获取int值的map
     * @param defaultValue 给定默认值key不存在或者map为空则返回默认值
     * @param <K>          key的泛型
     * @param <V>          value的泛型
     * @return 返回指定map之中某个key的int类型值
     */
    public static <K, V> Integer getIntegerValue(Map<K, V> map, K key, Integer defaultValue) {
        return isEmpty(map) ? defaultValue : StringUtil.toInteger(map.get(key), defaultValue);
    }

    /**
     * 获取map之中指定key的float值，如果key不存在或者map为null或者空则返回0
     *
     * @param map 指定获取int值的map
     * @param key 从指定map之中获取int值的map
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return 返回指定map之中某个key的float类型值
     */
    public static <K, V> Float getFloatValue(Map<K, V> map, K key) {
        return getFloatValue(map, key, 0F);
    }

    /**
     * 获取map之中指定key的float值，如果key不存在或者map为null或者空则返回给定的默认值defaultValue
     *
     * @param map 指定获取int值的map
     * @param key 从指定map之中获取int值的map
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return 返回指定map之中某个key的float类型值
     */
    public static <K, V> Float getFloatValue(Map<K, V> map, K key, Float defaultValue) {
        return isEmpty(map) ? defaultValue : StringUtil.toFloat(map.get(key), defaultValue);
    }

    /**
     * 获取map之中指定key的double值，如果key不存在或者map为null或者空则返回给定的默认值-1
     *
     * @param map 指定获取int值的map
     * @param key 从指定map之中获取double值的map
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return 返回指定map之中某个key的double类型值
     */
    public static <K, V> Double getDoubleValue(Map<K, V> map, K key) {
        return getDoubleValue(map, key, -1D);
    }

    /**
     * 获取map之中指定key的double值，如果key不存在或者map为null或者空则返回给定的默认值defaultValue
     *
     * @param map          指定获取int值的map
     * @param key          从指定map之中获取double值的map
     * @param defaultValue 给定默认值key不存在或者map为空则返回默认值
     * @param <K>          key的泛型
     * @param <V>          value的泛型
     * @return 返回指定map之中某个key的double类型值
     */
    public static <K, V> Double getDoubleValue(Map<K, V> map, K key, Double defaultValue) {
        return isEmpty(map) ? defaultValue : StringUtil.toDouble(map.get(key), defaultValue);
    }

    /**
     * 获取map之中指定key的long值，如果key不存在或者map为null或者空则返回给定的默认值defaultValue
     *
     * @param map          指定获取int值的map
     * @param key          从指定map之中获取long值的map
     * @param defaultValue 给定默认值key不存在或者map为空则返回默认值
     * @param <K>          key的泛型
     * @param <V>          value的泛型
     * @return 返回指定map之中某个key的long类型值
     */
    public static <K, V> Long getLongValue(Map<K, V> map, K key, Long defaultValue) {
        return isEmpty(map) ? defaultValue : StringUtil.toLong(map.get(key), defaultValue);
    }

    /**
     * 获取map之中指定key的Boolean值，如果key不存在或者map为null或者空则返回给定的默认值defaultValue
     *
     * @param map          指定获取int值的map
     * @param key          从指定map之中获取Boolean值的map
     * @param defaultValue 给定默认值key不存在或者map为空则返回默认值
     * @param <K>          key的泛型
     * @param <V>          value的泛型
     * @return 返回指定map之中某个key的Boolean类型值
     */
    public static <K, V> Boolean getBooleanValue(Map<K, V> map, K key, Boolean defaultValue) {
        return isEmpty(map) ? defaultValue : StringUtil.toBoolean(map.get(key), defaultValue);
    }

    /**
     * 获取List<Map<K, V>>之中第一个Map指定key的String值，如果list为空(list为空或者null)或者map为空(map为null或者为空)则返回空字符串
     *
     * @param list 指定获取String值的List<Map<K, V>>
     * @param key  获取String值的key
     * @param <K>  Map的key泛型
     * @param <V>  Map的value泛型
     * @return 返回String值
     */
    public static <K, V> String getFirstMapStringValue(List<Map<K, V>> list, K key) {
        return getFirstMapStringValue(list, key, "");
    }

    /**
     * 获取List<Map<K, V>>之中第一个Map指定key的String值，如果list为空(list为空或者null)或者map为空(map为null或者为空)则返回默认值defaultValue
     *
     * @param list         指定获取String值的List<Map<K, V>>
     * @param key          获取String值的key
     * @param defaultValue 给定默认值key不存在或者map为空或者list为空则返回默认值
     * @param <K>          Map的key泛型
     * @param <V>          Map的value泛型
     * @return 返回String值
     */
    public static <K, V> String getFirstMapStringValue(List<Map<K, V>> list, K key, String defaultValue) {
        return isEmpty(list) ? defaultValue : getStringValue(list.get(0), key, defaultValue);
    }

    /**
     * 获取List<Map<K, V>>之中第一个Map指定key的String值，如果list为空(list为空或者null)或者map为空(map为null或者为空)则返回默认值defaultValue
     *
     * @param list 指定获取String值的List<Map<K, V>>
     * @param key  获取String值的key
     * @param <K>  Map的key泛型
     * @param <V>  Map的value泛型
     * @return 返回String值
     */
    public static <K, V> Integer getFirstMapIntegerValue(List<Map<K, V>> list, K key) {
        return getFirstMapIntegerValue(list, key, -1);
    }

    /**
     * 获取List<Map<K, V>>之中第一个Map指定key的int值，如果list为空(list为空或者null)或者map为空(map为null或者为空)则返回默认值defaultValue
     *
     * @param list         指定获取String值的List<Map<K, V>>
     * @param key          获取String值的key
     * @param defaultValue 给定默认值key不存在或者map为空或者list为空则返回默认值
     * @param <K>          Map的key泛型
     * @param <V>          Map的value泛型
     * @return 返回int值
     */
    public static <K, V> Integer getFirstMapIntegerValue(List<Map<K, V>> list, K key, Integer defaultValue) {
        return isEmpty(list) ? defaultValue : getIntegerValue(list.get(0), key, defaultValue);
    }

    /**
     * 获取list之中某个下标index的String值,如果list为null或者为空则返回空字符串
     *
     * @param list  获取值的list
     * @param index 获取list之中String值的index
     * @param <E>   List之中元素泛型
     * @return 返回String值
     */
    public static <E> String getStringValue(List<E> list, int index) {
        return getStringValue(list, index, "");
    }

    /**
     * 获取list之中某个下标index的String值,如果list为null或者为空则返回给定的默认值
     *
     * @param list         获取值的list
     * @param index        获取list之中String值的index
     * @param defaultValue 给定默认值
     * @param <E>          List之中元素泛型
     * @return 返回String值
     */
    public static <E> String getStringValue(List<E> list, int index, String defaultValue) {
        AssertUtil.checkCondition(index >= 0, "下标必须大于等于零！");
        return isEmpty(list) ? defaultValue : StringUtil.toString(list.get(index));
    }

    /**
     * 获取list之中某个下标index的int值,如果list为null或者为空则返回-1
     *
     * @param list  获取值的list
     * @param index 获取list之中String值的index
     * @param <E>   List之中元素泛型
     * @return 返回int值
     */
    public static <E> Integer getIntegerValue(List<E> list, int index) {
        return getIntegerValue(list, index, -1);
    }

    /**
     * 获取list之中某个下标index的int值,如果list为null或者为空则返回给定的默认值
     *
     * @param list         获取值的list
     * @param index        获取list之中String值的index
     * @param defaultValue 给定默认值
     * @param <E>          List之中元素泛型
     * @return 返回int值
     */
    public static <E> Integer getIntegerValue(List<E> list, int index, int defaultValue) {
        AssertUtil.checkCondition(index >= 0, "元素下标必须大于等于零！");
        return isEmpty(list) ? defaultValue : StringUtil.toInteger(list.get(index), defaultValue);
    }

    /**
     * 获取list之中某个下标index的float值,如果list为null或者为空则返回-1
     *
     * @param list  获取值的list
     * @param index 获取list之中String值的index
     * @param <E>   List之中元素泛型
     * @return 返回float值
     */
    public static <E> Float getFloatValue(List<E> list, int index) {
        return getFloatValue(list, index, -1);
    }

    /**
     * 获取list之中某个下标index的float值,如果list为null或者为空则返回给定的默认值
     *
     * @param list         获取值的list
     * @param index        获取list之中String值的index
     * @param defaultValue 给定默认值
     * @param <E>          List之中元素泛型
     * @return 返回float值
     */
    public static <E> Float getFloatValue(List<E> list, int index, float defaultValue) {
        AssertUtil.checkCondition(index >= 0, "元素下标必须大于等于零！");
        return isEmpty(list) ? defaultValue : StringUtil.toFloat(list.get(index), defaultValue);
    }

    /**
     * 获取list之中某个下标index的long值,如果list为null或者为空则返回给定的默认值
     *
     * @param list         获取值的list
     * @param index        获取list之中String值的index
     * @param defaultValue 给定默认值
     * @param <E>          List之中元素泛型
     * @return 返回long值
     */
    public static <E> Long getLongValue(List<E> list, int index, long defaultValue) {
        AssertUtil.checkCondition(index >= 0, "元素下标必须大于等于零！");
        return isEmpty(list) ? defaultValue : StringUtil.toLong(list.get(index), defaultValue);
    }

    /**
     * 获取list之中某个下标index的double值,如果list为null或者为空则返回给定的默认值
     *
     * @param list         获取值的list
     * @param index        获取list之中String值的index
     * @param defaultValue 给定默认值
     * @param <E>          List之中元素泛型
     * @return 返回double值
     */
    public static <E> Double getDoubleValue(List<E> list, int index, double defaultValue) {
        AssertUtil.checkCondition(index >= 0, "元素下标必须大于等于零！");
        return isEmpty(list) ? defaultValue : StringUtil.toDouble(list.get(index), defaultValue);
    }

    public static <E> Set<E> forSet(E... elements) {
        Set<E> es = new HashSet<>(elements.length);
        for (E element : elements) {
            es.add(element);
        }
        return es;
    }

    public static <E> List<E> forList(E... elements) {
        List<E> es = new ArrayList<>(elements.length);
        for (E element : elements) {
            es.add(element);
        }
        return es;
    }

    /**
     * 求数组之和
     *
     * @param array 要求和数组
     * @return 返回数组所有int值之和
     */
    public static int getCount(int[] array) {
        if (null == array || array.length == 0) {
            return 0;
        }
        int result = 0;
        for (int e : array) {
            result += e;
        }
        return result;
    }

    /**
     * 获取List<Map<K, V>>之中第一个Map，在queryForList之中使用
     *
     * @param list 指定获取第一个map的list
     * @param <K>  Map的key泛型
     * @param <V>  Map的value泛型
     * @return 返回map，或者当list为空或者null时返回一个空map
     */
    public static <K, V> Map<K, V> getFirstMap(List<Map<K, V>> list) {
        return isEmpty(list) ? new HashMap<>(1) : list.get(0);
    }

    /**
     * 反射获取数组操作对象
     *
     * @param clazz  数组元素类型
     * @param length 数组长度
     * @return 数组操作对象
     */
    private static <E> Object getArrayObject(Class<E> clazz, int length) {
        return Array.newInstance(clazz, length);
    }

    /**
     * 像指定Collection之中添加数组elements所有元素
     *
     * @param collection 指定添加元素的collection
     * @param elements   要添加到collection之中的的数组
     * @param <E>        元素泛型
     */
    public static <E> void addAll(Collection<E> collection, E[] elements) {
        for (int i = 0, size = elements.length; i < size; ++i) {
            collection.add(elements[i]);
        }
    }

    /**
     * 求两个Collection的并集
     *
     * @param a   求并集的Collection
     * @param b   求并集的Collection
     * @param <E> 元素泛型
     * @return 返回Collection并集
     */
    public static <E> Collection<E> union(Collection<E> a, Collection<E> b) {
        Collection<E> copyB = BeanUtil.deepCopy(b);
        Set<E> set = new HashSet<E>(b);
        Iterator<E> iterator = a.iterator();
        while (iterator.hasNext()) {
            E e = iterator.next();
            if (set.add(e)) {
                copyB.add(e);
            }
        }
        return copyB;
    }

    /**
     * 求两个Collection的交集
     *
     * @param a   求并集的Collection
     * @param b   求并集的Collection
     * @param <E> 元素泛型
     * @return 返回Collection交集
     */
    public static <E> Collection<E> intersection(Collection<E> a, Collection<E> b) {
        Collection<E> copyB = BeanUtil.deepCopy(b);
        Set<E> set = new HashSet<E>();
        Iterator<E> iterator = a.iterator();
        while (iterator.hasNext()) {
            E e = iterator.next();
            if (!set.add(e)) {
                copyB.add(e);
            }
        }
        return copyB;
    }

    /**
     * 把数组转换成list
     *
     * @param array 要转换成list的数组
     * @param <E>   数组元素泛型值
     * @return 返回list
     */
    public static <E> List<E> asList(E[] array) {
        List<E> list = new ArrayList<E>();
        addAll(list, array);
        return list;
    }

    /**
     * 可变参数转换为数组
     *
     * @param values 所有的参数
     * @param <E>
     * @return 返回一个数组
     */
    public static <E> E[] toArray(E... values) {
        Class<E> clazz = null;
        for (E value : values) {
            clazz = (Class<E>) value.getClass();
            if (!isNull(values[0])) {
                break;
            }
        }
        AssertUtil.checkNotNull(clazz, "所有参数为null!!!");
        Object array = getArrayObject(clazz, values.length);
        int index = 0;
        for (E value : values) {
            Array.set(array, index++, value);
        }
        return (E[]) array;
    }

    /**
     * 把Collection转换成数组
     *
     * @param collection 要转换数组的collection
     * @param <E>        数组元素泛型值
     * @return 返回数组
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Collection<E> collection) {
        Class<E> type = null;
        for (E e : collection) {
            type = (Class<E>) e.getClass();
            if (!isNull(type)) {
                break;
            }
        }
        AssertUtil.checkNotNull(type, "所有参数为null!!!");
        Object array = getArrayObject(type, collection.size());
        Iterator<E> iterator = collection.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Array.set(array, index++, iterator.next());
        }
        return (E[]) array;
    }
}
