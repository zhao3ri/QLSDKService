package com.qinglan.sdk.server.application.platform.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.qinglan.sdk.server.application.platform.ChannelUtilsService;
import com.qinglan.sdk.server.platform.utils.Base64;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.HmacSHA1Encryption;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.common.RSAUtil;
import com.qinglan.sdk.server.common.StringUtil;
import com.qinglan.sdk.server.domain.basic.Order;

@Service
public class ChannelUtilServiceImpl implements ChannelUtilsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelUtilServiceImpl.class);
	
	/**
	 * 小米 获取Url
	 * @param params
	 * @param baseUrl
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public String getRequestUrlXiaomi(Map<String,String> params, String baseUrl,String secretKey) throws Exception{
		String signString = getSortQueryString(params);
		String signature = HmacSHA1Encryption.hmacSHA1Encrypt(signString, secretKey);
		return baseUrl + "?" + signString + "&signature=" + signature;
	}
	
	/**
	 * xiaomi 获取得到排序好的查询字符串
	 * @param params 请求参数
	 * @param isContainSignature 是否包含signature参数
	 * @return
	 */
	private String getSortQueryString(Map<String,String> params) throws Exception {
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		StringBuffer sb = new StringBuffer();
		for(Object key : keys){
			sb.append(String.valueOf(key)).append("=").append(params.get(String.valueOf(key))).append("&");
		}
		
		String text = sb.toString();
		if(text.endsWith("&")) {
			text=text.substring(0,text.length()-1);
		}
		return text;
	}
	/**
	 * 小米 获取参数
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Map<String,String> getSignParamsXiaomi(String queryString) throws UnsupportedEncodingException{
		Map<String,String> result = new HashMap<String, String>();
		String[] params = queryString.split("&");
		for(String param : params){
			String[] tmp = param.split("=");
			String key = tmp[0];
			if(!"signature".equals(key)) {
				result.put(key, URLDecoder.decode(tmp[1],"UTF-8"));
			}
		}
		return result;
 	}
	/**
	 * 小米 密钥匹配
	 * @param params
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public String getSignXiaomi(Map<String,String> params,String secretKey) throws Exception{
		String signString = getSortQueryString(params);
		return HmacSHA1Encryption.hmacSHA1Encrypt(signString,secretKey);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isValidRequestQihoo(HashMap<String,String> params,String appkey,String appsecret) {
		String arrFields[] = {"app_key", "product_id", "app_uid",
				"order_id", "sign_type", "gateway_flag",
				"sign", "sign_return","amount"
			};
			ArrayList fields = new ArrayList(Arrays.asList(arrFields));
			String key;
			String value;
			Iterator iterator = fields.iterator();
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				value = (String) params.get(key);
				if (value == null || value.equals("")) {
					return false;
				}
			}
			if(!params.get("app_key").equals(appkey)){
				return false;
			}

			String sign = getSignQihoo(params,appsecret);
			String paramSign = (String) params.get("sign");
			return sign.equals(paramSign);
	}
	
	/**
	 * 签名计算
	 * @param params
	 * @param appSecret
	 * @return 
	 */
	private static String getSignQihoo(HashMap<String,String> params, String appSecret) {
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		String k, v;

		String str = "";
		for (int i = 0; i < keys.length; i++) {
			k = (String) keys[i];
			if (k.equals("sign") || k.equals("sign_return")) {
				continue;
			}
            if(params.get(k)==null){
                continue;
            }
			v = (String) params.get(k);

			if (v.equals("0") || v.equals("")) {
				continue;
			}
			str += v + "#";
		}
		return md5Qihoo(str + appSecret);
	}
	
	private static String md5Qihoo(String str) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(str.getBytes("UTF8"));
			byte bytes[] = m.digest();

			for (int i = 0; i < bytes.length; i++) {
				if ((bytes[i] & 0xff) < 0x10) {
					sb.append("0");
				}
				sb.append(Long.toString(bytes[i] & 0xff, 16));
			}
		} catch (Exception e) {
			LOGGER.error("md5Qihoo() error", e);
		}
		return sb.toString();
	}
	
	@Override
	public boolean vaildWdjSign(String content, String sign,String publickey) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(publickey);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

        java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

        signature.initVerify(pubKey);
        signature.update(content.getBytes("utf-8"));

        return signature.verify(com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign));
	}

	@Override
	public String getGioneeMac(String host, String port, String macKey, String timestamp, String nonce, String method, String uri) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(timestamp).append("\n");
		buffer.append(nonce).append("\n");
		buffer.append(method.toUpperCase()).append("\n");
		buffer.append(uri).append("\n");
		buffer.append(host.toLowerCase()).append("\n");
		buffer.append(port).append("\n");
		buffer.append("\n");
		String text = buffer.toString();

		byte[] ciphertext = null;
		try {
			Mac mac = Mac.getInstance("MAC_NAME");
			mac.init(new SecretKeySpec(StringUtil.getBytes(macKey), "MAC_NAME"));
			ciphertext = mac.doFinal(StringUtil.getBytes(text));
		} catch (Throwable e) {
			LOGGER.error("getGioneeMac() error", e);
			e.printStackTrace();
			return null;
		}
		return com.qinglan.sdk.server.common.Base64.encode(ciphertext).replace("\n", "");
	}

	@Override
	public boolean validGioneeSign(Map<String, String> params, String publicKey, String sign) throws Exception{
		StringBuilder contentBuffer = new StringBuilder();
		Object[] signParamArray = params.keySet().toArray();
		Arrays.sort(signParamArray);
		for (Object key : signParamArray) {
			String value = params.get(key);
			if (!"sign".equals(key) && !"msg".equals(key)) {// sign和msg不参与签名
				contentBuffer.append(key + "=" + value + "&");
			}
		}
		String content = StringUtils.removeEnd(contentBuffer.toString(), "&");
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(publicKey);
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

		signature.initVerify(pubKey);
		signature.update(content.getBytes("utf-8"));

		return signature.verify(com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign));
	}
	
	@Override
	public String getOrderCreateSign(Map<String, Object> params, String privateKey) throws Exception {
		StringBuilder sb = new StringBuilder();
		Object[] signParamArray = params.keySet().toArray();
		Arrays.sort(signParamArray);
		for (Object key : signParamArray) {
			String value = params.get(key).toString();
			if (!"player_id".equals(key)) {// player_id不参与签名
				sb.append(value);
			}
		}
		String charset = CharEncoding.UTF_8;
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(com.qinglan.sdk.server.common.Base64.decodeToByteArray(privateKey));
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);
		java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
		signature.initSign(priKey);
		signature.update(sb.toString().getBytes(charset));
		byte[] signed = signature.sign();
		return com.qinglan.sdk.server.common.Base64.encode(signed);
	}

	@Override
	public boolean validAppchinaSign(String transdata, String sign, String key) throws Exception{
		String md5Str = MD5.encode(transdata);
		String decodeBaseStr = Base64.decode(key);
		String[] decodeBaseVec = decodeBaseStr.replace('+', '#').split("#");
		String privateKey = decodeBaseVec[0];
		String modkey = decodeBaseVec[1];
		String reqMd5 = RSAUtil.decrypt(sign, new BigInteger(privateKey), new BigInteger(modkey));
		return StringUtils.equals(md5Str, reqMd5);
	}

	@Override
	public boolean validOppoSign(HttpServletRequest request, String publicKey) throws Exception {
		String notifyId = request.getParameter("notifyId");
		String partnerOrder = request.getParameter("partnerOrder");
		String productName = request.getParameter("productName");
		String productDesc = request.getParameter("productDesc");
		String price = request.getParameter("price");
		String count = request.getParameter("count");
		String attach = request.getParameter("attach");
		String sign = request.getParameter("sign");
		
		StringBuilder sb = new StringBuilder();
		sb.append("notifyId=").append(notifyId);
		sb.append("&partnerOrder=").append(partnerOrder);
		sb.append("&productName=").append(productName);
		sb.append("&productDesc=").append(productDesc);
		sb.append("&price=").append(price);
		sb.append("&count=").append(count);
		sb.append("&attach=").append(attach);
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey.getBytes());
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

		signature.initVerify(pubKey);
		signature.update(sb.toString().getBytes("UTF-8"));
		boolean bverify = signature.verify(org.apache.commons.codec.binary.Base64.decodeBase64(sign.getBytes()));
		return bverify;
	}

	@Override
	public boolean verifyKupai(String content, String sign, String pub_key) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(pub_key);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
		java.security.Signature signature = java.security.Signature.getInstance("MD5WithRSA");
	
		signature.initVerify(pubKey);
		signature.update(content.getBytes("utf-8"));
	
		return signature.verify(com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign));
	}
	
	@Override
	public boolean verifyHTC(String content, String sign, String publicKey)throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(publicKey);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
		java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
		
		signature.initVerify(pubKey);
		signature.update( content.getBytes("utf-8"));
		return signature.verify( com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign));
	}

	@Override
	public String signHTCPayContent(String content, String privateKey) {
		String charset = "utf-8";
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(com.qinglan.sdk.server.common.Base64.decodeToByteArray(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initSign(priKey);
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();

			return com.qinglan.sdk.server.common.Base64.encode(signed);
		} catch (Exception e) {
			LOGGER.error("signHTCPayContent error :", e);
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public boolean verifyMmy(String sign, String appKey, String orderId) throws Exception {
		if(sign.length()<14){
			return false;
		}
		String verityStr = sign.substring(0,8);   
		sign = sign.substring(8);		
		String temp = MD5.encode(sign);	
				
						
		if(!verityStr.equals(temp.substring(0,8))){
			return false;
		}
		String keyB =  sign.substring(0,6);
			
		String randKey = keyB+appKey;
			
		randKey = MD5.encode(randKey);
			
		byte[] signB = com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign.substring(6));
		int signLength = signB.length;
		String verfic="";
		for(int i =0 ; i< signLength ; i++){
			char b = (char)(signB[i]^randKey.getBytes()[i%32]);
			verfic +=String.valueOf(b);
		}
		return verfic.equals(orderId);	
	}

	@Override
	public String playSmsXml(Order order, String cporderid,String correlator,boolean flag) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("<sms_pay_check_resp>");
		buffer.append("<cp_order_id>").append(cporderid).append("</cp_order_id>");
		buffer.append("<correlator>").append(correlator).append("</correlator>");
		buffer.append("<game_account>").append(order == null ? "":order.getUid()).append("</game_account>");
		buffer.append("<fee>").append(order == null ? "":order.getAmount()/100).append("</fee>");
		buffer.append("<if_pay>").append(flag ? "0":"-1").append("</if_pay>");
		buffer.append("<order_time>").append(DateUtils.toStringDate(new Date())).append("</order_time>");
		buffer.append("</sms_pay_check_resp>");
		return buffer.toString();
	}

	@Override
	public String playXml(String cporderid, boolean flag) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("<cp_notify_resp>");
		buffer.append("<h_ret>").append(flag ? 0:-1).append("</h_ret>");
		buffer.append("<cp_order_id>").append(cporderid).append("</cp_order_id>");
		buffer.append("</cp_notify_resp>");
		return buffer.toString();
	}

	@Override
	public boolean verifyHuawei(Map<String, String> params, String pub_key)throws Exception {
		StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++){
            String key = (String) keys.get(i);
            if ("sign".equals(key)){
                continue;
            }
            String value = (String) params.get(key);
            if (value != null){
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }else{
                content.append((i == 0 ? "" : "&") + key + "=");
            }
        }
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(pub_key);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

        java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

        signature.initVerify(pubKey);
        signature.update(content.toString().getBytes("utf-8"));

        boolean bverify = signature.verify(com.qinglan.sdk.server.common.Base64.decodeToByteArray((String)params.get("sign")));
        return bverify;
	}

	@Override
	public String huaweiPaySign(String content, String private_key)throws Exception {
		String charset = "utf-8";
        try{
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(com.qinglan.sdk.server.common.Base64.decodeToByteArray(private_key));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes(charset));

            byte[] signed = signature.sign();

            return com.qinglan.sdk.server.common.Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
	}

	@Override
	public String muzhiMd5(String content) throws Exception{
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		md.update(content.getBytes());
		byte tmp[] = md.digest();
		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte byte0 = tmp[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		s = new String(str);
		return s;
	}

	@Override
	public boolean verifyAtet(String content, String sign, String publicKey) {
		try{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initVerify(pubKey);
			signature.update(content.getBytes("utf-8"));

			return signature.verify(com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign));
		}catch (Exception e){
			LOGGER.error("verifyAtet error ", e);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean verifyQbao(String content, String sign, String pub_key)throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = com.qinglan.sdk.server.common.Base64.decodeToByteArray(pub_key);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

        java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");

        signature.initVerify(pubKey);
        signature.update(content.getBytes("utf-8"));

        boolean bverify = signature.verify(com.qinglan.sdk.server.common.Base64.decodeToByteArray(sign));
        return bverify;
	}

	@Override
	public String qbaoPaySign(String content, String private_key)throws Exception {
		String charset = "utf-8";
        try{
        	//解密私钥
            byte[] keyBytes = com.qinglan.sdk.server.common.Base64.decodeToByteArray(private_key);
            //构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            //指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //取私钥匙对象
            PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            //用私钥对信息生成数字签名
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(privateKey2);
            signature.update(content.getBytes(charset));
             
            return com.qinglan.sdk.server.common.Base64.encode(signature.sign());
        } catch (Exception e) {
        	LOGGER.error("qbaoPaySign error", e);
        }
        return "";
	}
}
