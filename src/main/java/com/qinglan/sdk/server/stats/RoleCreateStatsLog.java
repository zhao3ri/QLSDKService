package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.dto.RoleCreatePattern;

class RoleCreateStatsLog extends BaseStatsLog<RoleCreatePattern> {
    private String zoneName;
    private String roleName;
    private String roleLevel;
    private int isGameFirstRole;
    private int isZoneFirstRole;
    private boolean isDeviceGameFirstCreateRole;
    private boolean isDeviceZoneFirstCreateRole;

    static final String EXTRA_GAME_FIRST_ROLE = "isGameFirstRole";
    static final String EXTRA_ZONE_FIRST_ROLE = "isZoneFirstRole";
    static final String EXTRA_DEVICE_GAME_CREATE_ROLE = "isDeviceGameFirstCreateRole";
    static final String EXTRA_DEVICE_ZONE_CREATE_ROLE = "isDeviceZoneFirstCreateRole";

    public RoleCreateStatsLog(int code, int version, RoleCreatePattern dto) {
        super(code, version, dto);
    }

    @Override
    public void setEntity(RoleCreatePattern dto) {
        if (dto == null) {
            return;
        }
        this.os = dto.getClientType();
        this.zoneId = dto.getZoneId();
        this.roleId = dto.getRoleId();
        this.uid = dto.getUid();
        this.deviceId = dto.getDeviceId();
        this.zoneName = dto.getZoneName();
        this.roleName = dto.getRoleName();
        this.roleLevel = dto.getRoleLevel();
    }

    @Override
    protected <E> void handleExtras(String key, E val) {
        if (key.equals(EXTRA_GAME_FIRST_ROLE) && val instanceof Integer) {
            this.isGameFirstRole = (Integer) val;
        } else if (key.equals(EXTRA_ZONE_FIRST_ROLE) && val instanceof Integer) {
            this.isZoneFirstRole = (Integer) val;
        } else if (key.equals(EXTRA_DEVICE_GAME_CREATE_ROLE) && val instanceof Boolean) {
            this.isDeviceGameFirstCreateRole = (Boolean) val;
        } else if (key.equals(EXTRA_DEVICE_ZONE_CREATE_ROLE) && val instanceof Boolean) {
            this.isDeviceZoneFirstCreateRole = (Boolean) val;
        }
    }

    @Override
    protected void appendLog(StringBuffer buffer) {
        buffer.append(zoneId).append(SEPARATOR);
        buffer.append(roleId).append(SEPARATOR);
        buffer.append(uid).append(SEPARATOR);
        buffer.append(deviceId).append(SEPARATOR);
        buffer.append(zoneName).append(SEPARATOR);
        buffer.append(roleName).append(SEPARATOR);
        buffer.append(roleLevel).append(SEPARATOR);
        buffer.append(isGameFirstRole).append(SEPARATOR);
        buffer.append(isZoneFirstRole).append(SEPARATOR);
        buffer.append(boolean2Int(isDeviceGameFirstCreateRole)).append(SEPARATOR);
        buffer.append(boolean2Int(isDeviceZoneFirstCreateRole));
    }
}
