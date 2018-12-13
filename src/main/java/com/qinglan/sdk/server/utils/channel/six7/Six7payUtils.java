package com.qinglan.sdk.server.utils.channel.six7;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;


public class Six7payUtils {

	public static Map<String, String> filterParams(Map<String, String> params) {
		Map<String, String> result = new HashMap<String, String>();
		if (params == null || params.size() <= 0) {
			return result;
		}
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	public static String createParamsString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * MD5签名
	 * 
	 * @param text
	 * @param key
	 * @param input_charset
	 * @return
	 * @throws IOException
	 */
	public static String signMd5(String text, String key, String input_charset) throws IOException {
		text = text + key;
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * MD5校验
	 * 
	 * @param text
	 * @param sign
	 * @param key
	 * @param input_charset
	 * @return
	 */
	public static boolean verifyMd5(String text, String sign, String key, String input_charset) {
		text = text + key;
		String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证消息是否是Six7pay发出的合法消息
	 * 
	 * @param params
	 *            通知返回来的参数数组
	 * @return 验证结果
	 */
	public static boolean verify(Map<String, String> params, String input_charset,String key) {
		String sign = "";
		if (params.get("sign") != null) {
			sign = params.get("sign");
		}
		Map<String, String> paramsNew = filterParams(params);
		String paramsString = createParamsString(paramsNew);
		return verifyMd5(paramsString, sign, key, input_charset);
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

}
