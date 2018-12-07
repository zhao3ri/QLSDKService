package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    //订单状态
    public final static int ORDER_STATUS_SUBMIT_SUCCESS = 0;        //提交成功
    public final static int ORDER_STATUS_SUBMIT_FAIL = 1;        //提交失败
    public final static int ORDER_STATUS_PAYMENT_SUCCESS = 2;        //支付成功
    public final static int ORDER_STATUS_PAYMENT_FAIL = 3;        //支付失败
    //通知CP状态
    public final static int ORDER_NOTIFY_STATUS_DEFAULT = 0;        //默认状态
    public final static int ORDER_NOTIFY_STATUS_WAITING = 1;        //等待通知
    public final static int ORDER_NOTIFY_STATUS_SUCCESS = 2;        //通知完成
    public final static int ORDER_NOTIFY_STATUS_FAIL = 3;        //通知失败
    public final static int ORDER_NOTIFY_STATUS_RESEND = 4;        //重发通知

    public final static int SUCCESS = 0;    //成功
    public final static int FAIL = 1;    //失败
    public final static int REPEAT = 2;    //重复调用
    public final static int INVALID = 3;    //无效参数
    public final static int BALANCE_ERROR = 4;    //余额不够


    private long id;

    private long gameId;

    private int channelId;

    private String errorMsg;

    private int status;

    private int notifyStatus;

    private Date createTime;

    private Date updateTime;

    private String uid;
    private String zoneId;
    private String roleId;
    private String roleName;
    private String channelOrderId;
    private String extInfo;
    private int amount;
    private String notifyUrl;
    private int fixed;
    private String deviceId;
    private int clientType;
    private String orderId;
    private int gold;
    private int selfpay = 0;
    private String goodsId;
    private String goodsName;
    private int goodsCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId == null ? null : zoneId.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId == null ? null : channelOrderId.trim();
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo == null ? null : extInfo.trim();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl == null ? null : notifyUrl.trim();
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg == null ? null : errorMsg.trim();
    }


    public int getFixed() {
        return fixed;
    }

    public void setFixed(int fixed) {
        this.fixed = fixed;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(int notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getSelfpay() {
        return selfpay;
    }

    public void setSelfpay(int selfpay) {
        this.selfpay = selfpay;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }
}