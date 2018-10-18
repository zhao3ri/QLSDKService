package com.qinglan.sdk.server.release.presentation.platform.dto;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class YijieSdkSession {
    private  String sdk;
    private String platformId ;
    private String zdAppId ;
    private String app;
    private String uin;
    private String sess;

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getZdAppId() {
        return zdAppId;
    }

    public void setZdAppId(String zdAppId) {
        this.zdAppId = zdAppId;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getSess() {
        return sess;
    }

    public void setSess(String sess) {
        this.sess = sess;
    }
}
