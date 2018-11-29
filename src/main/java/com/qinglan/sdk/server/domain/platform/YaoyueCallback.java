package com.qinglan.sdk.server.domain.platform;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class YaoyueCallback implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String merchantId;//合作商ID
	private String appId;//应用ID
	private String userName;//用户账号
	private String tradeNo;//交易号
	private String channelCode;//支付通道代码
	private String amount;//充值金额（元，两位小数）
	private String createTime;//交易时间（毫秒）
	private String area;//分区信息
	private String chid;//推广渠道标识
	private String note;//附加字段，用于应用方自定参数，原样传送
	private String sign;
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getChid() {
		return chid;
	}
	public void setChid(String chid) {
		this.chid = chid;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
