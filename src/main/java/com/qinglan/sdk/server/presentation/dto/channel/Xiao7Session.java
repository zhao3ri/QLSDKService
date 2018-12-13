package com.qinglan.sdk.server.presentation.dto.channel;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class Xiao7Session {
    private  String tokenkey;
    private String platformId ;
    private String zdAppId ;
    private String appkey;

    public String getTokenkey() {
        return tokenkey;
    }

    public void setTokenkey(String tokenkey) {
        this.tokenkey = tokenkey;
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

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
