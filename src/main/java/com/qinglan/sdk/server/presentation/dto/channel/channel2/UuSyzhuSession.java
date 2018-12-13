package com.qinglan.sdk.server.presentation.dto.channel.channel2;

import com.qinglan.sdk.server.presentation.dto.channel.SessionBase;
import com.qinglan.sdk.server.common.JsonMapper;

public class UuSyzhuSession extends SessionBase {
    private String appid;
    private String token;
    private String cporderno;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCporderno() {
        return cporderno;
    }

    public void setCporderno(String cporderno) {
        this.cporderno = cporderno;
    }

    @Override
    public String toString() {
        return JsonMapper.toJson(this);
    }
}
