package com.qinglan.sdk.server.presentation.dto.channel;

import java.io.Serializable;

public class AnzhiPayCallback implements Serializable{
	
	private static final long serialVersionUID = 5262094137345004428L;
	
	private String uid;
	private String orderId;
	private Integer orderAmount;
	private String orderTime;
	private String orderAccount;
	private Integer code;
	private String msg;
	private Integer payAmount;
	private String cpInfo;
	private Long notifyTime;
	private String memo;
	private Integer redBagMoney;
    private String cpCustomInfo ;

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Integer orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getOrderAccount() {
		return orderAccount;
	}
	public void setOrderAccount(String orderAccount) {
		this.orderAccount = orderAccount;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(Integer payAmount) {
		this.payAmount = payAmount;
	}
	public String getCpInfo() {
		return cpInfo;
	}
	public void setCpInfo(String cpInfo) {
		this.cpInfo = cpInfo;
	}
	public Long getNotifyTime() {
		return notifyTime;
	}
	public void setNotifyTime(Long notifyTime) {
		this.notifyTime = notifyTime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getRedBagMoney() {
		return redBagMoney;
	}
	public void setRedBagMoney(Integer redBagMoney) {
		this.redBagMoney = redBagMoney;
	}

	public String getCpCustomInfo() {
		return cpCustomInfo;
	}

	public void setCpCustomInfo(String cpCustomInfo) {
		this.cpCustomInfo = cpCustomInfo;
	}

}
