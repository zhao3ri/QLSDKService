package com.qinglan.sdk.server.presentation.dto.channel;

import com.qinglan.sdk.server.common.JsonMapper;

public class YihuanSession {
	private String zdAppId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String timesTamp ;
	private String userId ;

	public String getZdAppId() {
		return zdAppId;
	}

	public void setZdAppId(String zdAppId) {
		this.zdAppId = zdAppId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getTimesTamp() {
		return timesTamp;
	}

	public void setTimesTamp(String timesTamp) {
		this.timesTamp = timesTamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
