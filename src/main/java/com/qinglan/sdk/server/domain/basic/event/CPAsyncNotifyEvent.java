package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;

import java.io.Serializable;

@Event
public class CPAsyncNotifyEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String orderId;

	public CPAsyncNotifyEvent(String orderId) {
		this.orderId = orderId;
	}

	public String getHelper() {
		return orderId;
	}
}
