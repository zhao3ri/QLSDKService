package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.Date;

public class ChannelEntity implements Serializable {
    private Integer id;

    private String channelName;

    private String channelCallbackUrl;

    private Date createTime;
    private Integer balance;
    private Integer version;
    private Integer newversion;
    private String business;
    private String phone;
    private String verifyUrl;

    private static final long serialVersionUID = 1L;

    public Integer getNewversion() {
        return newversion;
    }

    public void setNewversion(Integer newversion) {
        this.newversion = newversion;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName == null ? null : channelName.trim();
    }

    public String getChannelCallbackUrl() {
        return channelCallbackUrl;
    }

    public void setChannelCallbackUrl(String channelCallbackUrl) {
        this.channelCallbackUrl = channelCallbackUrl == null ? null : channelCallbackUrl.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVerifyUrl() {
        return verifyUrl;
    }

    public void setVerifyUrl(String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }
}