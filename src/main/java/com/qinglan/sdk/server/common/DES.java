package com.qinglan.sdk.server.common;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DES {
	
	private static final String DES = "DES";

	public static String encryptAndBase64(String text, String secretKey) {
		return Base64.encodeBase64String(encrypt(text.getBytes(), secretKey)); 
	}
	
	public static byte[] encrypt(String text, String secretKey) {
		return encrypt(text.getBytes(), secretKey);
	}
	
	public static byte[] encrypt(byte[] source, String secretKey) {
		try {
			SecretKeyFactory skeyFactory = SecretKeyFactory.getInstance(DES);
			Cipher cipher = Cipher.getInstance(DES);
			
			SecretKey deskey = skeyFactory.generateSecret(new DESKeySpec(secretKey.getBytes()));
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			byte[] result = cipher.doFinal(source);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("des encrypt exception");
		}
	}
	
	/**
	 * @param text: base64 encode string 
	 * @param secretKey
	 * @return UTF-8 encode string
	 */
	public static String decryptBase64(String text, String secretKey) {
		try {
			return (new String(decrypt(Base64.decodeBase64(text), secretKey), "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("decrypt Base64 text exception");
		}
	}
	
	public static byte[] decrypt(String text, String secretKey) {
		return decrypt(text.getBytes(), secretKey);
	}
	
	public static byte[] decrypt(byte[] source, String secretKey) {
		try {
			SecretKeyFactory skeyFactory = SecretKeyFactory.getInstance(DES);
			Cipher cipher = Cipher.getInstance(DES);
			
			SecretKey deskey = skeyFactory.generateSecret(new DESKeySpec(secretKey.getBytes()));
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			return cipher.doFinal(source);
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("des decrypt exception");
		}
	}

	public static void main(String[] args) throws Exception {
		String soureTxt = "860103028030931";
		String key = "!@#$%^*&(*((BGF$%Ssd$%fbf@#dbf*^$%#$";
		String str = null;
		System.out.println("明文：" + soureTxt);
		System.out.println("密匙：" + key);
		str = encryptAndBase64(soureTxt, key);
		System.out.println("加密：" + str);
		System.out.println("解密：" + decryptBase64(str, key));
	}
}