package com.xin.utils;

import com.alibaba.fastjson.JSON;

/**
 * 增强pojo的toJson能力
 *
 * @author 岑忠满
 */
public class JsonAble {
    public String toJson(){
        return JSON.toJSONString(this);
    }
}
