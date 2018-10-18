package com.qinglan.sdk.server.release.presentation.platform.dto;

import java.io.Serializable;

public class QixiaziPayCallback implements Serializable{

	private static final long serialVersionUID = -4233798138333925923L;

	private String exorderno;
	private String transid;
	private String appid;
	private Integer waresid;
	private Integer feetype;
	private Integer money;
	private Integer count;
	private Integer result;
	private Integer transtype;
	private String transtime;
	private String cpprivate;
	private String paytype;
	private String uid;
	public String getExorderno() {
		return exorderno;
	}
	public void setExorderno(String exorderno) {
		this.exorderno = exorderno;
	}
	public String getTransid() {
		return transid;
	}
	public void setTransid(String transid) {
		this.transid = transid;
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
	public Integer getMoney() {
		return money;
	}
	public void setMoney(Integer money) {
		this.money = money;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public Integer getTranstype() {
		return transtype;
	}
	public void setTranstype(Integer transtype) {
		this.transtype = transtype;
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
	public String getPaytype() {
		return paytype;
	}
	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
