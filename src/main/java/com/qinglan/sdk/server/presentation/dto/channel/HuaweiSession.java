package com.qinglan.sdk.server.presentation.dto.channel;

import com.qinglan.sdk.server.common.JsonMapper;

public class HuaweiSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String accessToken;		//华为accessToken
	
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
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
