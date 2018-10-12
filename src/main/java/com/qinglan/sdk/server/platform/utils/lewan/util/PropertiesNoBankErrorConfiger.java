package com.qinglan.sdk.server.platform.utils.lewan.util;

import java.util.ResourceBundle;

public class PropertiesNoBankErrorConfiger {
	private static ResourceBundle resb1 = ResourceBundle.getBundle("nonBankSubmitError");
	public static String getProperty(String key){
		return resb1.getString(key);
	}
	
}
