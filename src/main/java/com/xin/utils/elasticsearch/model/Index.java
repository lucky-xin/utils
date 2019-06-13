package com.xin.utils.elasticsearch.model;

import com.xin.utils.StringUtil;
import com.xin.utils.model.Field;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luchaoxin
 * @Description: elasticsearch index封装类
 * @date 2018-08-30 22:23
 */
public class Index {

    private String name;

    private String type;

    private Field primaryKeyField;

    private int shards;

    private int replicas;

    private List<Field> fields = new ArrayList();

    private Map<String, Field> metadata = new HashMap<>();

    public Index(Element element) {

        this.name = element.attributeValue("name");

        this.type = element.attributeValue("type");

        this.shards = StringUtil.toInteger(element.attributeValue("shards"), 5);

        this.replicas = StringUtil.toInteger(element.attributeValue("replicas"), 1);

        List<Element> columns = element.elements("field");
        Field field;
        for (Element column : columns) {
            field = new Field(column);
            this.fields.add(field);
            metadata.put(field.getName(), field);

            if (!StringUtil.isNull(column.attributeValue("primary-key"))) {
                this.primaryKeyField = new Field(column);
            }
        }
    }

    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        this.shards = shards;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Field getPrimaryKey() {
        return this.primaryKeyField;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public Map<String, Field> getMetaData() {
        return metadata;
    }

    public Field getField(String fieldName) {
        return metadata.get(fieldName);
    }

    public List<String> validate(Map<String, Object> data, String[] fields) {
        List<String> list = new ArrayList();


        return list;
    }

}
