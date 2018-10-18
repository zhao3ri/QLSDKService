package com.qinglan.sdk.server.release.presentation.platform.dto;

import lombok.ToString;

@ToString
public class WdjSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String uid;
	private String token;
	
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}
