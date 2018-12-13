package com.qinglan.sdk.server.presentation.dto.channel.channel2;

import com.qinglan.sdk.server.presentation.dto.channel.SessionBase;

/**
 * Created by hoog on 2017/4/26.
 */
public class LingdongSession extends SessionBase {

    private String ygAppId;
    private String platformId;
    private String user_id;
    private String token;
    private String ldAppId;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLdAppId() {
        return ldAppId;
    }

    public void setLdAppId(String ldAppId) {
        this.ldAppId = ldAppId;
    }
}
