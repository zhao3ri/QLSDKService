package com.qinglan.sdk.server.utils.channel.lewan.util;

import java.util.ResourceBundle;

public class PropertiesAliPayConfiger {
	private static ResourceBundle resb1 = ResourceBundle.getBundle("alipayapi");
	public static String getProperty(String key){
		return resb1.getString(key);
	}
	
}
