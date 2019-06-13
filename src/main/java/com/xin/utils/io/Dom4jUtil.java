package com.xin.utils.io;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Luchaoxin
 * @Description: Dom4j操作xml工具类
 * @date 2018-09-02 11:33
 */
public class Dom4jUtil {

    public static Document read(InputStream is) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(is);
        return document;
    }

    public static Document read(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(fileName));
        return document;
    }

    public static String mapToXml(Map<String, Object> map, String root, int msgId) {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");

        buf.append("<" + root + " id='" + msgId + "'>\r\n");
        appendMap2Xml(buf, map);
        buf.append("</" + root + ">");
        return buf.toString();
    }

    private static void appendMap2Xml(StringBuffer buf, Map<String, Object> map) {
        for (Map.Entry entry : map.entrySet()) {
            Object value = entry.getValue();

            if ((value instanceof Map)) {
                buf.append("<" + (String) entry.getKey() + ">\r\n");
                appendMap2Xml(buf, (Map) value);
                buf.append("</" + (String) entry.getKey() + ">\r\n");
            } else {
                buf.append("<" + (String) entry.getKey() + ">");
                buf.append(entry.getValue());
                buf.append("</" + (String) entry.getKey() + ">\r\n");
            }
        }
    }
}
