package com.qinglan.sdk.server.presentation.dto.channel;

public class KupaiSession {
	private String zdappId;		//指点游戏ID
	private String platformId; 	//指点联运平台ID
	
	private String code; //authorization code
	private String appId; //酷派appId
	
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}
