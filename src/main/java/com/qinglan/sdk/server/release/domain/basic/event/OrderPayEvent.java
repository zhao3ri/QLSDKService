package com.qinglan.sdk.server.release.domain.basic.event;

import java.io.Serializable;

import com.zhidian3g.ddd.annotation.event.Event;

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
