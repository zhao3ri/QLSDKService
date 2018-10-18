package com.qinglan.sdk.server.release.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class GioneeSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String amigoToken;
	
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
	public String getAmigoToken() {
		return amigoToken;
	}
	public void setAmigoToken(String amigoToken) {
		this.amigoToken = amigoToken;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
