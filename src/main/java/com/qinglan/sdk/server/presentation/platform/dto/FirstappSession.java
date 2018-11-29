package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class FirstappSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String appid;			//渠道应用id
	private String username;		//渠道用户名
	
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
