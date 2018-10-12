package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.platform.domain.dto.BaseSession;

/**
 * Created by hoog on 2017/4/28.
 */
public class ZhizhuyouSession extends BaseSession {

    private String ygAppId;
    private String platformId;
    private String zzyAppId;
    private String sessionid;

    @Override
    public String getYgAppId() {
        return ygAppId;
    }

    @Override
    public void setYgAppId(String ygAppId) {
        this.ygAppId = ygAppId;
    }

    @Override
    public String getPlatformId() {
        return platformId;
    }

    @Override
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getZzyAppId() {
        return zzyAppId;
    }

    public void setZzyAppId(String zzyAppId) {
        this.zzyAppId = zzyAppId;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
}
