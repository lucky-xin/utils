package com.xin.utils.kafka;

import com.xin.utils.StringUtil;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: 生成key工具类
 * @date 8:55 2018-06-25
 **/
public class KeyGeneration {

    public static String getKey(Integer hash, String topics, String groupId) {
        StringBuilder key = new StringBuilder().append(hash).append("_");
        for (String topic : topics.split(StringUtil.COLON)) {
            key.append(topic).append("_");
        }
        return key.append("_").append(groupId).toString();
    }


}
