package com.qinglan.sdk.server.presentation.dto.channel;

import com.qinglan.sdk.server.common.JsonMapper;

public class YoukuSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String sessionid;

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

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
