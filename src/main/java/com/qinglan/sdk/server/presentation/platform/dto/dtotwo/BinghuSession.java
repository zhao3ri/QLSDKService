package com.qinglan.sdk.server.presentation.platform.dto.dtotwo;

import com.qinglan.sdk.server.presentation.platform.dto.SessionBase;

public class BinghuSession extends SessionBase {
    private String app_id;
    private String mem_id;
    private String user_token;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }
}
