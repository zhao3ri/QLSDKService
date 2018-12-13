package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.dto.HeartbeatPattern;

@Event
public class HeartbeatEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HeartbeatPattern heartbeatPattern;

	public HeartbeatEvent(HeartbeatPattern heartbeatPattern) {
		this.heartbeatPattern=heartbeatPattern;
	}
	
	public HeartbeatPattern getHelper() {
		return heartbeatPattern;
	}
}
