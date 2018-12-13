package com.qinglan.sdk.server.presentation.dto.channel;

import com.qinglan.sdk.server.common.JsonMapper;

public class PaojiaoSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	private String token;			//泡椒SDK登录成功后返回的会话标识
	private String appId;				//泡椒appId
	
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
