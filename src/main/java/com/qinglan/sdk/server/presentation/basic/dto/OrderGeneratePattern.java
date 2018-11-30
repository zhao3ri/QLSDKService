package com.qinglan.sdk.server.presentation.basic.dto;

import java.io.Serializable;

import com.qinglan.sdk.server.common.StringUtil;

import lombok.ToString;

@ToString(callSuper = true)
public class OrderGeneratePattern extends OrderBasicInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String loginTime;
    private Integer payType = 1;
    private String ip;
    private String gameName;
    private String packageName;

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
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

    public boolean isEmpty() {
        if (null == getAppId()) return true;
        if (null == getPlatformId()) return true;
        if (null == getUid() || getUid().trim().isEmpty()) return true;
        if (null == getZoneId() || getZoneId().trim().isEmpty()) return true;
        if (null == getRoleId() || getRoleId().trim().isEmpty()) return true;
        if (null == getCpOrderId() || getCpOrderId().trim().isEmpty()) return true;
        if (null == getAmount()) return true;
        if (null == getNotifyUrl() || getNotifyUrl().trim().isEmpty()) return true;
        if (null == getFixed()) return true;
        if (null == getLoginTime() || getLoginTime().trim().isEmpty()) return true;
        if (null == getDeviceId() || getDeviceId().trim().isEmpty()) return true;
        if (null == getClientType()) return true;
        return false;
    }

}
