package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.dto.BaseDto;

import java.util.Date;

abstract class BaseStatsLog<T extends BaseDto> {
    protected static final String SEPARATOR = "|";

    private int code;
    private int version;
    private String date;
    protected long gameId;
    protected int channelId;
    protected int os;//手机操作系统，1:Android 2:ios
    protected String zoneId;
    protected String roleId;
    protected String uid;
    protected String deviceId;

    public BaseStatsLog(int code, int version, T dto) {
        this.code = code;
        this.version = version;
        this.date = DateUtils.toStringDate(new Date());
        this.gameId = dto.getGameId();
        this.channelId = dto.getChannelId();
        setEntity(dto);
    }

    public void setEntity(T dto) {

    }

    public final <E> BaseStatsLog setExtras(String key, E val) {
        handleExtras(key, val);
        return this;
    }

    protected <E> void handleExtras(String key, E val) {
    }

    @Override
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(code).append(SEPARATOR);
        buffer.append(version).append(SEPARATOR);
        buffer.append(date).append(SEPARATOR);
        buffer.append(os).append(SEPARATOR);
        buffer.append(gameId).append(SEPARATOR);
        buffer.append(channelId).append(SEPARATOR);
        appendLog(buffer);
        return buffer.toString();
    }

    protected abstract void appendLog(StringBuffer buffer);

    protected int boolean2Int(boolean b) {
        return b ? 1 : 0;
    }

}
