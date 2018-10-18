package com.qinglan.sdk.server.release.domain.basic.event;

import com.qinglan.sdk.server.release.presentation.basic.dto.QuitPattern;
import com.zhidian3g.ddd.annotation.event.Event;

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
