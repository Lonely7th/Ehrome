package com.daiji.feixiang.common;

import android.text.TextUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class Request {
    //请求方式
    private String method = "GET";
    //请求url
    private String url = "";
    //请求超时
    private int timeout = 3000;
    //请求成功状态码
    private int code = 200;

    //请求数据
    private Map<String, String> map;

    //请求的useragent
    private String userAgent = "";

    /**
     * 设置请求url
     *
     * @param url
     * @return
     */
    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置请求方法
     *
     * @param method GET,POST...
     * @return
     */
    public Request setMethod(String method) {
        this.method = method;
        return this;
    }


    /**
     * 设置超时时间
     *
     * @param timeout
     * @return
     */
    public Request setTimeOut(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 设置成功响应状态码
     *
     * @param code
     * @return
     */
    public Request setCode(int code) {
        this.code = code;
        return this;
    }

    public Request setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Request setMap(HashMap<String, String> map) {
        try {
            this.map = map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 获得GET请求数据
     *
     * @return
     */
    public String getString() throws Exception {
        String data = "";
        String charset = "UTF-8";
        InputStream in = null;
        HttpURLConnection conn = null;
        URL u = new URL(url);
        if (!url.startsWith("https")) {
            conn = (HttpURLConnection) u.openConnection();
        } else {
            conn = (HttpsURLConnection) u.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-Agent", userAgent);
        }
        String params = "";
        if (map != null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
                if (TextUtils.isEmpty(params)){
                    params = entry.getKey()+"="+ URLEncoder.encode(entry.getValue(),charset);
                } else {
                    params += "&"+entry.getKey()+"="+ URLEncoder.encode(entry.getValue(),charset);
                }
            }
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        if (method.equals("POST")){
            if (!TextUtils.isEmpty(params)){
                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(params);
                outputStream.flush();
                outputStream.close();
            }
        } else {
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        }
        conn.connect();
        if (code == conn.getResponseCode()) {
            in = conn.getInputStream();
            String contentType = conn.getHeaderField("Content-Type");
//            System.out.println(contentType);
            String pattern = "charset=([^\\s]*)";
            Pattern compile = Pattern.compile(pattern);
            Matcher matcher = compile.matcher(contentType);
            if (matcher.find()) {
                if (matcher.group(1) != null && !TextUtils.isEmpty(matcher.group(1).trim())) {
                    charset = matcher.group(1);
                }
            }
            byte[] by = new byte[1024];
            int len = -1;
            while ((len = in.read(by)) != -1) {
                data += new String(by, 0, len, charset);
            }
        }
        return data;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public InputStream getInputStream() throws Exception {
        HttpURLConnection conn = null;
        URL u = new URL(url);
        if (!url.startsWith("https")) {
            conn = (HttpURLConnection) u.openConnection();
        } else {
            conn = (HttpsURLConnection) u.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-Agent", userAgent);
        }
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.connect();
        return conn.getInputStream();
    }
}
