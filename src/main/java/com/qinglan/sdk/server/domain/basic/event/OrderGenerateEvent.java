package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.basic.dto.OrderGenerateRequest;

@Event
public class OrderGenerateEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrderGenerateRequest orderGenerateRequest;

	public OrderGenerateEvent(OrderGenerateRequest orderGenerateRequest) {
		this.orderGenerateRequest = orderGenerateRequest;
	}
	
	public OrderGenerateRequest getHelper() {
		return orderGenerateRequest;
	}
}
