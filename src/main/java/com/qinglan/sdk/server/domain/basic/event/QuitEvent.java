package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.dto.QuitPattern;

@Event
public class QuitEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private QuitPattern quitPattern;

	public QuitEvent(QuitPattern quitPattern) {
		this.quitPattern=quitPattern;
	}
	
	public QuitPattern getHelper() {
		return quitPattern;
	}
}
