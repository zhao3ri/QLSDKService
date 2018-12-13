package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.dto.LoginPattern;

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
