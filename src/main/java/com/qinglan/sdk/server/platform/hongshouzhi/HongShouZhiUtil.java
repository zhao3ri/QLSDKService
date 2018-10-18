package com.qinglan.sdk.server.platform.hongshouzhi;


import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by engine on 16/9/23.
 */
public class HongShouZhiUtil {

    public static String doRequest(String url, String str) {
        HttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);

        HttpPost post = new HttpPost(url);
        post.setHeader("content-type", "application/json");
        if (str != null) {
            HttpEntity entity = new ByteArrayEntity(str.getBytes());
            post.setEntity(entity);
        }
        int count = 0;
        // 等待3秒在请求2次
        while (count < 2) {
            try {
                HttpResponse response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                if (code == HttpStatus.SC_OK) {
                    return readFromNetStream(response.getEntity().getContent());
                }
            } catch (ClientProtocolException e) {
                System.out.println("网络异常");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("网络异常");
                e.printStackTrace();
            }
            count++;
            try {
                // 请求失败，在请求一次
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("网络异常");
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String readFromNetStream(InputStream request) {
        try {
            byte[] buffer = new byte[1024];
            int num = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (-1 != (num = request.read(buffer))) {
                bos.write(buffer, 0, num);
            }
            return bos.toString("utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
