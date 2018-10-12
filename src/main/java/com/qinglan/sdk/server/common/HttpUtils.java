package com.qinglan.sdk.server.common;

import org.apache.commons.codec.CharEncoding;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

public class HttpUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(HttpUtils.class);
    private static final int DEFAULT_TIME_OUT = 10000;

    public static String get(String url) {
        return get(url, DEFAULT_TIME_OUT);
    }

    public static String get(String url, int timeout) {
        String result = null;
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            if (response.getEntity() != null) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return result;
    }

    public static String get(String url, Map<String, String> headerParams) {

        String result = null;
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(DEFAULT_TIME_OUT).setConnectTimeout(DEFAULT_TIME_OUT)
                    .build();
            httpGet.setConfig(requestConfig);
            for (Entry<String, String> e : headerParams.entrySet()) {
                httpGet.setHeader(e.getKey(), e.getValue());
            }
            response = httpClient.execute(httpGet);
            if (response.getEntity() != null) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return result;

    }

    public static String getZmData(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            url = url + "?";
            for (Entry<String, Object> e : params.entrySet()) {
                sb.append(e.getKey() + "=" + e.getValue() + "&");
            }
            url = url + sb.substring(0, sb.length() - 1);
        }

        HttpURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
        } catch (Exception e) {
            logger.error("open connection exception", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        StringBuilder buffer = new StringBuilder();
        try {
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    buffer.append("|");
                    buffer.append(temp);
                }
            }
        } catch (Exception e) {
            logger.error("io exception", e);
        }
        return buffer.toString();
    }

    public static String post(String url, Map<String, Object> params) {
        return post(url, params, DEFAULT_TIME_OUT);
    }

    public static String post(String url, Map<String, Object> params,
                              int timeout) {
        String result = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .build();
            httpPost.setConfig(requestConfig);
            if (params != null && params.size() > 0) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                for (Entry<String, Object> entry : params.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(),
                            entry.getValue() == null ? "" : entry.getValue()
                                    .toString().trim()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(
                        nameValuePairs, Consts.UTF_8);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
            }
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getEntity() != null) {
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return result;
    }


    /**
     * POST方式发送
     *
     * @param toUrl 发送地址
     * @param data  发送数据
     * @return
     * @throws Exception
     */
    public static String doPost(String toUrl, Map<String, Object> params)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        StringBuilder buffer = null;
        if (params != null) {
            for (Entry<String, Object> e : params.entrySet()) {
                sb.append(e.getKey() + "=" + e.getValue().toString().trim()
                        + "&");
            }
            sb.substring(0, sb.length() - 1);
        }
        HttpURLConnection conn = null;
        try {

            URL url = new URL(toUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);
            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            if (sb != null && !sb.toString().trim().equals(""))
                out.writeBytes(sb.toString());
            out.flush();
            out.close();
            buffer = new StringBuilder();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    buffer.append(buf);
                }
                reader.close();
            } else {
                buffer.append("HttpResponsecode:" + conn.getResponseCode());
                buffer.append("HttpMessage:" + conn.getResponseMessage());
            }
        } catch (Exception e) {
            logger.error("io exception", e);
            throw new Exception("io exception");
        } finally {
            conn.disconnect();
        }
        return buffer.toString();
    }


    /**
     * POST方式发送
     *
     * @param toUrl 发送地址
     * @param data  发送数据
     * @return
     * @throws Exception
     */
    public static String doPost(String toUrl, String params, int timeout)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;
        try {

            URL url = new URL(toUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(timeout);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);
            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            if (params != null && !params.trim().equals(""))
                out.writeBytes(params);
            out.flush();
            out.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    sb.append(buf);
                }
                reader.close();
            }
        } catch (Exception e) {
            logger.error("io exception", e);
            throw new Exception("io exception");
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    /**
     * POST方式发送
     *
     * @param toUrl 发送地址
     * @param data  发送数据
     * @return
     * @throws Exception
     */
    public static String doPostToJson(String toUrl, String data, int timeout)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;
        try {

            URL url = new URL(toUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(timeout);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);
            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            if (data != null && !data.trim().equals(""))
                out.writeBytes(data);
            out.flush();
            out.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    sb.append(buf);
                }
                reader.close();
            }else{
                logger.info("错误码："+conn.getResponseCode()+"错误消息："+conn.getResponseMessage());
            }
        } catch (Exception e) {
            logger.error("io exception", e);
            throw new Exception("io exception");
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    /**
     *
     * @param url 发送地址
     * @param urldata 发送报文数据
     * @param headers 发送头部数据
     * @return
     * @throws Exception
     */
    public static String doPost(String url, String urldata, Map<String, Object> headers)
            throws Exception {


        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(urldata, ContentType.APPLICATION_JSON);
        stringEntity.setContentEncoding("UTF-8");

        httpPost.addHeader("Content-Type","application/json");
        Set<String> keySet = headers.keySet();
        for (String itemKey : keySet) {
            httpPost.addHeader(itemKey, String.valueOf(headers.get(itemKey)));
        }

        httpPost.setEntity(stringEntity);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpPost);

        HttpEntity entity = response.getEntity();

        String httpStr = EntityUtils.toString(entity);


        return httpStr;
    }

    @SuppressWarnings("deprecation")
    public static String postToHttps(String url, Map<String, Object> params)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            url = url + "?";
            for (Entry<String, Object> e : params.entrySet()) {
                sb.append(e.getKey() + "=" + e.getValue().toString().trim()
                        + "&");
            }
            url = url + sb.substring(0, sb.length() - 1);
        }

        InputStream in = null;
        OutputStream out = null;
        String str_return = "";
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                    new java.security.SecureRandom());
            URL console = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) console
                    .openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            DataInputStream indata = new DataInputStream(is);
            String ret = "";

            while (ret != null) {
                ret = indata.readLine();
                if (ret != null && !ret.trim().equals("")) {
                    str_return = str_return
                            + new String(ret.getBytes("ISO-8859-1"), "UTF-8");
                }
            }
            conn.disconnect();
        } catch (ConnectException e) {
            logger.error("io exception", e);
            throw e;
        } catch (IOException e) {
            logger.error("io exception", e);
            throw e;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return str_return;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static String post(String reqUrl, String data) throws Exception {
        String invokeUrl = reqUrl;
        URL serverUrl = new URL(invokeUrl);
        HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.connect();

        conn.getOutputStream().write(data.getBytes(CharEncoding.UTF_8));
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), CharEncoding.UTF_8));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        in.close();
        String response = buffer.toString();
        conn.disconnect();
        return response;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        Enumeration<String> attributeNames = request.getParameterNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRequestParamsObject(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        Enumeration<String> attributeNames = request.getParameterNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params;
    }

    public static String inputStream2String(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}