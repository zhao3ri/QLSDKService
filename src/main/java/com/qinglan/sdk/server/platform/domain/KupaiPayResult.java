package com.qinglan.sdk.server.platform.domain;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

public class KupaiPayResult implements Serializable{
	private static final long serialVersionUID = -4763218676745004985L;
	
	private Integer transtype;
	private String cporderid;
	private String transid;
	private String appuserid;
	private String appid;
	private Integer waresid;
	private Integer feetype;
	private Double money;
	private String currency;
	private Integer result;
	private String transtime;
	private String cpprivate;
	private Integer paytype;
	
	public Integer getTranstype() {
		return transtype;
	}
	public void setTranstype(Integer transtype) {
		this.transtype = transtype;
	}
	public String getCporderid() {
		return cporderid;
	}
	public void setCporderid(String cporderid) {
		this.cporderid = cporderid;
	}
	public String getTransid() {
		return transid;
	}
	public void setTransid(String transid) {
		this.transid = transid;
	}
	public String getAppuserid() {
		return appuserid;
	}
	public void setAppuserid(String appuserid) {
		this.appuserid = appuserid;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public Integer getWaresid() {
		return waresid;
	}
	public void setWaresid(Integer waresid) {
		this.waresid = waresid;
	}
	public Integer getFeetype() {
		return feetype;
	}
	public void setFeetype(Integer feetype) {
		this.feetype = feetype;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public String getTranstime() {
		return transtime;
	}
	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}
	public String getCpprivate() {
		return cpprivate;
	}
	public void setCpprivate(String cpprivate) {
		this.cpprivate = cpprivate;
	}
	public Integer getPaytype() {
		return paytype;
	}
	public void setPaytype(Integer paytype) {
		this.paytype = paytype;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
