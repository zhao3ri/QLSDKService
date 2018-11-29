package com.qinglan.sdk.server.presentation.platform.dto.dtotwo;

import com.qinglan.sdk.server.presentation.platform.dto.SessionBase;

/**
 * Created by engine on 2017/1/6.
 */
public class QinmuSession extends SessionBase {
    private String authorize_code ;
    private String app_key ;

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getAuthorize_code() {
        return authorize_code;
    }

    public void setAuthorize_code(String authorize_code) {
        this.authorize_code = authorize_code;
    }
}
