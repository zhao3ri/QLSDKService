package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.basic.dto.OrderGeneratePattern;

@Event
public class OraderCountEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrderGeneratePattern orderGeneratePattern;

	public OraderCountEvent(OrderGeneratePattern orderGeneratePattern) {
		this.orderGeneratePattern=orderGeneratePattern;
	}
	
	public OrderGeneratePattern getHelper() {
		return orderGeneratePattern;
	}
}