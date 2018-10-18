package com.qinglan.sdk.server.release.presentation.platform.dto.dtotwo;

import com.qinglan.sdk.server.release.presentation.platform.dto.SessionBase;

/**
 * Created by engine on 2017/1/6.
 */
public class QitianlediSession extends SessionBase {
    private String uid;
    private String session_id;
    private String ygAppId;
    private String platformId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
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
