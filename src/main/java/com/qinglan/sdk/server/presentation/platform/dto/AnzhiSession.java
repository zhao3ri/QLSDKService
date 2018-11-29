package com.qinglan.sdk.server.presentation.platform.dto;

import lombok.ToString;

@ToString
public class AnzhiSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String account;
	private String sid;

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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
}
