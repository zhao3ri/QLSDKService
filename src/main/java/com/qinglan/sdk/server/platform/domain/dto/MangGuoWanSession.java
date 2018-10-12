package com.qinglan.sdk.server.platform.domain.dto;

/**
 * Created by engine on 16/9/29.
 */
public class MangGuoWanSession extends BaseSession {
    private String memId;
    private String userToken;

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
