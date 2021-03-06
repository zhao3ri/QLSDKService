package com.qinglan.sdk.server.common;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	public static final String UTF8 = "UTF-8";
	private static final byte[] BYTEARRAY = new byte[0];

	public static boolean isNullOrEmpty(String s) {
		if (s == null || s.isEmpty() || s.trim().isEmpty())
			return true;
		return false;
	}

	public static byte[] getBytes(String value) {
		return getBytes(value, UTF8);
	}

	public static byte[] getBytes(String value, String charset) {
		if (isNullOrEmpty(value))
			return BYTEARRAY;
		if (isNullOrEmpty(charset))
			charset = UTF8;
		try {
			return value.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			return BYTEARRAY;
		}
	}
	
	public static String replaceBlank(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }
}
