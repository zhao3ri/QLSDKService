package com.qinglan.sdk.server.platform.domain.dto;

import lombok.ToString;

@ToString
public class SougouSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String gid; //搜狗游戏Id
	private String user_id;
	private String session_key;

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

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getSession_key() {
		return session_key;
	}

	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}
	
	
}
