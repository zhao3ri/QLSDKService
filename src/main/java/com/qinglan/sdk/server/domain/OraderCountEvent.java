package com.qinglan.sdk.server.domain;

import com.qinglan.sdk.server.domain.dto.OrderGeneratePattern;
import com.zhidian3g.ddd.annotation.event.Event;

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