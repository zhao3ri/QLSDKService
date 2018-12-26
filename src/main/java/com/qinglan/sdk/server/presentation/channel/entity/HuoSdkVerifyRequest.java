package com.qinglan.sdk.server.presentation.channel.entity;

public class HuoSdkVerifyRequest extends BaseRequest {
    private String appId;
    private String memId;
    private String userToken;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
