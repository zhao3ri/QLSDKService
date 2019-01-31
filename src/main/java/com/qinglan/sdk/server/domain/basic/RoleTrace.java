package com.qinglan.sdk.server.domain.basic;

import java.util.Date;

import com.qinglan.sdk.server.common.DateUtils;

public class RoleTrace extends Trace {
    private String rid;//角色ID
    private String rname;//角色名
    private Long lastHeartTime;//最后心跳时间
    private Long payRecord;//支付记录

    /**
     * @return the rid
     */
    public String getRid() {
        return rid;
    }

    /**
     * @param rid the rid to set
     */
    public void setRid(String rid) {
        this.rid = rid;
    }

    /**
     * @return the rname
     */
    public String getRname() {
        return rname;
    }

    /**
     * @param rname the rname to set
     */
    public void setRname(String rname) {
        this.rname = rname;
    }


    /**
     * @return the lastHeartTime
     */
    public Long getLastHeartTime() {
        return lastHeartTime;
    }

    /**
     * @param lastHeartTime the lastHeartTime to set
     */
    public void setLastHeartTime(Long lastHeartTime) {
        this.lastHeartTime = lastHeartTime;
    }

    /**
     * @return the payRecord
     */
    public Long getPayRecord() {
        return payRecord;
    }

    /**
     * @param payRecord the payRecord to set
     */
    public void setPayRecord(Long payRecord) {
        this.payRecord = payRecord;
    }


    /**
     * 是否角色首次登陆游戏
     */
    public int isFirstRoleLogin() {
        if (firstInTime == null || firstInTime == 0) {
            return 1;
        }
        return 0;
    }

    public int isFirstLoginMonth() {
        if (firstInTime == null || firstInTime == 0 || lastLoginTime == null || lastLoginTime == 0) {
            return 1;
        }
        if (DateUtils.sameMonth(lastLoginTime, new Date().getTime())) {
            return 0;
        }
        return 1;
    }

    //最近35天支付情况
    public Long late35Pay() {
        if (payRecord == null) {
            return 1L;
        }
        Integer loginDel = DateUtils.getIntervalDays(lastPayTime, System.currentTimeMillis());
        if (loginDel > 0) {
            return updateRecord(payRecord, loginDel);
        }
        return payRecord;
    }

}
