package com.qinglan.sdk.server.platform.domain.dto;

public class LiebaoSession extends BaseSession {

	private String gameid;

	private String username;

	private String logintime;

	public String getGameid() {
		return gameid;
	}

	public void setGameid(String gameid) {
		this.gameid = gameid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLogintime() {
		return logintime;
	}

	public void setLogintime(String logintime) {
		this.logintime = logintime;
	}
}
