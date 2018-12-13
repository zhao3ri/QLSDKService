package com.qinglan.sdk.server.presentation.dto.channel;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class YunxiaotanSession {
    private String appId ;
    private  String sid ;
    private String platformId ;
    private String zdAppId ;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
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
}
