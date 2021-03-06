package com.qinglan.sdk.server.dto;

public class QueryOrderRequest extends BaseDto {
    private String orderId;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public boolean isEmpty() {
        return getChannelId() == 0 || getGameId() == 0 || orderId == null || orderId.isEmpty();
    }
}
