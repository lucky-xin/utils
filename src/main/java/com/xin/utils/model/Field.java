package com.xin.utils.model;

import com.xin.utils.AssertUtil;
import com.xin.utils.StringUtil;
import org.dom4j.Element;

/**
 * @author Luchaoxin
 * @Description: schema.xml文件解析之后的field属性，对应elasticsearch的document的字段
 * @date 2018-08-30 22:24
 */
public class Field {

    private String defaultFormat = "yyy-MM-dd'T'HH:mm:ss";

    private String name;

    private String desc;

    private String type;

    private int length;

    private boolean index;

    private boolean store;

    private boolean analyze;

    private String defaultVal;

    private float boost;

    private String analyzer;

    private String searchAnalyzer;

    private String format;

    public Field(Element field) {

        this.name = field.attributeValue("name");
        AssertUtil.checkNotEmpty(name, "字段名称不能为空!");
        this.desc = field.attributeValue("desc");

        this.type = field.attributeValue("type");
        AssertUtil.checkNotEmpty(type, "字段类型不能为空!");

        this.length = StringUtil.toInteger(field.attributeValue("length"), 10);

        this.boost = StringUtil.toInteger(field.attributeValue("boost"), 1);

        this.index = StringUtil.toBoolean(field.attributeValue("index"), true);

        this.store = StringUtil.toBoolean(field.attributeValue("store"), false);

        this.analyze = StringUtil.toBoolean(field.attributeValue("analyze"), false);

        this.defaultVal = StringUtil.toString(field.attributeValue("defaultVal"));

        String primaryKey = field.attributeValue("primary-key");

        defaultVal = getPrimaryKeyValue(primaryKey);

        this.analyzer = StringUtil.toString(field.attributeValue("analyzer"), "standard");

        this.searchAnalyzer = StringUtil.toString(field.attributeValue("search_analyzer"), analyzer);

        this.format = field.attributeValue("format");

        this.format = ("date".equalsIgnoreCase(this.type) && StringUtil.isEmpty(format)) ? defaultFormat : format;
    }

    private String getPrimaryKeyValue(String primaryKey) {
        if (null == primaryKey) {
            return "";
        }
        switch (primaryKey) {
            case "uuid":
                return StringUtil.getUUID();
            case "orderId":
                return StringUtil.getOrderId();
            default:
                break;
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public boolean isAnalyze() {
        return analyze;
    }

    public Object getDefaultVal() {
        return defaultVal;
    }


    public float getBoost() {
        return boost;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public String getFormat() {
        return format;
    }

    public boolean isIncludeInAll() {
        //TODO
        return true;
    }

    public boolean isSorted() {
        //todo
        return false;
    }
}
