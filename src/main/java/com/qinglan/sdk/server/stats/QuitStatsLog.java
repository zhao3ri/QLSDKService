package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.dto.QuitPattern;

class QuitStatsLog extends BaseStatsLog<QuitPattern> {

    private String roleName;
    private String lastLoginTime;
    private String lastHeartTime;

    static final String EXTRA_LAST_LOGIN_TIME = "lastLoginTime";
    static final String EXTRA_LAST_HEARTBEAT_TIME = "lastHeartTime";
    static final String EXTRA_ROLE_NAME = "roleName";

    public QuitStatsLog(int code, int version, QuitPattern dto) {
        super(code, version, dto);
    }

    @Override
    public void setEntity(QuitPattern dto) {
        if (dto == null) {
            return;
        }
        this.zoneId = dto.getZoneId();
        this.roleId = dto.getRoleId();
        this.os = dto.getClientType();
        this.uid = dto.getUid();
        this.deviceId = dto.getDeviceId();
    }

    @Override
    protected <E> void handleExtras(String key, E val) {
        if (key.equals(EXTRA_LAST_HEARTBEAT_TIME) && val instanceof String) {
            this.lastHeartTime = (String) val;
        } else if (key.equals(EXTRA_LAST_LOGIN_TIME) && val instanceof String) {
            this.lastLoginTime = (String) val;
        } else if (key.equals(EXTRA_ROLE_NAME) && val instanceof String) {
            this.roleName = (String) val;
        }
    }

    @Override
    protected void appendLog(StringBuffer buffer) {
        buffer.append(zoneId).append(SEPARATOR);
        buffer.append(roleId).append(SEPARATOR);
        buffer.append(uid).append(SEPARATOR);
        buffer.append(deviceId).append(SEPARATOR);
        buffer.append(lastLoginTime).append(SEPARATOR);
        buffer.append(lastHeartTime).append(SEPARATOR);
        buffer.append(roleName);
    }
}
