package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.platform.domain.dto.BaseSession;

/**
 * Created by engine on 2017/1/6.
 */
public class QinmuSession extends BaseSession {
    private String authorize_code ;
    private String app_key ;

    public String getAppKey() {
        return app_key;
    }

    public void setAppKey(String app_key) {
        this.app_key = app_key;
    }

    public String getAuthorizeCode() {
        return authorize_code;
    }

    public void setAuthorizeCode(String authorize_code) {
        this.authorize_code = authorize_code;
    }
}
