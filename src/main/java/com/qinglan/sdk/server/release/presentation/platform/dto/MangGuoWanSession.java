package com.qinglan.sdk.server.release.presentation.platform.dto;

/**
 * Created by engine on 16/9/29.
 */
public class MangGuoWanSession extends SessionBase {
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