package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.Date;

import lombok.ToString;

@ToString
public class Account implements Serializable {
    private Long id;

    private Integer channelId;

    private String uid;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Account() {
    }

    public Account(Integer channelId, String uid) {
        this.channelId = channelId;
        this.uid = uid;
        this.createTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}