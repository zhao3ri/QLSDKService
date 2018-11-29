package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class GuopanSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String gameUin;		//果盘分配给该游戏对应的唯一账号
	private String appId;		//果盘appid
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
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getGameUin() {
		return gameUin;
	}
	public void setGameUin(String gameUin) {
		this.gameUin = gameUin;
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
