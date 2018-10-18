package com.qinglan.sdk.server.release.presentation.platform.dto;

import lombok.ToString;

/**
 * Created by engine on 16/6/12.
 */
@ToString
public class QuickSdkSession {
    private  String token;
    private String platformId ;
    private String zdAppId ;
    private String product_code;
    private String uid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
