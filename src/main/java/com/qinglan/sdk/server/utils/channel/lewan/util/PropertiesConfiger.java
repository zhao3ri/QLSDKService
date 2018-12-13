package com.qinglan.sdk.server.utils.channel.lewan.util;

import java.util.ResourceBundle;

public class PropertiesConfiger {
	private static ResourceBundle resb1 = ResourceBundle.getBundle("payapi");
	public static String getProperty(String key){
		return resb1.getString(key);
	}
	
}
