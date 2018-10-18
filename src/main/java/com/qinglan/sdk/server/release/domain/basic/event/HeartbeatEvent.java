package com.qinglan.sdk.server.release.domain.basic.event;

import com.qinglan.sdk.server.release.presentation.basic.dto.HeartbeatPattern;
import com.zhidian3g.ddd.annotation.event.Event;

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
