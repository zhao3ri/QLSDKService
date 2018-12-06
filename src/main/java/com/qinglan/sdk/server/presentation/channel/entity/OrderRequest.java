package com.qinglan.sdk.server.presentation.channel.entity;

import com.qinglan.sdk.server.presentation.basic.dto.OrderGeneratePattern;

public class OrderRequest extends BaseRequest {
    private String uid;
    private String zoneId;
    private String roleId;
    private String roleName;
    private String cpOrderId;
    private String cpExtInfo;
    private Integer amount;
    private String notifyUrl;
    private Integer fixed;
    private String deviceId;
    private Integer clientType;
    private String orderId;
    private Integer gold;
    private Integer selfpay = 0;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCpOrderId() {
        return cpOrderId;
    }

    public void setCpOrderId(String cpOrderId) {
        this.cpOrderId = cpOrderId;
    }

    public String getCpExtInfo() {
        return cpExtInfo;
    }

    public void setCpExtInfo(String cpExtInfo) {
        this.cpExtInfo = cpExtInfo;
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

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getSelfpay() {
        return selfpay;
    }

    public void setSelfpay(Integer selfpay) {
        this.selfpay = selfpay;
    }

    public static OrderRequest getOrderByBean(OrderGeneratePattern order) {
        OrderRequest request = new OrderRequest();
        request.setCpExtInfo(order.getExtInfo());
        request.setNotifyUrl(order.getNotifyUrl());
        request.setAmount(order.getAmount());
        request.setCpOrderId(order.getCpOrderId());
        request.setUid(order.getUid());
        return request;
    }
}