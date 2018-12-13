package com.qinglan.sdk.server.presentation.dto.channel.channel2;

import com.qinglan.sdk.server.presentation.dto.channel.SessionBase;

public class XmwSession extends SessionBase {

    /**
     * 授权应用游戏的ClientID。
     */
    private String client_id;

    /**
     * 授权应用游戏的ClientSecret。
     */
    private String client_secret;

    /**
     * 获取授权码的方式,可以选择authorization_code或refresh_token。
     */
    private String grant_type;

    /**
     * 当使用authorization_code时,该参数必填,传入授权验证码。
     */
    private String code;

    /**
     * 当使用refresh_token时,该参数必填,传入更新授权码。
     */
    private String refresh_token;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
