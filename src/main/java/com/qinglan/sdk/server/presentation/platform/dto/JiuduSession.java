package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

/**
 * @author Administrator
 *
 */
public class JiuduSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	private String gameId;			//玖度平台游戏ID
	private String sid;				//登录时爱上游戏平台返回的 SID
	
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
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
