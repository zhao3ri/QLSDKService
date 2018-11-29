package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class HTCSession {
	private String zdappId;//指点游戏ID
	private String platformId; //指点联运平台ID
	
	private String content;
	private String sign;
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
