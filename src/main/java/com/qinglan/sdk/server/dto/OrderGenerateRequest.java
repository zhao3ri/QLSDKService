package com.qinglan.sdk.server.dto;

import java.io.Serializable;

import com.qinglan.sdk.server.common.StringUtil;

import lombok.ToString;

@ToString(callSuper = true)
public class OrderGenerateRequest extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ip;
    private String gameName;
    private String packageName;
    private String uid;
    private String zoneId;
    private String roleId;
    private String roleName;
    private String channelOrderId;
    private String extInfo;
    private Integer amount;
    private String notifyUrl;
    private Integer fixed;
    private String deviceId;
    private Integer clientType;
    private String orderId;
    private Integer gold;
    private Integer selfpay = 0;
    private String loginTime;
    private Integer payType = 1;
    private String goodsId;
    private String goodsName;
    private int goodsCount;

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

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUid() {
        if (null == uid) return uid;
        if (uid.trim().length() > 5 && uid.trim().charAt(4) == '_') {
            return uid.substring(5, uid.length());
        }
        return uid;
    }

    public String getRoleName() {
        if (null == roleName) {
            return null;
        }
        return StringUtil.replaceBlank(roleName.replace("|", "#"));
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public Integer getSelfpay() {
        return selfpay;
    }

    public void setSelfpay(Integer selfpay) {
        this.selfpay = selfpay;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
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

    public boolean isEmpty() {
        if (null == getGameId()) return true;
        if (null == getChannelId()) return true;
        if (null == getUid() || getUid().trim().isEmpty()) return true;
        if (null == getZoneId() || getZoneId().trim().isEmpty()) return true;
        if (null == getRoleId() || getRoleId().trim().isEmpty()) return true;
//        if (null == getChannelOrderId() || getChannelOrderId().trim().isEmpty()) return true;
        if (null == getAmount()) return true;
        if (null == getNotifyUrl() || getNotifyUrl().trim().isEmpty()) return true;
        if (null == getFixed()) return true;
        if (null == getLoginTime() || getLoginTime().trim().isEmpty()) return true;
        if (null == getDeviceId() || getDeviceId().trim().isEmpty()) return true;
        if (null == getGoodsId() || getGoodsId().trim().isEmpty()) return true;
        if (null == getClientType()) return true;
        return false;
    }

}
