package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.domain.basic.GameTrace;
import com.qinglan.sdk.server.domain.basic.RoleTrace;
import com.qinglan.sdk.server.domain.basic.ZoneTrace;
import com.qinglan.sdk.server.dto.GameStartPattern;

import java.util.Date;
import java.util.Map;

class GameStartStatsLog extends BaseStatsLog<GameStartPattern> {
    private boolean isNewUser;
    private boolean isGameActiveDevice;
    private boolean isChannelActiveDevice;
    private boolean isZoneActiveDevice;

    private int isGameBackUser;
    private int isGameBackPayUser;
    private int isZoneBackUser;
    private int isZoneBackPayUser;
    private int isGameFirstLoginToday;
    private int isZoneFirstLoginToday;

    private Map<Integer, Integer> gameUserKeep;
    private Map<Integer, Integer> zoneUserKeep;

    private String gameLate7LoginRecord;
    private String zoneLate7LoginRecord;
    private String roleName;
    private String firstRoleTime;
    private int roleLoginTodayTotal;
    private long roleLoginRecord;
    private int isFirstRoleLogin;
    private int isFirstRoleLoginMonth;

    static final String EXTRA_GAME_TRACE = "gameTrace";
    static final String EXTRA_ZONE_TRACE = "zoneTrace";
    static final String EXTRA_ROLE_TRACE = "roleTrace";
    static final String EXTRA_NEW_USER = "isNewUser";
    static final String EXTRA_GAME_ACTIVE_DEVICE = "isGameActiveDevice";
    static final String EXTRA_CHANNEL_ACTIVE_DEVICE = "isChannelActiveDevice";
    static final String EXTRA_ZONE_ACTIVE_DEVICE = "isZoneActiveDevice";

    public GameStartStatsLog(int code, int version, GameStartPattern dto) {
        super(code, version, dto);
    }

    @Override
    public void setEntity(GameStartPattern dto) {
        if (dto == null) {
            return;
        }
        this.os = dto.getClientType();
        this.zoneId = dto.getZoneId();
        this.roleId = dto.getRoleId();
        this.uid = dto.getUid();
        this.deviceId = dto.getDeviceId();
        this.roleName = dto.getRoleName();
    }

    @Override
    protected <E> void handleExtras(String key, E val) {
        if (key.equals(EXTRA_GAME_TRACE) && val instanceof GameTrace) {
            GameTrace gameTrace = (GameTrace) val;
            this.isGameBackUser = gameTrace.isBackUser();
            this.isGameBackPayUser = gameTrace.isBackPayUser();
            this.isGameFirstLoginToday = gameTrace.isFirstLoginToday();
            this.gameUserKeep = gameTrace.userKeep();
            this.gameLate7LoginRecord = gameTrace.late7Login();
        } else if (key.equals(EXTRA_ZONE_TRACE) && val instanceof ZoneTrace) {
            ZoneTrace zoneTrace = (ZoneTrace) val;
            this.isZoneBackUser = zoneTrace.isBackUser();
            this.isZoneBackPayUser = zoneTrace.isBackPayUser();
            this.isZoneFirstLoginToday = zoneTrace.isFirstLoginToday();
            this.zoneUserKeep = zoneTrace.userKeep();
            this.zoneLate7LoginRecord = zoneTrace.late7Login();
        } else if (key.equals(EXTRA_ROLE_TRACE) && val instanceof RoleTrace) {
            RoleTrace roleTrace = (RoleTrace) val;
            this.firstRoleTime = DateUtils.toStringDate(new Date(roleTrace.getFirstRoleTime()));
            this.roleLoginTodayTotal = roleTrace.getLoginTimesToday();
            this.roleLoginRecord = roleTrace.getLoginRecord();
            this.isFirstRoleLogin = roleTrace.isFirstRoleLogin();
            this.isFirstRoleLoginMonth = roleTrace.isFirstLoginMonth();
        } else if (key.equals(EXTRA_NEW_USER) && val instanceof Boolean) {
            this.isNewUser = (Boolean) val;
        } else if (key.equals(EXTRA_GAME_ACTIVE_DEVICE) && val instanceof Boolean) {
            this.isGameActiveDevice = (Boolean) val;
        } else if (key.equals(EXTRA_CHANNEL_ACTIVE_DEVICE) && val instanceof Boolean) {
            this.isChannelActiveDevice = (Boolean) val;
        } else if (key.equals(EXTRA_ZONE_ACTIVE_DEVICE) && val instanceof Boolean) {
            this.isZoneActiveDevice = (Boolean) val;
        }
    }

    @Override
    protected void appendLog(StringBuffer buffer) {
        buffer.append(zoneId).append(SEPARATOR);
        buffer.append(roleId).append(SEPARATOR);
        buffer.append(uid).append(SEPARATOR);
        buffer.append(deviceId).append(SEPARATOR);
        buffer.append(boolean2Int(isNewUser)).append(SEPARATOR);
        buffer.append(boolean2Int(isGameActiveDevice)).append(SEPARATOR);
        buffer.append(boolean2Int(isChannelActiveDevice)).append(SEPARATOR);
        buffer.append(boolean2Int(isZoneActiveDevice)).append(SEPARATOR);
        buffer.append(isGameBackUser).append(SEPARATOR).append(isGameBackPayUser).append(SEPARATOR);
        buffer.append(isZoneBackUser).append(SEPARATOR).append(isZoneBackPayUser).append(SEPARATOR);
        buffer.append(isGameFirstLoginToday).append(SEPARATOR);
        buffer.append(isZoneFirstLoginToday).append(SEPARATOR);
        appendUserKeep(buffer);
        buffer.append(gameLate7LoginRecord).append(SEPARATOR);
        buffer.append(zoneLate7LoginRecord).append(SEPARATOR);
        buffer.append(roleName).append(SEPARATOR);
        buffer.append(firstRoleTime).append(SEPARATOR);
        buffer.append(roleLoginTodayTotal).append(SEPARATOR);
        buffer.append(roleLoginRecord).append(SEPARATOR);
        buffer.append(isFirstRoleLogin).append(SEPARATOR);
        buffer.append(isFirstRoleLoginMonth);
    }

    private void appendUserKeep(StringBuffer buffer) {
        buffer.append(gameUserKeep.get(2)).append(SEPARATOR).append(gameUserKeep.get(3)).append(SEPARATOR);
        buffer.append(gameUserKeep.get(4)).append(SEPARATOR).append(gameUserKeep.get(5)).append(SEPARATOR);
        buffer.append(gameUserKeep.get(6)).append(SEPARATOR).append(gameUserKeep.get(7)).append(SEPARATOR);
        buffer.append(gameUserKeep.get(14)).append(SEPARATOR).append(gameUserKeep.get(30)).append(SEPARATOR);

        buffer.append(zoneUserKeep.get(2)).append(SEPARATOR).append(zoneUserKeep.get(3)).append(SEPARATOR);
        buffer.append(zoneUserKeep.get(4)).append(SEPARATOR).append(zoneUserKeep.get(5)).append(SEPARATOR);
        buffer.append(zoneUserKeep.get(6)).append(SEPARATOR).append(zoneUserKeep.get(7)).append(SEPARATOR);
        buffer.append(zoneUserKeep.get(14)).append(SEPARATOR).append(zoneUserKeep.get(30)).append(SEPARATOR);
    }
}
