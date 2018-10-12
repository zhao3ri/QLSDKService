package com.qinglan.sdk.server.dao;

import com.qinglan.sdk.server.common.DateUtils;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    protected Long loginRecord;
    protected Long lastLoginTime;

    public Long getLoginRecord() {
        return loginRecord;
    }

    public void setLoginRecord(Long loginRecord) {
        this.loginRecord = loginRecord;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * 最近7天登陆情况
     */
    public Long late35Login() {
        if (loginRecord == null || loginRecord == 0) {
            return 0L;
        }
        Integer loginDel = DateUtils.getIntervalDays(lastLoginTime, System.currentTimeMillis());
        if (loginDel > 0) {
            String record = Long.toBinaryString(loginRecord);
            if (record.length() > 34) {
                record = record.substring(record.length() - 34);
            }
            return (Long.parseLong(record, 2) << loginDel);
        }
        return loginRecord;
    }

    /**
     * 最近7天登陆情况
     */
    public String late7Login() {
        Long late35Login = late35Login();
        String record = Long.toBinaryString(late35Login);
        if (record.length() > 7) {
            return record.substring(record.length() - 7);
        } else {
            StringBuffer addBuffer = new StringBuffer();
            for (int i = 0; i < 7 - record.length(); i++) {
                addBuffer.append("0");
            }
            return addBuffer.append(record).toString();
        }
    }
}
