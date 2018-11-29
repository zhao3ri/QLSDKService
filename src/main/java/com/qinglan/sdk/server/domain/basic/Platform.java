package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.Date;

public class Platform implements Serializable {
    private Integer id;

    private String platformName;

    private String platformCallbackUrl;

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

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName == null ? null : platformName.trim();
    }

    public String getPlatformCallbackUrl() {
        return platformCallbackUrl;
    }

    public void setPlatformCallbackUrl(String platformCallbackUrl) {
        this.platformCallbackUrl = platformCallbackUrl == null ? null : platformCallbackUrl.trim();
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