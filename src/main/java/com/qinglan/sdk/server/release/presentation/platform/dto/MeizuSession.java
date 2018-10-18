package com.qinglan.sdk.server.release.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class MeizuSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String app_id;
	private String session_id;
	private String uid;

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

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
