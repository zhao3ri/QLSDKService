package com.qinglan.sdk.server.application;

import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.dto.OrderGenerateRequest;

public interface OrderService {
	
	String saveOrder(OrderGenerateRequest params);
	
	Order getOrderByOrderId(String orderId);
	
	int paySuccess(String orderId);
	
	int payFail(String orderId,String errorMsg);
	
	int notifyResend(String orderId,String errorMsg);
	
	int notifyFail(String orderId,String errorMsg);
	
	int notifySuccess(String orderId);
	
}
