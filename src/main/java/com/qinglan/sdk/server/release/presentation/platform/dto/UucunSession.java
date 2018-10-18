package com.qinglan.sdk.server.release.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class UucunSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String token;			//使用getUserInfo接口获取到的accessToken值
	
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
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
