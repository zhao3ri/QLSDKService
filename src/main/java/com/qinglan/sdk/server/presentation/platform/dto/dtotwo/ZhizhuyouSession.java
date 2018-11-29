package com.qinglan.sdk.server.presentation.platform.dto.dtotwo;

import com.qinglan.sdk.server.presentation.platform.dto.SessionBase;

/**
 * Created by hoog on 2017/4/28.
 */
public class ZhizhuyouSession extends SessionBase {

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
