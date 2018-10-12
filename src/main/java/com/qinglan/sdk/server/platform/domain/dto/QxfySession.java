package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class QxfySession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String sdk;				//渠道在易接服务器上的 ID
	private String app;				//CP 游戏在易接服务器上的ID
	private String uin;				//用户在渠道上面的编号
	private String sess;			//用户在登陆渠道后，由渠道返回给客户端的 SessionId
	
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
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	public String getSess() {
		return sess;
	}
	public void setSess(String sess) {
		this.sess = sess;
	}
	public String getSdk() {
		return sdk;
	}
	public void setSdk(String sdk) {
		this.sdk = sdk;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
