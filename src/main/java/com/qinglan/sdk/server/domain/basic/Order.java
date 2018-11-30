package com.qinglan.sdk.server.domain.basic;

import com.qinglan.sdk.server.presentation.basic.dto.OrderBasicInfo;

import java.io.Serializable;
import java.util.Date;

public class Order extends OrderBasicInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    //订单状态
    public final static Integer STATUS_SUBMITSUCCESS = 0;        //提交成功
    public final static Integer STATUS_SUBMITFAIL = 1;        //提交失败
    public final static Integer STATUS_PAYSUCCESS = 2;        //支付成功
    public final static Integer STATUS_PAYFAIL = 3;        //支付失败
    //通知CP状态
    public final static Integer NOTIFYSTATUS_DEFAULT = 0;        //默认状态
    public final static Integer NOTIFYSTATUS_WAIT = 1;        //等待通知
    public final static Integer NOTIFYSTATUS_SUCCESS = 2;        //通知完成
    public final static Integer NOTIFYSTATUS_FAIL = 3;        //通知失败
    public final static Integer NOTIFYSTATUS_RESEND = 4;        //重发通知

    public final static int SUCCESS = 0;    //成功
    public final static int FAIL = 1;    //失败
    public final static int REPEAT = 2;    //重复调用
    public final static int INVALID = 3;    //无效参数
    public final static int BALANCE_ERROR = 4;    //余额不够

    private Long id;

    private Long gameId;

    private Integer platformId;

    private String errorMsg;

    private Integer status;

    private Integer notifyStatus;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
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

    public String getCpOrderId() {
        return cpOrderId;
    }

    public void setCpOrderId(String cpOrderId) {
        this.cpOrderId = cpOrderId == null ? null : cpOrderId.trim();
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo == null ? null : extInfo.trim();
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


    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public Integer getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(Integer notifyStatus) {
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

    public Integer getGold() {
        return gold;
    }

    public Integer getSelfpay() {
        return selfpay;
    }

    public void setSelfpay(Integer selfpay) {
        this.selfpay = selfpay;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }
}