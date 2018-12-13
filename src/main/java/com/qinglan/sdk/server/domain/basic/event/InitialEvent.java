package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.dto.InitialPattern;

@Event
public class InitialEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InitialPattern initialPattern;

	public InitialEvent(InitialPattern initialPattern) {
		this.initialPattern=initialPattern;
	}
	
	public InitialPattern getHelper() {
		return initialPattern;
	}
}
