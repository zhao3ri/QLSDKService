package com.qinglan.sdk.server.presentation.platform.dto;

import lombok.ToString;

@ToString
public class BaiduSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	public String appId;//游戏ID
	private String accessToken;//授权的accessToken
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
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
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
}
