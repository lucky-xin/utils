package com.xin.utils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Luchaoxin
 * @version V 4.0.0
 * @Description java集合, 数组操作util
 * @date 2015年9月27日 下午4:28:50
 */

public class CollectionUtil {

    /**
     * Condition为查询条件接口
     */
    public interface Condition {
        /**
         * 该方法在遍历Collection或数组时调用
         *
         * @param object 为数组元素或Collection元素
         * @return 如何要进行相关操作(如删除该元素)则返回true, 否则返回false
         */
        boolean test(Object object);

        /**
         * 该方法在遍历map时调用
         *
         * @param key   map中的键
         * @param value map中的值
         * @return 如何要进行相关操作(如删除该entry)则返回true, 否则返回false
         */
        boolean test(Object key, Object value);
    }

    /**
     * 默认条件为Collection类或数组类元素为null Map时键或值为null
     */
    private static Condition nullCondition = new Condition() {
        @Override
        public boolean test(Object object) {
            return isNull(object);
        }

        @Override
        public boolean test(Object key, Object value) {
            return isNull(key) || isNull(value);
        }
    };

    /**
     * 默认条件为Collection类或数组类元素为空 Map类时键或值为空
     */
    private static Condition emptyCondition = new Condition() {
        @Override
        public boolean test(Object object) {
            return StringUtil.isNull(object);
        }

        @Override
        public boolean test(Object key, Object value) {
            return StringUtil.isNull(key) || StringUtil.isNull(value);
        }
    };

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
     * 去除数组之中所有null值
     *
     * @param e   需要过滤的数组
     * @param <E> 数组元素泛型
     * @return 返回去除null值之后的数组
     */
    public static <E> E[] trimNull(E[] e) {
        return (E[]) trimWithCondition(e, nullCondition);
    }

    /**
     * 去除数组之中所有null值和空值（toString之后为空）
     *
     * @param e   需要过滤的数组
     * @param <E> 数组元素泛型
     * @return 返回去除null值之后的数组
     */
    public static <E> E[] trimEmpty(E[] e) {
        return (E[]) trimWithCondition(e, emptyCondition);
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
     * Collection的toArray方法返回类型为Object不能转换为其它类型
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] trimWithCondition(E[] e, Condition condition) {
        AssertUtil.checkNotEmpty(e, "数组不能为空");
        AssertUtil.checkNotNull(condition, "condition不能为null");
        ArrayList<E> list = new ArrayList<E>();
        Class<E> type = null;
        for (E element : e) {
            if (!condition.test(element)) {
                list.add(element);
            }
            if (isNull(type) && !isNull(element)) {
                type = (Class<E>) element.getClass();
            }
        }
        if (list.size() == e.length) {
            return e;
        }
        StringBuilder message = new StringBuilder("获取数组类型失败，请检查数组：").append(Arrays.toString(e));
        AssertUtil.checkNotNull(type, message.toString());
        return toArray(list);
    }

    /**
     * 清除 Map<K,V> 所有为key和value为 null 的元素
     *
     * @param map
     */
    public static <K, V> void trimNull(Map<K, V> map) {
        trimWithCondition(map, nullCondition);
    }

    /**
     * 清除 Map<K,V> 所有为key和value为 null 和value为空的元素
     *
     * @param map
     */
    public static <K, V> void trimEmpty(Map<K, V> map) {
        trimWithCondition(map, emptyCondition);
    }

    public static <K, V> void trimWithCondition(Map<K, V> map, Condition condition) {
        AssertUtil.checkNotEmpty(map, "map must not be empty!");
        AssertUtil.checkNotNull(condition, "condition must not be null!");
        doTrimWithCondition(map, condition);
    }

    public static <E> void trimNull(Collection<E> collection) {
        trimWithCondition(collection, nullCondition);
    }

    public static <E> void trimEmpty(Collection<E> collection) {
        trimWithCondition(collection, emptyCondition);
    }

    public static <E> void trimWithCondition(Collection<E> collection, Condition condition) {
        AssertUtil.checkNotEmpty(collection, "collection must not be empty!");
        AssertUtil.checkNotNull(condition, "condition must not be null!");
        doTrimWithCondition(collection, condition);
    }

    /**
     * 递归Map里集合和递归list里的集合进行操作,根据Condition对元素进行删除
     */
    @SuppressWarnings({"rawtypes"})
    private static void doTrimWithCondition(Object object, Condition condition) {
        Iterator iterator = null;
        if (object instanceof Map) {
            iterator = ((Map) object).entrySet().iterator();
        } else if (object instanceof Collection) {
            iterator = ((Collection) object).iterator();
        } else {
            return;
        }

        while (iterator.hasNext()) {
            Object element = iterator.next();

            if ((object instanceof Collection) && condition.test(element)) {
                iterator.remove();
            } else if ((object instanceof Map) && (element instanceof Map.Entry)) {

                Entry entry = (Entry) element;
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (condition.test(key, value)) {
                    iterator.remove();
                } else {
                    doTrimWithCondition(value, condition);
                }
            } else {
                doTrimWithCondition(element, condition);
            }
        }
    }

    /**
     * 删除Collection里的null元素,当map的value为数组时，并删除数组里所有null元素
     *
     * @param collection
     */
    public static <E> void removeNull(Collection<E[]> collection) {
        removeWhitCondition(collection, nullCondition);
    }

    /**
     * 删除Collection里的空元素,当map的value为数组时，并删除数组里所有空元素
     *
     * @param collection
     */
    public static <E> void removeEmpty(Collection<E[]> collection) {
        removeWhitCondition(collection, emptyCondition);
    }

    public static <E> void removeWhitCondition(Collection<E[]> collection, Condition condition) {
        AssertUtil.checkNotEmpty(collection, "collection must not be empty!");
        AssertUtil.checkNotNull(condition, "condition must not be null!");
        doRemoveWhitCondition(collection, condition);
    }

    /**
     * 删除map里的null,当map的value为数组时，并删除数组里所有null元素
     *
     * @param map
     */
    public static <K, V> void removeNull(Map<K, V[]> map) {
        removeWhitCondition(map, nullCondition);
    }

    /**
     * 删除map里的空元素,当map的value为数组时，并删除数组里所有空元素
     *
     * @param map
     */
    public static <K, V> void removeEmpty(Map<K, V[]> map) {
        removeWhitCondition(map, emptyCondition);
    }

    public static <K, V> void removeWhitCondition(Map<K, V[]> map, Condition condition) {
        AssertUtil.checkNotEmpty(map, "map must not be empty!");
        AssertUtil.checkNotNull(condition, "condition must not be null!");
        doRemoveWhitCondition(map, condition);
    }

    /**
     * 递归Map里集合和递归list里的集合进行操作,根据Condition对元素进行删除
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void doRemoveWhitCondition(Object instance, Condition condition) {
        Iterator iterator = null;
        Object tempObject = null;
        if (instance instanceof Map) {
            iterator = ((Map) instance).entrySet().iterator();
            tempObject = DeepCopyUtil.deepCopy(instance);
            ((Map) tempObject).clear();
        } else if (instance instanceof Collection) {
            iterator = ((Collection) instance).iterator();
            tempObject = DeepCopyUtil.deepCopy(instance);
            ((Collection) tempObject).clear();
        } else {
            return;
        }

        while (iterator.hasNext()) {
            Object element = iterator.next();
            if (instance instanceof Map) {
                Entry entry = (Entry) element;
                Object key = entry.getKey();
                Object obj = entry.getValue();
                if (condition.test(key, obj)) {
                    iterator.remove();
                } else if (obj instanceof Object[]) {
                    Object[] array = (Object[]) obj;
                    Object[] result = trimWithCondition(array, condition);
                    if (result.length != array.length) {
                        ((Map) tempObject).put(key, result);
                        iterator.remove();
                    }
                }
            } else {
                if (condition.test(element)) {
                    iterator.remove();
                } else if (element instanceof Object[]) {
                    Object[] elements = (Object[]) element;
                    Object[] result = trimWithCondition(elements, condition);
                    if (result.length != elements.length) {
                        ((Collection) tempObject).add(result);
                        iterator.remove();
                    }
                }
            }
        }

        if (instance instanceof Map) {
            ((Map) instance).putAll((Map) tempObject);
        } else {
            ((Collection) instance).addAll((Collection) tempObject);
        }
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
        Collection<E> copyB = DeepCopyUtil.deepCopy(b);
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
        Collection<E> copyB = DeepCopyUtil.deepCopy(b);
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

    /**
     * 去除list中的重复元素
     *
     * @param list
     */
    public static <E> void trimMultiple(List<E> list) {

        Condition condition = new Condition() {
            Set<Object> set = new HashSet<Object>();

            @Override
            public boolean test(Object object) {
                return !set.add(object);
            }

            @Override
            public boolean test(Object key, Object value) {
                return false;
            }
        };
        trimWithCondition(list, condition);
    }

    public interface MapFilter<K, V> {

        /**
         * 该方法在遍历map时调用
         *
         * @param key   map中的键
         * @param value map中的值
         * @return 如何要进行相关操作(如删除该entry)则返回true, 否则返回false
         */
        boolean test(K key, V value);

        /**
         * test方法返回true时执行operator方法
         *
         * @param iterator while循环中的迭代器
         * @param entry    while循环中的Entry
         */
        void operator(Iterator<Entry<K, V>> iterator, Entry<K, V> entry);
    }

    public interface ListFilter<T> {

        /**
         * 该方法在遍历list,set时调用
         *
         * @param value 遍历获取的元素值
         * @return 如果返回true则调用ListFilter接口的operator方法
         */
        boolean test(T value);

        /**
         * test方法返回true时执行operator方法
         *
         * @param iterator while循环中的迭代器
         * @param value    while循环中的元素值
         */
        void operator(Iterator<T> iterator, T value);
    }

    /**
     * 根据{@link ListFilter}过滤collection之中元素，如果ListFilter的方法test返回true则调用operator方法
     *
     * @param collection 需要过滤元素的Collection
     * @param filter     过滤操作接口
     * @param <T>        collection元素泛型
     */
    public static <T> void filter(Collection<T> collection, ListFilter<T> filter) {
        if (isEmpty(collection)) {
            return;
        }
        AssertUtil.checkNotNull(filter, "ListFilter 接口不能为空。");
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T value = iterator.next();
            if (filter.test(value)) {
                filter.operator(iterator, value);
            }
        }
    }

    /**
     * 根据{@link MapFilter}过滤map之中元素，如果MapFilter的方法test返回true则调用operator方法
     *
     * @param params 需要过滤元素的Map
     * @param filter 过滤操作接口
     * @param <K>    Map的key泛型
     * @param <V>    Map的value泛型
     */
    public static <K, V> void filter(Map<K, V> params, MapFilter<K, V> filter) {
        if (isEmpty(params)) {
            return;
        }
        AssertUtil.checkNotNull(filter, "MapFilter 接口不能为空。");
        Iterator<Entry<K, V>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            K key = entry.getKey();
            V value = entry.getValue();
            if (filter.test(key, value)) {
                filter.operator(iterator, entry);
            }
        }
    }

    public static <K> void filterIntValue(final Map<K, Object> params, K[] keys, final int defaultValue) {
        if (isEmpty(params)) {
            return;
        }
        AssertUtil.checkNotEmpty(keys, "map的key数组不能为空。");
        final Set<K> set = new HashSet<K>();
        for (K key : keys) {
            set.add(key);
        }

        MapFilter<K, Object> filterInt = new MapFilter<K, Object>() {

            @Override
            public boolean test(K key, Object value) {
                return !set.add(key);
            }

            @Override
            public void operator(Iterator<Entry<K, Object>> iterator, Entry<K, Object> entry) {
                entry.setValue(CollectionUtil.getIntegerValue(params, entry.getKey(), defaultValue));
            }
        };

        filter(params, filterInt);
    }


    /**
     * 过滤List<Map<K, V>> 之中所有map的某一个key的value是否已经重复，如果已经重复则去除,使用场景如数据库某一列有重复字段，
     * 但是接下来的业务需要对这一列去重
     *
     * @param list      需要过滤的List<Map<K, V>>
     * @param uniqueKey 需要过滤的key
     * @param <K>       Map的key泛型
     * @param <V>       Map的value泛型
     * @return 返回key对应的value没有重复值的List
     */
    public static <K, V> Stream<Map<K, V>> filter(List<Map<K, V>> list, K uniqueKey) {
        Set<String> set = new HashSet<>();
        return list.stream().filter(map -> set.add(CollectionUtil.getStringValue(map, uniqueKey)));
    }

    /**
     * 对List<Map<K, V>>之中所有map,指定某个key的value为新map的value,指定另外一个key的value为新map的value，返回这个map
     *
     * @param list     生成新map的List<Map<K, V>>
     * @param mapKey   返回map的key对应于List<Map<K, V>>之中的map的key为mapKey的值
     * @param valueKey 返回map的value对应于List<Map<K, V>>之中的map的key为mapKey的值
     * @param <K>      Map的key泛型
     * @param <V>      Map的value泛型
     * @return 返回map
     */
    public static <K, V> Map<String, String> toMap(List<Map<K, V>> list, K mapKey, K valueKey) {
        return list.stream().collect(Collectors.toMap(map -> getStringValue(map, mapKey), map -> getStringValue(map, valueKey)));
    }
}
