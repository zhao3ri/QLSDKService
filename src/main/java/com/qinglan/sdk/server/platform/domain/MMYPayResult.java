package com.qinglan.sdk.server.platform.domain;

import java.io.Serializable;

public class MMYPayResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String uid;
	public String orderID;
	public String payType;
	public String productName;
	public String productPrice;
	public String productDesc;
	public String orderTime;
	public String tradeSign;
	public String tradeState;
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @return the orderID
	 */
	public String getOrderID() {
		return orderID;
	}
	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	/**
	 * @return the payType
	 */
	public String getPayType() {
		return payType;
	}
	/**
	 * @param payType the payType to set
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the productPrice
	 */
	public String getProductPrice() {
		return productPrice;
	}
	/**
	 * @param productPrice the productPrice to set
	 */
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	/**
	 * @return the productDesc
	 */
	public String getProductDesc() {
		return productDesc;
	}
	/**
	 * @param productDesc the productDesc to set
	 */
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	/**
	 * @return the orderTime
	 */
	public String getOrderTime() {
		return orderTime;
	}
	/**
	 * @param orderTime the orderTime to set
	 */
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	/**
	 * @return the tradeSign
	 */
	public String getTradeSign() {
		return tradeSign;
	}
	/**
	 * @param tradeSign the tradeSign to set
	 */
	public void setTradeSign(String tradeSign) {
		this.tradeSign = tradeSign;
	}
	/**
	 * @return the tradeState
	 */
	public String getTradeState() {
		return tradeState;
	}
	/**
	 * @param tradeState the tradeState to set
	 */
	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	
	
}
