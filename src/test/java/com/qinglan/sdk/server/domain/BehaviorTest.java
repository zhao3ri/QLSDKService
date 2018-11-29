package com.qinglan.sdk.server.domain;

import java.io.Serializable;

public class BehaviorTest implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long rId;//角色ID
	private String rName;//角色名
	private Long fRTime;//首次登录时间
	private Long lLTime;//最后登录时间
	private Long fCTime;//首次创建角色时间
	private Long lLogoutTime;//最后退出时间
	private Integer lTToday;//登录次数
	private Long lHTime;//最后心跳时间
	private Long fPTime;//首次支付时间
	private Long lPTime;//最后支付时间
	private Integer pTToday;//今天支付次数
	private Long pRecord;//支付35天记录
	private Long lRecord;//登录35天记录

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getrId() {
		return rId;
	}

	public void setrId(Long rId) {
		this.rId = rId;
	}

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}

	public Long getfRTime() {
		return fRTime;
	}

	public void setfRTime(Long fRTime) {
		this.fRTime = fRTime;
	}

	public Long getfCTime() {
		return fCTime;
	}

	public void setfCTime(Long fCTime) {
		this.fCTime = fCTime;
	}

	public Integer getlTToday() {
		return lTToday;
	}

	public void setlTToday(Integer lTToday) {
		this.lTToday = lTToday;
	}

	public Long getlLTime() {
		return lLTime;
	}

	public void setlLTime(Long lLTime) {
		this.lLTime = lLTime;
	}

	public Long getlLogoutTime() {
		return lLogoutTime;
	}

	public void setlLogoutTime(Long lLogoutTime) {
		this.lLogoutTime = lLogoutTime;
	}

	public Long getlHTime() {
		return lHTime;
	}

	public void setlHTime(Long lHTime) {
		this.lHTime = lHTime;
	}

	public Long getlPTime() {
		return lPTime;
	}

	public void setlPTime(Long lPTime) {
		this.lPTime = lPTime;
	}

	public Long getfPTime() {
		return fPTime;
	}

	public void setfPTime(Long fPTime) {
		this.fPTime = fPTime;
	}

	public Integer getpTToday() {
		return pTToday;
	}

	public void setpTToday(Integer pTToday) {
		this.pTToday = pTToday;
	}

	public Long getpRecord() {
		return pRecord;
	}

	public void setpRecord(Long pRecord) {
		this.pRecord = pRecord;
	}

	public Long getlRecord() {
		return lRecord;
	}

	public void setlRecord(Long lRecord) {
		this.lRecord = lRecord;
	}



}