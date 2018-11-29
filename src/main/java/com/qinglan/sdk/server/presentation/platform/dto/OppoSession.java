package com.qinglan.sdk.server.presentation.platform.dto;

import lombok.ToString;

@ToString
public class OppoSession {
	private String zdappId;		//指点游戏ID
	private String platformId; 	//指点联运平台ID
	private String token;
	private String ssoid;

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

	public String getSsoid() {
		return ssoid;
	}

	public void setSsoid(String ssoid) {
		this.ssoid = ssoid;
	}

}
