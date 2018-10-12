package com.qinglan.sdk.server.common;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 类说明  随机生成类
 **/
public class RandomTool {
	//时间格式
	final static String DATE_FORMAT = "yyyyMMddHHmmssSSS";
	//同步锁
	private static Object lock = new Object();
	
	/**
	 * 生成充值交易号
	 * 规则：17位时间+15位随机数=32位订单号
	 * @return
	 */
	public static String getOrderId(){
		String tradeNo = DateFormatUtils.format(new Date(), DATE_FORMAT);
		synchronized(lock){
			tradeNo += RandomStringUtils.randomNumeric(15);
		}
		return tradeNo;
	}
	
	/**
	 * 生成充值交易号
	 * 规则：17位时间 + N位随机
	 * @param size 随机位数
	 * @return
	 */
	public static String getOrderId(int size){
		String tradeNo = DateFormatUtils.format(new Date(), DATE_FORMAT);
		synchronized(lock){
			tradeNo += RandomStringUtils.randomNumeric(size);
		}
		return tradeNo;
	}
	
	public static void main(String[] args) {
		System.out.println(RandomTool.getOrderId());
	}
}
