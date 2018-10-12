package com.qinglan.sdk.server.dao;

import com.qinglan.sdk.server.domain.dto.HeartbeatPattern;
import com.zhidian3g.ddd.annotation.event.Event;

@Event
public class HeartbeatEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HeartbeatPattern heartbeatPattern;

	public HeartbeatEvent(HeartbeatPattern heartbeatPattern) {
		this.heartbeatPattern= heartbeatPattern;
	}
	
	public HeartbeatPattern getHelper() {
		return heartbeatPattern;
	}
}
