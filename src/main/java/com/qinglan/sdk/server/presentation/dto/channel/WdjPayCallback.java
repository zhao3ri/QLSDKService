package com.qinglan.sdk.server.presentation.dto.channel;

import java.io.Serializable;

public class WdjPayCallback implements Serializable{

	private static final long serialVersionUID = 4309449626605788466L;
	
	private Long timeStamp;
	private String orderId;
	private Integer money;
	private String chargeType;
	private String appKeyId;
	private String buyerId;
	private String out_trade_no;
	private String cardNo;
	private String discount ;
	private String settlement;

	public String getSettlement() {
		return settlement;
	}

	public void setSettlement(String settlement) {
		this.settlement = settlement;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getMoney() {
		return money;
	}
	public void setMoney(Integer money) {
		this.money = money;
	}
	public String getChargeType() {
		return chargeType;
	}
	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}
	public String getAppKeyId() {
		return appKeyId;
	}
	public void setAppKeyId(String appKeyId) {
		this.appKeyId = appKeyId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
}
