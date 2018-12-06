package com.qinglan.sdk.server.presentation.channel.entity;

public class BaseRequest {
    /**
     * 管理后台的游戏Id
     * */
    private long gameId;
    /**
     * 管理后台的渠道Id
     * */
    private int platformId;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }
}
