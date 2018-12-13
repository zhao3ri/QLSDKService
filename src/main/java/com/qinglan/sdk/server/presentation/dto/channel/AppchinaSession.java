package com.qinglan.sdk.server.presentation.dto.channel;

import lombok.ToString;

@ToString
public class AppchinaSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String ticket;
	private String appId;
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
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
}
