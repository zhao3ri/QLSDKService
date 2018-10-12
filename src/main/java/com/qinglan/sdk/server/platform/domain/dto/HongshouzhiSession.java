package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class HongshouzhiSession {
	private String zdAppId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String app_id;
	private String mem_id;
	private String user_token;

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

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getMem_id() {
		return mem_id;
	}

	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
