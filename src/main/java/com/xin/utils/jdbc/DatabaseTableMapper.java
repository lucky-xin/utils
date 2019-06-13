package com.xin.utils.jdbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.xin.utils.log.LogFactory;
import com.xin.utils.AssertUtil;
import com.xin.utils.CollectionUtil;
import com.xin.utils.StringUtil;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 数据库表映射工具类, 统一读取映射文件抽象类
 * @date 2018年6月6日 下午3:42:55
 * @Copyright (C)2018 , Suntektech
 */
public class DatabaseTableMapper {

    private String mapperFilePath;

    /**
     * js脚本引擎
     */
    private static ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("javascript");

    /**
     * 所有字段映射信息,map信息如下：
     * 保存联网平台字段类型key{@link DatabaseTableMapper#VNMP_COLUMN_TYPE_KEY}
     * 保存联网平台字段key{@link DatabaseTableMapper#VNMP_COLUMN_KEY}
     * 保存一机一档所对应的联网平台字段key{@link DatabaseTableMapper#OMOF_COLUMN_KEY}
     */
    private List<Map<String, String>> mappingInfo = Lists.newArrayList();

    /**
     * 字段映射信息 key联网平台数据库字段 -> value 一机一档数据库字段
     */
    private Map<String, String> columnInfo = Maps.newHashMap();

    /**
     * 保存所有值信息 key(联网平台字段0 -> value(对应的值)
     */
    private Map<String, String> values = Maps.newHashMap();

    /**
     * 所有js函数信息 key(联网平台列名) -> 保存的js函数信息的map
     * map保存的函数信息:
     * 函数名称key为 {@link DatabaseTableMapper#JS_FUNCTION_NAME_KEY}
     * 函数参数key为 {@link DatabaseTableMapper#JS_FUNCTION_PARAMS_KEY}
     */
    private Map<String, Map<String, String>> functions = Maps.newHashMap();

    /**
     * 保存联网平台和一机一档唯一区分列名和主键信息
     */
    private Map<String, String> uniqueInfo = Maps.newHashMap();

    private List<Map<String, String>> primaryKeys = Lists.newArrayList();

    protected static final Logger logger = LogFactory.getLogger("DatabaseTableMapper");

    /**
     * 联网平台表名
     */
    private String tableName;

    public DatabaseTableMapper(String tableName, String mapperFilePath) {
        AssertUtil.checkNotEmpty(tableName, "the tableName must not be null.");
        AssertUtil.checkNotEmpty(mapperFilePath, "the mapperFilePath must not be null.");
        this.tableName = tableName;
        this.mapperFilePath = mapperFilePath;
        init();
    }

    /**
     * 保存于{@link DatabaseTableMapper#uniqueInfo} 之中联网平台唯一字段的key
     **/
    public static final String VNMP_UNIQUE_KEY = "vnmpUnique";

    /**
     * 保存于{@link DatabaseTableMapper#uniqueInfo}之中一机一档唯一字段的key
     **/
    public static final String OMOF_UNIQUE_KEY = "omofUnique";

    /**
     * 保存于{@link DatabaseTableMapper#uniqueInfo}之中唯一字段类型的key
     **/
    public static final String UNIQUE_TYPE_KEY = "unique-type";

    public static final String PRIMARY_KEY_COLUMN_KEY = "primary-key";

    public static final String PRIMARY_KEY_VALUE_KEY = "primary-value";

    /**
     * 保存于map(map为{@link DatabaseTableMapper#functions}中的元素)之中js函数名称key
     **/
    public static final String JS_FUNCTION_NAME_KEY = "funName";

    /**
     * 保存于map(map为{@link DatabaseTableMapper#functions}中的元素)之中js函数参数key
     **/
    public static final String JS_FUNCTION_PARAMS_KEY = "params";

    /**
     * xml配置文件中保存联网平台对应字段节点名称 如摄像机名称表示为 vnmp="VIDEONAME"
     **/
    public static final String VNMP_COLUMN_KEY = "vnmp";

    /**
     * xml配置文件中保存一机一档对应字段名称
     **/
    public static final String OMOF_COLUMN_KEY = "omof";

    /**
     * xml配置文件中保存联网平台字段类型key
     **/
    public static final String VNMP_COLUMN_TYPE_KEY = "type";

    public static final String NEXT_ID_VALUE = "$nextId";

    public static final String JS_FUNCTION_PARAM_REGEX = NEXT_ID_VALUE.substring(0, 1);

    public static final String PRIMARY_KEY_VALUE_REGEX = NEXT_ID_VALUE.substring(0, 1);

    public static final String INTEGER_TYPE_NAME = "Integer";

    protected Set<String> jsFiles = Sets.newHashSet("META-INF/function/function.js");

    private void init() {
        try {
            if (StringUtil.isEmpty(mapperFilePath)) {
                throw new RuntimeException("获取映射配置xml失败。");
            }
            Document document = null; //Dom4jUtil.read(mapperFilePath);
//            if (document == null) {
//                throw new RuntimeException("读取映射文件出错！");
//            }

            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> videopointinfo = root.elements(tableName);
            for (Element e : videopointinfo) {
                String vnmpColumn = e.attributeValue(VNMP_COLUMN_KEY);
                String valueType = e.attributeValue(VNMP_COLUMN_TYPE_KEY);
                String unique = e.attributeValue("unique");

                String omofColumn = e.attributeValue(OMOF_COLUMN_KEY);
                String value = e.attributeValue("value");
                String function = e.attributeValue("function");
                String primaryKey = e.attributeValue(PRIMARY_KEY_COLUMN_KEY);
                /** 只能是1.vnmp和value 2.vnmp和omof或function **/
                if (StringUtil.isEmpty(vnmpColumn)
                        || (StringUtil.isEmpty(value) && StringUtil.isEmpty(omofColumn) && StringUtil.isEmpty(function))
                        || (!StringUtil.isEmpty(value) && (!StringUtil.isEmpty(omofColumn) || !StringUtil.isEmpty(function)))
                        || (!StringUtil.isEmpty(primaryKey) && !StringUtil.isEmpty(unique))) {
                    throw new RuntimeException("xml配置文件：" + mapperFilePath + "vnmp=" + vnmpColumn
                            + " 出现错误。配置的xml文件不对,只能是1.vnmp和value 2.vnmp和omof或function "
                            + " 3.同一列之中primary-key和unique不能同时存在。");
                }

                // 1.获取函数信息
                if (!StringUtil.isEmpty(function)) {
                    String params = StringUtil.toString(e.attributeValue("params"));
                    if (!StringUtil.isEmpty(function)) {
                        Map<String, String> funInfo = new HashMap<String, String>();
                        funInfo.put(JS_FUNCTION_PARAMS_KEY, params);
                        funInfo.put(JS_FUNCTION_NAME_KEY, function);
                        functions.put(vnmpColumn, funInfo);
                    }
                }
                // 2.获取直接设置值的字段信息
                if (!StringUtil.isEmpty(value)) {
                    values.put(vnmpColumn, value);
                } else {
                    String type = "";
                    if ("string".equalsIgnoreCase(valueType)) {
                        type = "String";
                    }
                    if ("int".equals(valueType) || "Integer".equalsIgnoreCase(valueType)) {
                        type = INTEGER_TYPE_NAME;
                    }

                    // 3.获取联网平台和一机一档一一对应字段信息
                    if (!StringUtil.isEmpty(unique) && "true".equals(unique)) {
                        if (null != uniqueInfo.get(VNMP_UNIQUE_KEY) || null != uniqueInfo.get(OMOF_UNIQUE_KEY)) {
                            throw new RuntimeException("vnmp=" + vnmpColumn + " 出现错误。只能有一个唯一字段");
                        }
                        uniqueInfo.put(VNMP_UNIQUE_KEY, vnmpColumn);
                        uniqueInfo.put(OMOF_UNIQUE_KEY, omofColumn);
                        uniqueInfo.put(UNIQUE_TYPE_KEY, type);
                    }

                    // 4.获取联网平台数据表主键信息
                    if (!StringUtil.isEmpty(primaryKey) && "true".equals(primaryKey)) {
                        Map<String, String> primaryInfo = Maps.newHashMap();
                        primaryInfo.put(PRIMARY_KEY_COLUMN_KEY, vnmpColumn);
                        String val = !StringUtil.isEmpty(function) ? function : omofColumn;
                        primaryInfo.put(PRIMARY_KEY_VALUE_KEY, val);
                        primaryKeys.add(primaryInfo);
                    }

                    // 5.获取字段映射信息
                    if (StringUtil.isEmpty(unique) && StringUtil.isEmpty(primaryKey)) {
                        Map<String, String> info = Maps.newHashMap();
                        info.put(VNMP_COLUMN_KEY, vnmpColumn);
                        info.put(OMOF_COLUMN_KEY, omofColumn);
                        info.put(VNMP_COLUMN_TYPE_KEY, type);
                        mappingInfo.add(info);
                    }

                    if (!StringUtil.isEmpty(vnmpColumn) && !StringUtil.isEmpty(omofColumn)) {
                        columnInfo.put(vnmpColumn, omofColumn);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("读取jar文件中的 映射配置信息出错", e);
        }
    }

    public Map<String, String> getUniqueInfo() {
        return uniqueInfo;
    }

    public List<Map<String, String>> getMappingInfo() {
        return mappingInfo;
    }

    public Map<String, String> getColumnInfo() {
        return columnInfo;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public Map<String, Map<String, String>> getFunctions() {
        return functions;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Map<String, String>> getPrimaryKeys() {
        return primaryKeys;
    }

    public Map<String, String> getJSFunctionInfo(String columnName) {
        return functions.get(columnName);
    }

    protected void addJsFunFile(String... files) throws ScriptException, IOException, DocumentException {
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        for (String file : files) {
            jsEngine.eval(new InputStreamReader(new FileInputStream(file)));
        }
    }

    /**
     * 使用脚本引擎执行js函数
     *
     * @param funName js函数
     * @param params  js参数
     * @return
     * @throws NoSuchMethodException
     * @throws ScriptException
     */
    public static Object invokeJsFun(String funName, Object[] params) throws NoSuchMethodException, ScriptException {
        return ((Invocable) jsEngine).invokeFunction(funName, params);
    }
}
