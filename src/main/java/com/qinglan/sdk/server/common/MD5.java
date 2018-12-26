package com.qinglan.sdk.server.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MD5 {
	private static final Logger logger = LoggerFactory.getLogger(MD5.class);

	public static String encode(String raw) {
		byte[] digest;
		try {
			// MessageDigest#digest not thread safe, so can't be singleton
			digest = MessageDigest.getInstance("MD5").digest(raw.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(raw, e);
			throw new IllegalStateException("UTF-8 not supported!");
		} catch (NoSuchAlgorithmException e) {
			logger.error(raw, e);
			throw new IllegalArgumentException("No such algorithm [MD5]");
		}
		return new String(Hex.encodeHex(digest));
	}
	
	public static String encode(byte[] raw) {
		byte[] digest;
		try {
			// MessageDigest#digest not thread safe, so can't be singleton
			digest = MessageDigest.getInstance("MD5").digest(raw);
		} catch (Exception e) {
			logger.error("encode error",e);
			throw new IllegalStateException("UTF-8 not supported!");
		} 
		return new String(Hex.encodeHex(digest));
	}
	
	public static String encodeSalt(String raw, String salt) {
		byte[] digest;
		try {
			if (StringUtils.isNotBlank(salt)) {
				raw = raw + "{" + salt.toString() + "}";
			}
			digest = MessageDigest.getInstance("MD5").digest(raw.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(raw, e);
			throw new IllegalStateException("UTF-8 not supported!");
		} catch (NoSuchAlgorithmException e) {
			logger.error(raw, e);
			throw new IllegalArgumentException("No such algorithm [MD5]");
		}
		return new String(Hex.encodeHex(digest));
	}

	public static void main(String[] args) {
		String s = "82081951429085698";
		System.out.println(encode(s));
	}

}
