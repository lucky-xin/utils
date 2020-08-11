package com.xin.utils.http;

import com.xin.utils.CollectionUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.file.FileUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.io.File.separator;

/**
 * @author Luchaoxin, cenzhongman
 * @version V1.0
 * @Description: http工具类
 * @date 15:37 2018-07-02
 **/

public class HttpUtil {

    private static String defaultCharset = "utf-8";

    private static Logger logger = Logger.getLogger(HttpUtil.class);

    /**
     * 发送http get请求
     */
    public static String get(String url, Map<String, String> headers) throws IOException, URISyntaxException {
        return get(url, headers, defaultCharset);
    }


    /**
     * 发送http get请求
     */
    public static String get(String url, HttpHost proxy) throws IOException {
        return get(url, null, defaultCharset, proxy);
    }

    /**
     * 发送http get请求
     */
    public static String get(String url, Map<String, String> headers, HttpHost proxy) throws IOException {
        return get(url, headers, defaultCharset, proxy);
    }

    public static String get(String url, Map<String, String> headers, String charset) throws IOException {
        return get(url, headers, charset, null);
    }

    /**
     * 发送http get请求
     */
    public static String get(String url, Map<String, String> headers, String charset, HttpHost proxy) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpGet httpGet = new HttpGet(url);
        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpGet::setHeader);
        }
        return doRequest(httpGet, charset, proxy);
    }

    /**
     * 发送http get请求
     */
    public static String get(String url, String charset) throws IOException {
        return get(url, null, charset);
    }

    /**
     * 发送http get请求
     */
    public static String get(String url) throws IOException {
        return get(url, null, defaultCharset);
    }

    /**
     * 发送http get请求
     */
    public static String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException, URISyntaxException {
        return get(url, headers, params, defaultCharset);
    }

    /**
     * 发送http get请求 带参数
     */
    public static String get(String url, Map<String, String> headers, Map<String, String> params, String charset) throws IOException, URISyntaxException {
        if (charset == null) {
            charset = defaultCharset;
        }

        URIBuilder uriBuilder = new URIBuilder(url);
        HttpGet httpGet = null;
        if (!CollectionUtil.isEmpty(params)) {
            params.forEach(uriBuilder::setParameter);
            httpGet = new HttpGet(uriBuilder.build());
        } else {
            httpGet = new HttpGet(url);
        }
        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpGet::setHeader);
        }
        return doRequest(httpGet, charset, null);
    }


    /**
     * 发送  post 请求，参数以form表单键值对的形式提交。
     */
    public static String post(String url, Map<String, Object> params, String charset) throws IOException {
        return post(url, params, null, charset);
    }

    /**
     * 发送  post 请求，参数以form表单键值对的形式提交。
     */
    public static String post(String url, Map<String, Object> params) throws IOException {
        return post(url, params, null, defaultCharset);
    }

    /**
     * 发送  post 请求，参数以form表单键值对的形式提交。
     */
    public static String post(String url, HttpHost proxy) throws IOException {
        return post(url, "", proxy);
    }

    /**
     * 发送 post 请求，参数以form表单键值对的形式提交。
     */
    public static String post(String url) throws IOException {
        return post(url, "", null, defaultCharset);
    }

    /**
     * 发送  post 请求，参数以form表单键值对的形式提交。
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers, String charset) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpPost httpPost = new HttpPost(url);

        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpPost::setHeader);
        }

        //组织请求参数
        List<NameValuePair> paramList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(params)) {
            params.forEach((key, value) -> paramList.add(new BasicNameValuePair(key, StringUtil.toString(value))));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(paramList, charset));

        return doRequest(httpPost, charset, null);
    }


    /**
     * 发送  post 请求，参数以json字符串进行提交
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String post(String url, String json, HttpHost proxy) throws IOException {
        return post(url, json, null, defaultCharset, proxy);
    }


    /**
     * 发送 post 请求，参数以json字符串进行提交
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String post(String url, String json) throws IOException {
        return post(url, json, null, defaultCharset);
    }

    /**
     * 发送  post 请求，参数以json字符串进行提交
     *
     * @param url
     * @param json
     * @param headers
     * @return
     * @throws IOException
     */
    public static String post(String url, String json, Map<String, String> headers) throws IOException {
        return post(url, json, headers, defaultCharset);
    }

    /**
     * 发送  post 请求，参数以json字符串进行提交
     *
     * @param url
     * @param json
     * @param headers
     * @return
     * @throws IOException
     */
    public static String post(String url, String json, Map<String, String> headers, HttpHost proxy) throws IOException {
        return post(url, json, headers, defaultCharset, proxy);
    }

    /**
     * 发送  post 请求，参数以json字符串进行提交
     *
     * @param url
     * @param json
     * @param headers
     * @param charset
     * @return
     * @throws IOException
     */
    public static String post(String url, String json, Map<String, String> headers, String charset) throws IOException {
        return post(url, json, headers, charset, null);
    }

    /**
     * 发送  post 请求，参数以json字符串进行提交
     *
     * @param url
     * @param json
     * @param headers
     * @param charset
     * @return
     * @throws IOException
     */
    public static String post(String url, String json, Map<String, String> headers, String charset, HttpHost proxy) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpPost httpPost = new HttpPost(url);

        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpPost::setHeader);
        }
        //组织请求参数
        StringEntity stringEntity = new StringEntity(json, charset);
        httpPost.setEntity(stringEntity);
        String content = doRequest(httpPost, charset, proxy);

        return content;
    }

    /**
     * 发送  put 请求，参数以原生字符串进行提交
     *
     * @param url
     * @return
     */
    public static String put(String url, String json) throws IOException {
        return post(url, json, null, defaultCharset);
    }

    /**
     * 发送  put 请求，参数以原生字符串进行提交
     *
     * @param url
     * @param charset
     * @return
     */
    public static String put(String url, String json, String charset) throws IOException {
        return post(url, json, null, charset);
    }

    /**
     * 发送  put 请求，参数以原生字符串进行提交
     *
     * @param url
     * @param charset
     * @return
     */
    public static String put(String url, String json, Map<String, String> headers, String charset) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpPut httpPut = new HttpPut(url);

        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpPut::setHeader);
        }

        //组织请求参数
        StringEntity stringEntity = new StringEntity(json, charset);
        httpPut.setEntity(stringEntity);
        String content = doRequest(httpPut, charset, null);

        return content;
    }

    /**
     * 发送  put 请求，参数以form表单键值对的形式提交。
     *
     * @param url
     * @return
     */
    public static String put(String url, Map<String, Object> params, Map<String, String> headers) throws IOException {
        return put(url, params, headers, defaultCharset);
    }

    /**
     * 发送  put 请求，参数以form表单键值对的形式提交。
     *
     * @param url
     * @return
     */
    public static String put(String url, Map<String, Object> params) throws IOException {
        return put(url, params, null, defaultCharset);
    }

    /**
     * 发送  put 请求，参数以form表单键值对的形式提交。
     *
     * @param url
     * @param charset
     * @return
     */
    public static String put(String url, Map<String, Object> params, Map<String, String> headers, String charset) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpPut httpPut = new HttpPut(url);
        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpPut::setHeader);
        }

        //组织请求参数
        List<NameValuePair> paramList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(params)) {
            params.forEach((key, value) -> paramList.add(new BasicNameValuePair(key, StringUtil.toString(value))));
        }

        httpPut.setEntity(new UrlEncodedFormEntity(paramList, charset));
        String content = doRequest(httpPut, charset, null);

        return content;
    }

    /**
     * 发送http delete请求
     */
    public static String delete(String url) throws IOException {
        return delete(url, null, defaultCharset);
    }

    /**
     * 发送http delete请求
     */
    public static String delete(String url, String charset) throws IOException {
        return delete(url, null, charset);
    }

    /**
     * 发送http delete请求
     */
    public static String delete(String url, Map<String, String> headers, String charset) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpDelete httpDelete = new HttpDelete(url);
        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpDelete::setHeader);
        }
        String content = doRequest(httpDelete, charset, null);
        return content;
    }

    public static String postFromUrlEncode(String url, Map<String, String> params, Map<String, String> forms) throws IOException, URISyntaxException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return postFromUrlEncode(url, params, forms, headers, defaultCharset);
    }

    public static String postFromUrlEncode(String url, Map<String, String> params, Map<String, String> forms, String charset) throws IOException, URISyntaxException {
        return postFromUrlEncode(url, params, forms, new HashMap<>(), charset);
    }

    public static String postFromUrlEncode(String url, Map<String, String> params, Map<String, String> forms, Map<String, String> headers, String charset) throws IOException, URISyntaxException {
        if (charset == null) {
            charset = defaultCharset;
        }

        URIBuilder uriBuilder = new URIBuilder(url);
        params.forEach(uriBuilder::setParameter);

        HttpPost httpPost = new HttpPost(uriBuilder.build());

        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpPost::setHeader);
        }

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        forms.forEach((k, v) -> {
            nameValuePairs.add(new BasicNameValuePair(k, v));
        });
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, charset));

        return doRequest(httpPost, charset, null);
    }

    /**
     * 发送  post 请求，支持文件上传
     */
    public static String postForm(String url, Map<String, String> params, List<File> files, Map<String, String> headers, String charset) throws IOException {
        if (charset == null) {
            charset = defaultCharset;
        }
        HttpPost httpPost = new HttpPost(url);

        //设置header
        if (!CollectionUtil.isEmpty(headers)) {
            headers.forEach(httpPost::setHeader);
        }

        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setCharset(Charset.forName(charset));

        // 普通参数
        ContentType contentType = ContentType.create("text/plain", Charset.forName(charset));
        if (!CollectionUtil.isEmpty(params)) {
            params.forEach((key, value) -> mEntityBuilder.addTextBody(key, value, contentType));
        }
        //二进制参数
        if (files != null && files.size() > 0) {
            for (File file : files) {
                mEntityBuilder.addBinaryBody("com.xin.utils.file", file);
            }
        }
        httpPost.setEntity(mEntityBuilder.build());
        String content = doRequest(httpPost, charset, null);
        return content;
    }

    private static String doRequest(HttpRequestBase httpRequestBase, String charset, HttpHost proxy) throws IOException {
        String content;
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setProxy(proxy)
                .build();
        httpRequestBase.setConfig(requestConfig);
        try {
            //响应信息
            httpResponse = closeableHttpClient.execute(httpRequestBase);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, charset);
        } finally {
            closeClient(closeableHttpClient);
            closeResponse(httpResponse);
        }
        return content;
    }

    private static void closeResponse(CloseableHttpResponse httpResponse) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (Exception ignored) {
        }
    }

    private static void closeClient(CloseableHttpClient closeableHttpClient) {
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (Exception e) {
            logger.error("HttpUtil关闭连接失败", e);
        }
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param url url地址
     * @return url请求参数部分
     */
    public static Map<String, String> getUrlParams(String url) {
        Map<String, String> mapRequest = new HashMap<>();

        if (StringUtil.isEmpty(url)) {
            return mapRequest;
        }
        String[] arrSplit = null;

        String strUrlParam = truncateUrlPage(url);
        if (StringUtil.isEmpty(strUrlParam)) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            if (arrSplitEqual.length > 1) {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (!"".equals(arrSplitEqual[0])) {
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (CollectionUtil.isEmpty(map)) {
            return "";
        }
        StringBuilder sb = new StringBuilder("&");
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String value = StringUtil.toString(entry.getValue());
            try {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(value, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("map转换url异常", e);
            }
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

    public static String getUrlParamsByMapWithOurEncode(Map<String, Object> map) {
        if (CollectionUtil.isEmpty(map)) {
            return "";
        }
        StringBuilder sb = new StringBuilder("?");
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String value = StringUtil.toString(entry.getValue());
            sb.append(entry.getKey()).append("=").append(value);
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 下载文件或图片，使用文件自身的文件名
     *
     * @param urlString 下载链接
     */
    public static void download(String urlString, File downloadFile) throws IOException {
        download(urlString, downloadFile.getParent(), downloadFile.getName());
    }

    /**
     * 下载文件或图片，使用文件自身的文件名
     *
     * @param urlString    下载链接
     * @param downloadPath 下载路径
     */
    public static void download(String urlString, String downloadPath) throws IOException {
        download(urlString, downloadPath, "");
    }

    /**
     * 下载文件或图片
     *
     * @param urlString    下载链接
     * @param downloadPath 下载路径
     * @param saveName     保存的文件名
     */
    public static void download(String urlString, String downloadPath, String saveName) throws IOException {
        URL url;
        String fileName;
        String savePath;

        // 若下载文件夹不存在创建文件夹
        if (!FileUtil.exists(downloadPath)) {
            FileUtil.mkdirs(downloadPath);
        }

        // 文件名包含路径，抛出异常
        if (saveName.contains("\\") || saveName.contains("/")) {
            throw new IllegalArgumentException("名字不应包含路径");
        }

        // 为下载文件夹添加后缀
        if (!downloadPath.endsWith("\\") && !downloadPath.endsWith("/")) {
            downloadPath = downloadPath + separator;
        }

        url = new URL(urlString);
        DataInputStream dataInputStream = new DataInputStream(url.openStream());

        URLConnection uc = url.openConnection();

        // 下载名不存在，使用默认名字
        if ("".equals(saveName)) {
            try {
                fileName = uc.getHeaderField("Content-Disposition");
                fileName = new String(fileName.getBytes(StandardCharsets.ISO_8859_1), "GBK");
                fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename=") + 9), "UTF-8");
            } catch (NullPointerException e) {
                fileName = urlString.replaceAll("^.*[/\\\\]", "");
            }
            saveName = fileName;
        }

        savePath = downloadPath + saveName;

        FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;

        while ((length = dataInputStream.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        byte[] context = output.toByteArray();
        fileOutputStream.write(output.toByteArray());
        dataInputStream.close();
        fileOutputStream.close();
    }
}
