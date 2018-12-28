package com.qinglan.sdk.server.channel.entity;

import lombok.ToString;

@ToString
public class UCVerifyRequest extends BaseRequest {

    private String sid;
    private String appID;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

}
