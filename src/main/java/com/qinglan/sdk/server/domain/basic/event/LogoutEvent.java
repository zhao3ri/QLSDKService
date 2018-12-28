package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.dto.LogoutPattern;

@Event
public class LogoutEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LogoutPattern logoutPattern;

	public LogoutEvent(LogoutPattern logoutPattern) {
		this.logoutPattern=logoutPattern;
	}
	
	public LogoutPattern getHelper() {
		return logoutPattern;
	}
}
