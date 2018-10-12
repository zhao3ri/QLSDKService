package com.qinglan.sdk.server.service;

import com.qinglan.sdk.server.domain.Order;
import com.qinglan.sdk.server.domain.dto.OrderGeneratePattern;

public interface OrderService {
	
	String saveOrder(OrderGeneratePattern params);
	
	Order getOrderByOrderId(String orderId);
	
	int paySuccess(String orderId);
	
	int payFail(String orderId, String errorMsg);
	
	int notifyResend(String orderId, String errorMsg);
	
	int notifyFail(String orderId, String errorMsg);
	
	int notifySuccess(String orderId);
	
}
