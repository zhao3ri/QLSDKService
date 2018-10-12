package com.qinglan.sdk.server.domain;

import com.qinglan.sdk.server.domain.dto.LoginPattern;
import com.zhidian3g.ddd.annotation.event.Event;

@Event
public class LoginEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoginPattern loginPattern;

	public LoginEvent(LoginPattern loginPattern) {
		this.loginPattern=loginPattern;
	}
	
	public LoginPattern getHelper() {
		return loginPattern;
	}
}
