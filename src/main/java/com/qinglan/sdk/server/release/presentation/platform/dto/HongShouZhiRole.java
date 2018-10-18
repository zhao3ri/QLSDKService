package com.qinglan.sdk.server.release.presentation.platform.dto;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by engine on 16/9/27.
 */
public class HongShouZhiRole {
    @JsonIgnore
    public String zdAppId ;
    @JsonIgnore
    public String platformId ;
    public String app_id	;//	必须游戏接入时分配的应用app_id
    public String mem_id	;//	mem_id登陆时提供给CP的用户ID
    public String server	;//	玩家所在区服  需urlencode
    public String role	;//	玩家所在区服角色  需urlencode
    public String money	;//	玩家所在服金币数量  需urlencode
    public String level	;//	玩家所在区服等级  需urlencode
    public String experience	;//	玩家所在区服经验  需urlencode
    public String user_token	;//	登陆获取的user_token
    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getZdAppId() {
        return zdAppId;
    }

    public void setZdAppId(String zdAppId) {
        this.zdAppId = zdAppId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

}

