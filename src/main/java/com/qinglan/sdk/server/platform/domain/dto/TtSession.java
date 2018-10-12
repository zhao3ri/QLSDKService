package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class TtSession extends BaseSession {
    private Long uid;
    private String sid;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return JsonMapper.toJson(this);
    }
}
