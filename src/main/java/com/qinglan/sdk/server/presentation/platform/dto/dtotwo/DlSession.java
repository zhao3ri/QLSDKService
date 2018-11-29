package com.qinglan.sdk.server.presentation.platform.dto.dtotwo;

import com.qinglan.sdk.server.presentation.platform.dto.SessionBase;
import com.qinglan.sdk.server.common.JsonMapper;

public class DlSession extends SessionBase {

    private int appid;
    private String token;
    private String umid;

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUmid() {
        return umid;
    }

    public void setUmid(String umid) {
        this.umid = umid;
    }

    @Override
    public String toString() {
        return JsonMapper.toJson(this);
    }

}