package com.qinglan.sdk.server.domain.basic;

import java.util.Calendar;
import java.util.Date;

import com.qinglan.sdk.server.common.DateUtils;

public class GameTrace extends Trace{

    //是否新用户
    public int isNewUser() {
        if (firstInTime == null || firstInTime == 0) {
            return 1;
        }
        return DateUtils.getIntervalDays(firstInTime, new Date().getTime()) == 0 ? 1 : 0;
    }

    //是否本月新用户
    public int isNewMonthUser() {
        if (firstInTime == null || firstInTime == 0) {
            return 1;
        }
        return DateUtils.sameMonth(firstInTime, new Date().getTime()) ? 1 : 0;
    }

    //获取第一天登陆的时间
    public String loginFirstDay() {
        int i = loginDays();
        if (i == -1) {
            i = 0;
        } else if (i > 0) {
            i = -i;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, i);
        return DateUtils.format(calendar.getTime(), DateUtils.yyyy_MM_dd);
    }

    //获取是第几天登陆的
    private int loginDays() {
        if (lastLoginTime == null || lastLoginTime == 0) {
            return -1;
        }
        return DateUtils.getIntervalDays(firstInTime, new Date().getTime());
    }

    @Override
    public Long late35Login() {
        if (loginRecord == null) {
            return 1L;
        }
        Integer loginDel = DateUtils.getIntervalDays(lastLoginTime, System.currentTimeMillis());
        if (loginDel > 0 && (loginRecord % 2 == 0)) {
            loginRecord = loginRecord + 1;
        }
        return loginRecord;
    }

}
