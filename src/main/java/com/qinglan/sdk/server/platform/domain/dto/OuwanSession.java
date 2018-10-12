package com.qinglan.sdk.server.platform.domain.dto;

import lombok.ToString;

@ToString
public class OuwanSession {
	private String zdappId;		//指点游戏ID
	private String platformId; 	//指点联运平台ID
	
	private String uid;			//用户id
	private String sign;		//签名
	private Long timestamp;		//登陆时间
	
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
