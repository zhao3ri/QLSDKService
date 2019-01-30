package com.qinglan.sdk.server.domain.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.JsonMapper;

public class ZoneTrace extends Trace {
    private Long lastHeartTime;//最后心跳时间

    public Long getLastHeartTime() {
        return lastHeartTime;
    }

    public void setLastHeartTime(Long lastHeartTime) {
        this.lastHeartTime = lastHeartTime;
    }

    //是否该游戏当月第一次充值
    public int isFirstPayMonth() {
        if (lastPayTime == null || lastPayTime == 0) {
            return 1;
        }
        if (DateUtils.sameMonth(lastPayTime, new Date().getTime())) {
            return 0;
        }
        return 1;
    }

}
