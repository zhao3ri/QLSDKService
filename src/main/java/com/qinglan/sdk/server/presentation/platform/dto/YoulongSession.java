package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class YoulongSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String token;			//用户token
	private String pid;				//由游龙平台提供的参数

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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
