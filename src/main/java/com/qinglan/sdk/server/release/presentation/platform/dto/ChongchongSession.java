package com.qinglan.sdk.server.release.presentation.platform.dto;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class ChongchongSession {
    private  String token;
    private String platformId ;
    private String zdAppId ;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
