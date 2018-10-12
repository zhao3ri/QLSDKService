package com.qinglan.sdk.server.platform.domain.dto;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class KuaifaSession {
    private String platformId ;
    private String zdAppId ;
    private String token ;
    private String openid ;
    private String timestamp ;
    private String gamekey ;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGamekey() {
        return gamekey;
    }

    public void setGamekey(String gamekey) {
        this.gamekey = gamekey;
    }
}
