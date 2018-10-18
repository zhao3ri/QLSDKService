package com.qinglan.sdk.server.platform.qq;

import java.util.*;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import com.qinglan.sdk.server.common.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiV3 {
	private static final Logger logger = LoggerFactory.getLogger(OpenApiV3.class);
	/**
	 * 构造函数
	 * 
	 * @param appid
	 *            应用的ID
	 * @param appkey
	 *            应用的密钥
	 */
	public OpenApiV3(String appid, String appkey) {
		this.appid = appid;
		this.appkey = appkey;
	}
	
	public OpenApiV3(String appid, String appkey, String serverName) {
		this.appid = appid;
		this.appkey = appkey;
		this.serverName = serverName;
	}

	/**
	 * 设置OpenApi服务器的地址
	 * 
	 * @param serverName
	 *            OpenApi服务器的地址
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * 执行API调用
	 * 
	 * @param scriptName
	 *            OpenApi CGI名字 ,如/v3/user/get_info
	 * @param params
	 *            OpenApi的参数列表
	 * @param protocol
	 *            HTTP请求协议 "http" / "https"
	 * @return 返回服务器响应内容
	 */
	public String api_pay(String scriptName, HashMap<String, String> cookies,
			HashMap<String, String> params, String protocol)
			throws OpensnsException {
		// 检查openid openkey等参数
		if (params.get("openid") == null) {
			throw new OpensnsException(ErrorCode.PARAMETER_EMPTY,
					"openid is empty");
		}

		// 无需传sig,会自动生成
		params.remove("sig");

		// 添加固定参数
		params.put("appid", this.appid);

		// 请求方法
		String method = "get";

		// 签名密钥
		String secret = this.appkey + "&";

		// 计算签名
		String signScriptName = "/v3/r"+scriptName;
//		String sig = SnsSigCheck.makeSig(method, scriptName, params, secret);
		String sig = SnsSigCheck.makeSig(method, signScriptName, params, secret);

		logger.debug("---------------sigin----------------------->"+sig);

		params.put("sig", sig);

		StringBuilder sb = new StringBuilder(64);
		sb.append(protocol).append("://").append(this.serverName)
				.append(scriptName);
		String url = sb.toString();

		String qs = null;
		try {
			qs = mkQueryString(params);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		url += "?";
		url += qs;

		// cookie
		// cookies = null;

		long startTime = System.currentTimeMillis();

		// 通过调用以下方法，可以打印出最终发送到openapi服务器的请求参数以及url，默认注释
		printRequest(url, method, params);
		printRequest(url, method, cookies);

		// 发送请求
		String resp = SnsNetwork.getRequest(url, cookies, protocol);

		// 解码JSON
		JSONObject jo = null;
		try {
			jo = new JSONObject(resp);
		} catch (JSONException e) {
			throw new OpensnsException(ErrorCode.RESPONSE_DATA_INVALID, e);
		}

		// 检测ret值
		int rc = jo.optInt("ret", 0);

		// 通过调用以下方法，可以打印出调用openapi请求的返回码以及错误信息，默认注释
		printRespond(resp);

		return resp;
	}

	public String api_msdk(String scriptName, HashMap<String, String> qs,
			HashMap<String, String> params, String protocol)
			throws OpensnsException {
		// 检查openid openkey等参数
		if (params.get("openid") == null) {
			throw new OpensnsException(ErrorCode.PARAMETER_EMPTY,
					"openid is empty");
		}

		// 添加固定参数
		// params.put("appid", this.appid);
		// qs.put("appid", this.appid);

		// 请求方法
		String method = "post";

		String params_json = JsonMapper.toJson(params);

		StringBuilder sb = new StringBuilder(64);
		sb.append(protocol).append("://").append(this.serverName)
				.append(scriptName);
		String url = sb.toString();

		String _qs = null;
		try {
			_qs = OpenApiV3.mkQueryString(qs);
			url += "?";
			url += _qs;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// cookie
		HashMap<String, String> cookies = null;

		long startTime = System.currentTimeMillis();

		// 通过调用以下方法，可以打印出最终发送到openapi服务器的请求参数以及url，默认注释
		printRequest(url, method, params);

		// 发送请求
		String resp = SnsNetwork.postRequest(url, params_json, cookies,
				protocol);

		// 解码JSON
		JSONObject jo = null;
		try {
			jo = new JSONObject(resp);
		} catch (JSONException e) {
			throw new OpensnsException(ErrorCode.RESPONSE_DATA_INVALID, e);
		}

		// 检测ret值
		int rc = jo.optInt("ret", 0);

		// 通过调用以下方法，可以打印出调用openapi请求的返回码以及错误信息，默认注释
		printRespond(resp);

		return resp;
	}

	/**
	 * 辅助函数，打印出完整的请求串内容
	 * 
	 * @param url
	 *            请求cgi的url
	 * @param method
	 *            请求的方式 get/post
	 * @param params
	 *            OpenApi的参数列表
	 */
	private void printRequest(String url, String method,
			HashMap<String, String> params) throws OpensnsException {
		logger.debug ("==========Request Info==========\n");
		logger.debug("method:  " + method);
		logger.debug("url:  " + url);
		logger.debug("params:");
		logger.debug(params.toString());
		logger.debug("querystring:");
		StringBuilder buffer = new StringBuilder(128);
		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			try {
				buffer.append(
						URLEncoder.encode((String) entry.getKey(), "UTF-8")
								.replace("+", "%20").replace("*", "%2A"))
						.append("=")
						.append(URLEncoder
								.encode((String) entry.getValue(), "UTF-8")
								.replace("+", "%20").replace("*", "%2A"))
						.append("&");
			} catch (UnsupportedEncodingException e) {
				throw new OpensnsException(ErrorCode.MAKE_SIGNATURE_ERROR, e);
			}
		}
		String tmp = buffer.toString();
		tmp = tmp.substring(0, tmp.length() - 1);
		logger.debug(tmp);
	}

	public static String mkQueryString(HashMap<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder buffer = new StringBuilder(128);
		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			buffer.append(
					URLEncoder.encode((String) entry.getKey(), "UTF-8")
							.replace("+", "%20").replace("*", "%2A"))
					.append("=")
					.append(URLEncoder
							.encode((String) entry.getValue(), "UTF-8")
							.replace("+", "%20").replace("*", "%2A"))
					.append("&");
		}
		String tmp = buffer.toString();
		tmp = tmp.substring(0, tmp.length() - 1);
		return tmp;
	}

	/**
	 * 辅助函数，打印出完整的执行的返回信息
	 * 
	 * @return 返回服务器响应内容
	 */
	private void printRespond(String resp) {
		logger.debug("===========Respond Info============");
		logger.debug(resp);
	}

	/**
	 * 验证openid是否合法
	 */
	private boolean isOpenid(String openid) {
		return (openid.length() == 32) && openid.matches("^[0-9A-Fa-f]+$");
	}

	private String appid;;
	private String appkey;
	private String serverName;
}
