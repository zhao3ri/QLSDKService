package com.qinglan.sdk.server.platform.domain;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

public class VivoPaySign implements Serializable{
	private static final long serialVersionUID = -5147536456064833500L;
	
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	private String cpOrderNumber;		//指点订单号
	
	private String cpId;			//Cp-id
	private String appId;			//平台应用ID
	private String orderTitle;		//商品的标题
	private String orderDesc;		//商品描述
	private String extInfo;			//CP透传参数
	
	public String getZdappId() {
		return zdappId;
	}

	public void setZdappId(String zdappId) {
		this.zdappId = zdappId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getCpOrderNumber() {
		return cpOrderNumber;
	}

	public void setCpOrderNumber(String cpOrderNumber) {
		this.cpOrderNumber = cpOrderNumber;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getOrderTitle() {
		return orderTitle;
	}

	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public String getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(String extInfo) {
		this.extInfo = extInfo;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
