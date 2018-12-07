package com.qinglan.sdk.server.application.platform;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.qinglan.sdk.server.domain.basic.Order;

public interface ChannelUtilsService {

	//xiaomi util
	public String getRequestUrlXiaomi(Map<String,String> params, String baseUrl,String secretKey) throws Exception;
	public Map<String,String> getSignParamsXiaomi(String queryString) throws UnsupportedEncodingException;
	public String getSignXiaomi(Map<String,String> params,String secretKey) throws Exception;
	
	//360 util
	public boolean isValidRequestQihoo(HashMap<String,String> params,String appkey,String appsecret);
	
	//wandoujia util
	public boolean vaildWdjSign(String content, String sign,String publickey) throws Exception;
	
	//gionee util
	public String getGioneeMac(String host, String port, String macKey, String timestamp, String nonce, String method, String uri);
	public boolean validGioneeSign(Map<String, String> params, String publicKey, String sign) throws Exception;
	public String getOrderCreateSign(Map<String, Object> params, String privateKey) throws Exception;
	
	//appchina util
	public boolean validAppchinaSign(String transdata, String sign, String key) throws Exception;
	
	//oppo util
	public boolean validOppoSign(HttpServletRequest request, String key) throws Exception;
	
	//kupai util
	public boolean verifyKupai(String content, String sign, String pub_key) throws Exception;
	
	//htc util
	public boolean verifyHTC(String content, String sign, String pub_key) throws Exception;
	public String signHTCPayContent(String content, String privateKey);
	
	//mmy util
	public boolean verifyMmy(String sign , String appKey , String orderId) throws Exception ;
	
	//play util
	public String playSmsXml(Order order, String cporderid, String correlator, boolean flag);
	public String playXml(String cporderid,boolean flag);
	
	//huawei util
	public String huaweiPaySign(String content, String private_key) throws Exception;
	public boolean verifyHuawei(Map<String, String> params, String pub_key) throws Exception;
	
	//muzhi util
	public String muzhiMd5(String content) throws Exception;
	
	//atet util
	public boolean verifyAtet(String transdata, String sign, String publicKey);
	
	//qbao util
	public String qbaoPaySign(String content, String private_key) throws Exception;
	public boolean verifyQbao(String content, String sign, String pub_key) throws Exception;
}
