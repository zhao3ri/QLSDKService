package com.qinglan.sdk.server.release.domain.basic.event;

import java.io.Serializable;

import com.zhidian3g.ddd.annotation.event.Event;

@Event
public class CPNotifyEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String orderId;

	public CPNotifyEvent(String orderId) {
		this.orderId = orderId;
	}

	public String getHelper() {
		return orderId;
	}
}
