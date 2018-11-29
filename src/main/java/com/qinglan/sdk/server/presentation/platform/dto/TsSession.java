package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class TsSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String gid;				//37游戏Id
	private String token;			//37 手游生成的加密校验令牌
	private String pid;				//联运商 ID
	
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
