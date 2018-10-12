package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.platform.domain.dto.BaseSession;

/**
 * Created by hoog on 2017/5/4.
 */
public class UcSession extends BaseSession {

    private String ygAppId;
    private String platformId;
    private String callbackInfo;
    private String amount;
    private String notifyUrl;
    private String cpOrderId;
    private String accountId;

    @Override
    public String getYgAppId() {
        return ygAppId;
    }

    @Override
    public void setYgAppId(String ygAppId) {
        this.ygAppId = ygAppId;
    }

    @Override
    public String getPlatformId() {
        return platformId;
    }

    @Override
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getCallbackInfo() {
        return callbackInfo;
    }

    public void setCallbackInfo(String callbackInfo) {
        this.callbackInfo = callbackInfo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getCpOrderId() {
        return cpOrderId;
    }

    public void setCpOrderId(String cpOrderId) {
        this.cpOrderId = cpOrderId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
