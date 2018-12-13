package com.qinglan.sdk.server.presentation.dto.channel;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

public class AtetPayCallback implements Serializable{
	private static final long serialVersionUID = -7945665573337898930L;
	
	private String exOrderNo;
	private String payOrderNo;
	private String appId;
	private Integer amount;
	private Integer counts;
	private String payPoint;
	private String cpPrivateInfo;
	private Integer payType;
	private String transTime;
	private Integer result;
	
	public String getExOrderNo() {
		return exOrderNo;
	}
	public void setExOrderNo(String exOrderNo) {
		this.exOrderNo = exOrderNo;
	}
	public String getPayOrderNo() {
		return payOrderNo;
	}
	public void setPayOrderNo(String payOrderNo) {
		this.payOrderNo = payOrderNo;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getCounts() {
		return counts;
	}
	public void setCounts(Integer counts) {
		this.counts = counts;
	}
	public String getPayPoint() {
		return payPoint;
	}
	public void setPayPoint(String payPoint) {
		this.payPoint = payPoint;
	}
	public String getCpPrivateInfo() {
		return cpPrivateInfo;
	}
	public void setCpPrivateInfo(String cpPrivateInfo) {
		this.cpPrivateInfo = cpPrivateInfo;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
