package com.qinglan.sdk.server.platform.domain.dto;

/**
 * Created by engine on 2017/1/6.
 */
public class QitianlediSession extends BaseSession {
    private String uid;
    private String sessionId;
    private String ygAppId;
    private String platformId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getYgAppId() {
        return ygAppId;
    }

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
}
