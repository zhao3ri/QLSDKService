package com.qinglan.sdk.server.presentation.platform.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class YuewanSession extends SessionBase {

	private String username;		//登录成功后，用户的用户名
	private String logintime;		//用户登录的时间戳
	private String sign;			//用来登录验签对比
	
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
