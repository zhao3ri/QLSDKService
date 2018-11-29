package com.qinglan.sdk.server.domain.basic.event;

import java.io.Serializable;

import com.qinglan.sdk.server.data.annotation.event.Event;

@Event
public class OrderPayEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private String orderId;

	public OrderPayEvent(String orderId) {
		this.orderId = orderId;
	}

	public String getOrder() {
		return orderId;
	}
}
