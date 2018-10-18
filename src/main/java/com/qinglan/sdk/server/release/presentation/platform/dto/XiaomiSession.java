package com.qinglan.sdk.server.release.presentation.platform.dto;

import lombok.ToString;

@ToString
public class XiaomiSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String appId;//小米游戏ID
	private String session;//小米会话ID
	private String uid;//小米用户ID
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
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	
	
}
