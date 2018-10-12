package com.qinglan.sdk.server.platform.domain.dto;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class LewanSession {
    private  String token;
    private String platformId ;
    private String zdAppId ;
    private String app_id ;
    private String code ;
    private String password ;
    private String channelId;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

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
