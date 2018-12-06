package com.qinglan.sdk.server.presentation.channel.entity;

public class HMSVerifyRequest extends BaseRequest{
    private String appID;
    private String cpID;
    private String ts;
    private String playerId;
    private String playerLevel;
    private String playerSSign;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getCpID() {
        return cpID;
    }

    public void setCpID(String cpID) {
        this.cpID = cpID;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(String playerLevel) {
        this.playerLevel = playerLevel;
    }

    public String getPlayerSSign() {
        return playerSSign;
    }

    public void setPlayerSSign(String playerSSign) {
        this.playerSSign = playerSSign;
    }
}
