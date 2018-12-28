package com.qinglan.sdk.server.channel.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class YSPayResponseEntity extends YSBaseResponseEntity implements Serializable {
    private static final long serialVersionUID = 2506840742961490139L;
    private String appId;// 应用ID，在nox 平台上申请获取的
    private String uid;// 用户ID
    private Integer payStatus;// 支付状态1待支付 2成功 3失败
    private String goodsTitle;// 商品名称
    private String goodsOrderId;// 商品订单
    private String goodsDesc;// 商品描述
    private Long orderMoney;// 订单金额(分)
    private String orderId;// 夜神订单号
    private Timestamp orderTime;// 订单时间
    private String privateInfo;// 用户私有信息
    private String appName;// 应用名称

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrivateInfo() {
        return privateInfo;
    }

    public void setPrivateInfo(String privateInfo) {
        this.privateInfo = privateInfo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsOrderId() {
        return goodsOrderId;
    }

    public void setGoodsOrderId(String goodsOrderId) {
        this.goodsOrderId = goodsOrderId;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public Long getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Long orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "YSPayResponseEntity [appId=" + appId + ", uid=" + uid + ", payStatus=" + payStatus + ", goodsTitle=" + goodsTitle + ", goodsOrderId=" + goodsOrderId + ", goodsDesc=" + goodsDesc + ", orderMoney=" + orderMoney + ", orderId=" + orderId
                + ", orderTime=" + orderTime + ", privateInfo=" + privateInfo + ", appName=" + appName + "]";
    }
}
