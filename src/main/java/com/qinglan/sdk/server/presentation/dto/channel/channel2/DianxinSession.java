package com.qinglan.sdk.server.presentation.dto.channel.channel2;

import com.qinglan.sdk.server.presentation.dto.channel.SessionBase;
import com.qinglan.sdk.server.common.JsonMapper;

public class DianxinSession extends SessionBase {
    private String code;			//授权码
    private String accessToken;    //访问令牌

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return JsonMapper.toJson(this);
    }
}
