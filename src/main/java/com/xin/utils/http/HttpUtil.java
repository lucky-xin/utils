package com.xin.utils.http;

import com.xin.utils.CollectionUtil;
import com.xin.utils.StringUtil;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: http工具类
 * @date 15:37 2018-07-02
 **/

public class HttpUtil {

    /**
     * 请求超时时间
     */
    private static int timeout = 15000 * 2;

    /**
     * 请求成功码
     */
    private static int succeed = 200;

    private static String UNKNOWN = "unknown";

    private static String LOCAL_HOST = "127.0.0.1";

    private static String IP_ADDRESS = "0:0:0:0:0:0:0:1";

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public enum ParamType {
        //请求参数为map
        Request2Map,
        //请求参数为json
        Request2Json
    }

    /**
     * 以post方式发送请求
     *
     * @param url   请求url
     * @param param 请求参数
     * @param type  请求类型请看{@link ParamType}
     * @return 返回请求结果
     * @throws Exception
     */
    public static String post(String url, Object param, ParamType type) throws Exception {
        return post(url, null, param, type);
    }

    /**
     * 以post方式发送请求
     *
     * @param url     请求url
     * @param param   请求参数
     * @param type    请求类型请看{@link ParamType}
     * @param headMap 请求头key头部参数名，value为该头部值
     * @return 返回请求结果
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String post(String url, Map<String, String> headMap, Object param, ParamType type) throws Exception {
        BasicCookieStore cookieStore = new BasicCookieStore();

        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        HttpUriRequest request = null;
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();

        httpPost.setConfig(requestConfig);
        if (headMap != null) {
            headMap.forEach(httpPost::addHeader);
        }
        if (null != param) {
            switch (type) {
                case Request2Map:
                    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httpPost.setEntity(new UrlEncodedFormEntity(getParam((Map<Object, Object>) param), "UTF-8"));
                    break;
                case Request2Json:
                    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
                    httpPost.setEntity(new StringEntity(param.toString(), ContentType.create(
                            ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8)));
                default:
                    break;
            }
        }

        request = httpPost;
        String result = null;
        HttpResponse response = null;
        response = client.execute(request);

        if (response.getStatusLine().getStatusCode() == succeed) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        }

        return result;
    }

    /**
     * 建发送参数
     */
    protected static List<NameValuePair> getParam(Map<Object, Object> parameterMap) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        for (Object key : parameterMap.keySet()) {
            param.add(new BasicNameValuePair(key.toString(), (String) parameterMap.get(key)));
        }
        return param;
    }

    /**
     * 获取HttpServletRequest之中的ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCAL_HOST.equals(ipAddress) || IP_ADDRESS.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                }
            }
        }
        int ipMaxLen = 15;
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > ipMaxLen) {
            // = 15
            if (ipAddress.indexOf(StringUtil.COLON) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 以get方式发送请求
     *
     * @param url 请求url
     * @return 请求结果
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * 以get方式发送请求
     *
     * @param url     url 请求url
     * @param headMap 请求头参数，请求头key头部参数名，value为该头部值
     * @return
     * @throws IOException
     */
    public static String get(String url, Map<String, String> headMap) throws IOException {
        String content = "";
        CloseableHttpResponse response = null;

        try {
            HttpGet get = new HttpGet(url);
            if (!CollectionUtil.isEmpty(headMap)) {
                headMap.forEach(get::setHeader);
            }

            RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .build();

            get.setConfig(config);
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }

        return content;
    }
}
