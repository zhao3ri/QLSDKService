package com.qinglan.sdk.server.common;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();

		return ip;
	}
	
	public static String getIpAddr(HttpServletRequest request) { 
       String ip = request.getHeader("x-forwarded-for"); 
       if(ip != null && ip.indexOf(",") != -1) {  
    	   ip = ip.substring(0,ip.indexOf(",")).trim();  
       } 
       return ip; 
    }

}
