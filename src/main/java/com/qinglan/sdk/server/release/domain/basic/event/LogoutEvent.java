package com.qinglan.sdk.server.release.domain.basic.event;

import com.qinglan.sdk.server.release.presentation.basic.dto.LogoutPattern;
import com.zhidian3g.ddd.annotation.event.Event;

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
