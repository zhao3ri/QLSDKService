package com.qinglan.sdk.server.domain.platform;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

import lombok.ToString;

@ToString
public class YouleCallback implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String merchantId;				//合作商ID
	private String appId;					//应用ID
	private String uid;						//用户账号
	private String tradeNo;					//交易号
	private String channelCode;				//支付通道代码
	private String amount;					//充值金额（元，两位小数）
	private String createTime;				//交易时间（毫秒）
	private String content;					//支付内容描述
	private String area;					//分区信息
	private String chid;					//推广渠道标识
	private String note;					//附加字段，用于应用方自定参数，原样传送
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
