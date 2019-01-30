package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.domain.basic.GameTrace;
import com.qinglan.sdk.server.domain.basic.RoleTrace;
import com.qinglan.sdk.server.domain.basic.ZoneTrace;
import com.qinglan.sdk.server.dto.OrderGenerateRequest;

import java.util.Date;

public class OrderStatsLog extends BaseStatsLog<OrderGenerateRequest> {
    private int mFlag;
    static final int FLAG_TOTAL_ORDER = 1000;
    static final int FLAG_PAY_SUCCESS = 1001;

    private String roleName;
    private int amount;
    private String orderId;
    private int distanceDay;
    private String firstDayLoginTime;

    private int isNewUser;
    private int isNewMonthUser;
    private int isFirstGamePayUser;
    private int isFirstZonePayUser;
    private int isFirstGamePayToday;
    private int isFirstZonePayToday;
    private int isFirstGamePayMonth;
    private int isFirstZonePayMonth;
    private int totalPayCountToday;
    private String firstPayTime;
    private long rolePayDaysRecord;

    static final String EXTRA_GAME_TRACE = "gameTrace";
    static final String EXTRA_ZONE_TRACE = "zoneTrace";
    static final String EXTRA_ROLE_TRACE = "roleTrace";
    static final String EXTRA_DISTANCE_DAY = "distanceDay";
    static final String EXTRA_FIRST_DAY_LOGIN_TIME = "firstDayLoginTime";

    /**
     * @param flag log格式的标记
     * @see #FLAG_TOTAL_ORDER
     * @see #FLAG_PAY_SUCCESS
     */
    public OrderStatsLog(int code, int version, OrderGenerateRequest dto, int flag) {
        super(code, version, dto);
        this.mFlag = flag;
    }

    @Override
    public void setEntity(OrderGenerateRequest dto) {
        if (dto == null) {
            return;
        }
        this.os = dto.getClientType();
        this.zoneId = dto.getZoneId();
        this.roleId = dto.getRoleId();
        this.uid = dto.getUid();
        this.deviceId = dto.getDeviceId();
        this.roleName = dto.getRoleName();
        this.amount = dto.getAmount();
        this.orderId = dto.getOrderId();
    }

    @Override
    protected <E> void handleExtras(String key, E val) {
        if (key.equals(EXTRA_GAME_TRACE) && val instanceof GameTrace) {
            GameTrace gameTrace = (GameTrace) val;
            this.isNewUser = gameTrace.isNewUser();
            this.isNewMonthUser = gameTrace.isNewMonthUser();
            this.isFirstGamePayUser = gameTrace.isFirstPayUser();
            this.isFirstGamePayToday = gameTrace.isFirstPayToday();
            this.isFirstGamePayMonth = gameTrace.isFirstPayMonth();
        } else if (key.equals(EXTRA_ZONE_TRACE) && val instanceof ZoneTrace) {
            ZoneTrace zoneTrace = (ZoneTrace) val;
            this.isFirstZonePayUser = zoneTrace.isFirstPayUser();
            this.isFirstZonePayToday = zoneTrace.isFirstPayToday();
            this.isFirstZonePayMonth = zoneTrace.isFirstPayMonth();
        } else if (key.equals(EXTRA_ROLE_TRACE) && val instanceof RoleTrace) {
            RoleTrace roleTrace = (RoleTrace) val;
            this.totalPayCountToday = roleTrace.getPayTimesToday();
            this.firstPayTime = DateUtils.toStringDate(new Date(roleTrace.getFirstPayTime()));
            this.rolePayDaysRecord = roleTrace.getPay35DaysRecord();
        } else if (key.equals(EXTRA_DISTANCE_DAY) && val instanceof Integer) {
            this.distanceDay = (int) val;
        } else if (key.equals(EXTRA_FIRST_DAY_LOGIN_TIME) && val instanceof String) {
            this.firstDayLoginTime = (String) val;
        }

    }

    @Override
    protected void appendLog(StringBuffer buffer) {
        buffer.append(zoneId).append(SEPARATOR);
        buffer.append(roleId).append(SEPARATOR);
        buffer.append(uid).append(SEPARATOR);
        buffer.append(deviceId).append(SEPARATOR);
        buffer.append(amount).append(SEPARATOR);
        switch (mFlag) {
            case FLAG_TOTAL_ORDER:
                appendTotalLog(buffer);
                break;
            case FLAG_PAY_SUCCESS:
                appendSuccessLog(buffer);
                break;
        }
    }

    private void appendTotalLog(StringBuffer buffer) {
        buffer.append(distanceDay).append(SEPARATOR).append(firstDayLoginTime);
    }

    private void appendSuccessLog(StringBuffer buffer) {
        buffer.append(isNewUser).append(SEPARATOR);
        buffer.append(isFirstGamePayUser).append(SEPARATOR);
        buffer.append(isFirstZonePayUser).append(SEPARATOR);
        buffer.append(isFirstGamePayToday).append(SEPARATOR);
        buffer.append(isFirstZonePayToday).append(SEPARATOR);
        buffer.append(isFirstGamePayMonth).append(SEPARATOR);
        buffer.append(isFirstZonePayMonth).append(SEPARATOR);
        buffer.append(orderId).append(SEPARATOR);
        buffer.append(roleName).append(SEPARATOR);
        buffer.append(totalPayCountToday).append(SEPARATOR);
        buffer.append(DateUtils.toStringDate(new Date(firstPayTime))).append(SEPARATOR);
        buffer.append(rolePayDaysRecord).append(SEPARATOR);
        buffer.append(isNewMonthUser);
    }


}
