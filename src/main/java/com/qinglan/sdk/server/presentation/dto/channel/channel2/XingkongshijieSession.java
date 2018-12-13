package com.qinglan.sdk.server.presentation.dto.channel.channel2;

import com.qinglan.sdk.server.presentation.dto.channel.SessionBase;

/**
 * Created by hoog on 2017/5/4.
 */
public class XingkongshijieSession extends SessionBase {

    private String ygAppId;
    private String platformId;
    private String appid;
    private String logintoken;

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

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getLogintoken() {
        return logintoken;
    }

    public void setLogintoken(String logintoken) {
        this.logintoken = logintoken;
    }
}
