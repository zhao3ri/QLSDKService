package com.qinglan.sdk.server.release.presentation.platform.dto.dtotwo;

import com.qinglan.sdk.server.release.presentation.platform.dto.SessionBase;

/**
 * Created by hoog on 2017/4/25.
 */
public class CangluanSession extends SessionBase {

    private String ygAppId;
    private String platformId;
    private String game_id;
    private String user_id;
    private String token;
    private String channel_id;

    public String getYgAppId() {
        return ygAppId;
    }

    public void setYgAppId(String ygAppId) {
        this.ygAppId = ygAppId;
    }

    @Override
    public String getPlatformId() {
        return platformId;
    }

    @Override
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
