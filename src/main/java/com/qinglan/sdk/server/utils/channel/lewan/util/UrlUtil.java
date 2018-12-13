package com.qinglan.sdk.server.utils.channel.lewan.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlUtil {
	/** 
     * 功能：检测当前URL是否可连接或是否有效, 
     * 描述：最多连接网络 3 次, 如果 3 次都不成功，视为该地址不可用 
     * @param  urlStr 指定URL网络地址 
     * @return URL 
     */  
    public  int isConnect(String urlStr) {  
        int counts = 0;  
        int retu = 404;  
        if (urlStr == null || urlStr.length() <= 0) {                         
            return 403;                   
        }  
        while (counts < 3) {  
            long start = 0;  
            try {  
            	URL url = new URL(urlStr);  
                start = System.currentTimeMillis();  
                HttpURLConnection con = (HttpURLConnection) url.openConnection();  
               int  state = con.getResponseCode();  
               // log.info("请求断开的URL一次需要："+(System.currentTimeMillis()-start)+"毫秒");  
                if (state != 404) {  
                    retu = state;  
                   // log.info(urlStr+"--可用");  
                }
                break;  
            }catch (Exception ex) {  
                counts++;   
               // log.info("请求断开的URL一次需要："+(System.currentTimeMillis()-start)+"毫秒");  
                //log.info("连接第 "+counts+" 次，"+urlStr+"--不可用");  
                continue;  
            }  
        }  
        return retu;  
    } 
    
    public static void main(String[] args) {
    	UrlUtil uu=new UrlUtil();
		System.out.println(uu.isConnect("http://pay.3top7.com/pay/payment/lewan/index.php"));
	}
}
