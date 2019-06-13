package com.xin.utils.test;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: http请求工具类(用一句话描述该类做什么)
 * @date 2018-06-21 17:25
 * @Copyright (C)2017 , Luchaoxin
 */

public class HttpUtil {
    private static int timeout = 30000;
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static String post(String paramString, Object paramObject, ParamType paramParamType)
            throws Exception {
        return post(paramString, null, paramObject, paramParamType);
    }

    public static String post(String paramString1, String paramString2)
            throws Exception {
        return post(paramString1, paramString2, null, null);
    }

    public static String post(String paramString1, String paramString2, Object paramObject, ParamType paramParamType)
            throws Exception {
        BasicCookieStore localBasicCookieStore = new BasicCookieStore();
        CloseableHttpClient localCloseableHttpClient = HttpClients.custom().setDefaultCookieStore(localBasicCookieStore).build();
        HttpUriRequest localObject = null;
        HttpPost localHttpPost = new HttpPost(paramString1);
        RequestConfig localRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        localHttpPost.setConfig(localRequestConfig);
        localHttpPost.addHeader("SecurityToken", paramString2);
        if (null != paramObject)
            switch (paramParamType) {
                case Request2Map:
                    localHttpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    localHttpPost.setEntity(new UrlEncodedFormEntity(getParam((Map) paramObject), "UTF-8"));
                    break;
                case Stream2Map:
                    localHttpPost.setHeader("Content-Type", "text/plain;charset=UTF-8");
                    localHttpPost.setEntity(new StringEntity(paramObject.toString(), ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8)));
                    break;
                case Request2Json:
                    localHttpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
                    localHttpPost.setEntity(new StringEntity(paramObject.toString(), ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8)));
            }
        localObject = localHttpPost;
        String str = null;
        HttpResponse localHttpResponse = null;
        localHttpResponse = localCloseableHttpClient.execute(localObject);
        if (localHttpResponse.getStatusLine().getStatusCode() == 200)
            str = EntityUtils.toString(localHttpResponse.getEntity(), "utf-8");
        return str;
    }

    public static String put(String paramString1, String paramString2, String paramString3)
            throws Exception {
        String str = "";
        return str;
    }

    public static String delete(String paramString1, String paramString2, String paramString3)
            throws IOException {
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        localDefaultHttpClient.getParams().setIntParameter("http.socket.timeout", timeout * 1000);
        localDefaultHttpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        String str1 = "";
        try {
            String str2 = "UTF-8";
            HttpEntityEnclosingRequestBase local1HttpDeleteWithBody = new HttpEntityEnclosingRequestBase() {
                public static final String METHOD_NAME = "DELETE";

                public String getMethod() {
                    return "DELETE";
                }
            };
            StringEntity localStringEntity = new StringEntity(paramString3, str2);
            local1HttpDeleteWithBody.addHeader("content-type", "application/json");
            local1HttpDeleteWithBody.addHeader("SecurityToken", paramString2);
            local1HttpDeleteWithBody.setEntity(localStringEntity);
            BasicResponseHandler localBasicResponseHandler = new BasicResponseHandler();
            str1 = new String(((String) localDefaultHttpClient.execute(local1HttpDeleteWithBody, localBasicResponseHandler)).getBytes(), "UTF-8");
        } catch (IOException localIOException) {
            throw localIOException;
        } finally {
            localDefaultHttpClient.getConnectionManager().shutdown();
        }
        return str1;
    }

    protected static List<NameValuePair> getParam(Map<Object, Object> paramMap) {
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = paramMap.keySet().iterator();
        while (localIterator.hasNext()) {
            Object localObject = localIterator.next();
            localArrayList.add(new BasicNameValuePair(localObject.toString(), (String) paramMap.get(localObject)));
        }
        return localArrayList;
    }

    public static String getIpAddr(HttpServletRequest paramHttpServletRequest) {
        String str = paramHttpServletRequest.getHeader("x-forwarded-for");
        if ((str == null) || (str.length() == 0) || ("unknown".equalsIgnoreCase(str))) {
            str = paramHttpServletRequest.getHeader("Proxy-Client-IP");
        }
        if ((str == null) || (str.length() == 0) || ("unknown".equalsIgnoreCase(str))) {
            str = paramHttpServletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if ((str == null) || (str.length() == 0) || ("unknown".equalsIgnoreCase(str))) {
            str = paramHttpServletRequest.getRemoteAddr();
            if ((str.equals("127.0.0.1")) || (str.equals("0:0:0:0:0:0:0:1"))) {
                InetAddress localInetAddress = null;
                try {
                    localInetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException localUnknownHostException) {
                    localUnknownHostException.printStackTrace();
                }
                str = localInetAddress.getHostAddress();
            }
        }
        if ((str != null) && (str.length() > 15) && (str.indexOf(",") > 0))
            str = str.substring(0, str.indexOf(","));
        return str;
    }

    public static String get(String paramString) {
        String str = "";
        CloseableHttpResponse localCloseableHttpResponse = null;
        try {
            HttpGet localHttpGet = new HttpGet(paramString);
            RequestConfig localRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
            localHttpGet.setConfig(localRequestConfig);
            localCloseableHttpResponse = httpClient.execute(localHttpGet);
            HttpEntity localHttpEntity = localCloseableHttpResponse.getEntity();
            str = EntityUtils.toString(localHttpEntity);
            EntityUtils.consume(localHttpEntity);
        } catch (Exception localException2) {
//            ServiceLoLog.error(">>>get>>>>" + localException2.getMessage(), localException2);
        } finally {
            try {
                localCloseableHttpResponse.close();
            } catch (Exception localException4) {
//                ServiceLog.error(">>>get>>>>" + localException4.getMessage(), localException4);
            }
        }
        return str;
    }

    public static String getStringFromRequestStream(ServletInputStream paramServletInputStream)
            throws Exception {
        String str = null;
        try {
            StringBuffer localStringBuffer = new StringBuffer();
            int i = -1;
            byte[] arrayOfByte = new byte[1024];
            while ((i = paramServletInputStream.read(arrayOfByte)) != -1)
                localStringBuffer.append(new String(arrayOfByte, 0, i, "UTF-8"));
            str = localStringBuffer.toString();
        } catch (Exception localException) {
//            ServiceLog.error("请求参数获取失败！" + localException.getMessage(), localException);
        } finally {
            paramServletInputStream.close();
        }
        return str;
    }

    public static enum ParamType {
        Stream2Map, Request2Map, Request2Json;
    }
}