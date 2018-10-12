package com.qinglan.sdk.server.domain;

import com.qinglan.sdk.server.domain.dto.InitialPattern;
import com.zhidian3g.ddd.annotation.event.Event;

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
