package com.qinglan.sdk.server.presentation.dto.channel;

import lombok.ToString;

@ToString
public class QihooSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String access_token;//授权的access token
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
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
	
}
