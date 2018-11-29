package com.qinglan.sdk.server.application.basic;

import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.presentation.basic.dto.OrderGeneratePattern;

public interface OrderService {
	
	String saveOrder(OrderGeneratePattern params);
	
	Order getOrderByOrderId(String orderId);
	
	int paySuccess(String orderId);
	
	int payFail(String orderId,String errorMsg);
	
	int notifyResend(String orderId,String errorMsg);
	
	int notifyFail(String orderId,String errorMsg);
	
	int notifySuccess(String orderId);
	
}
