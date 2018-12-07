package com.qinglan.sdk.server.presentation.channel.entity;

import com.qinglan.sdk.server.presentation.basic.dto.OrderGenerateRequest;

public class UCOrderSignRequest extends BaseRequest {
    private String uid;
    private String channelOrderId;
    private String extInfo;
    private Integer amount;
    private String notifyUrl;
    private String orderId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public static UCOrderSignRequest getOrderByBean(OrderGenerateRequest order) {
        UCOrderSignRequest request = new UCOrderSignRequest();
        request.setExtInfo(order.getExtInfo());
        request.setNotifyUrl(order.getNotifyUrl());
        request.setAmount(order.getAmount());
        request.setOrderId(order.getOrderId());
        request.setUid(order.getUid());
        return request;
    }
}
