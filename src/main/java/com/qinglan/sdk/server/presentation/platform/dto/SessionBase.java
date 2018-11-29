package com.qinglan.sdk.server.presentation.platform.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by engine on 16/9/29.
 */
public class SessionBase {
    private String ygAppId;            //指点游戏ID
    private String platformId;        //指点联运平台ID

    public String getYgAppId() {
        return ygAppId;
    }

    public void setYgAppId(String ygAppId) {
        this.ygAppId = ygAppId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Map<String, String> verifySession() {
        Map<String, String> result = new HashMap<String, String>();
        if (getYgAppId() == null) {
            result.put("status", "2");
            result.put("msg", "[ygAppId] 为空请核对");
            return result;
        }
        if (getPlatformId() == null) {
            result.put("status", "2");
            result.put("msg", "[platformId] 为空请核对");
            return result;
        }
        return null;
    }
}
