package com.xin.utils.elasticsearch;

import com.google.gson.Gson;
import com.xin.utils.CollectionUtil;
import com.xin.utils.http.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Luchaoxin
 * @Description: ElasticSearch工具类
 * @date 2018-09-04 15:36
 */
public class ElasticSearchUtil {
    /**
     * @param server
     * @param index
     * @param field
     * @param text
     * @return
     * @throws IOException
     */
    public static String analysisString(String server, String index, String field, String text) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append("http://")
                .append(server)
                .append("/")
                .append(index)
                .append("/_analyze?field=")
                .append(field)
                .append("&text=")
                .append(text);
        return HttpUtil.get(url.toString());
    }

    /**
     * 获取服务器上某个index,type的mapping配置
     *
     * @param server ElasticSearch服务器地址如192.168.80.129:9200
     * @param index  ElasticSearch索引
     * @param type   ElasticSearch类型
     * @return 返回json字符串
     * @throws IOException
     */
    public static String getMappingInfo(String server, String index, String type) throws IOException {
        StringBuilder url = new StringBuilder("http://")
                .append(server)
                .append("/")
                .append(index)
                .append("/")
                .append("_mapping")
                .append("/")
                .append(type);
        String json = HttpUtil.get(url.toString());
        Map<String, Object> map = new HashMap<>(1);
        String result = "";
        Gson gson = new Gson();

        map = gson.fromJson(json, map.getClass());
        if (CollectionUtil.isEmpty(map)) {
            return result;
        }

        Map<String, Object> mappings = (Map<String, Object>) map.get(index);
        if (CollectionUtil.isEmpty(mappings)) {
            return result;
        }

        Map<String, Object> articleInfo = (Map<String, Object>) mappings.get("mappings");
        if (CollectionUtil.isEmpty(articleInfo)) {
            return result;
        }

        return CollectionUtil.getStringValue(articleInfo, type);
    }
}
