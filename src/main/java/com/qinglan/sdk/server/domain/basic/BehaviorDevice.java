package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.qinglan.sdk.server.common.JsonMapper;
import lombok.ToString;

@ToString
public class BehaviorDevice implements Serializable {

    private Long id;

    private String device;

    private Long gameId;

    private Integer clientType;

    private String channels;

    private String loginZones;

    private String roleZones;
    
    private String loginChannels;
    
    private Set<Integer> channelIds;
    
    private Set<String> loginZoneIds;
    
    private Set<String> roleZoneIds;
    
    private Set<Integer> loginChannelIds;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device == null ? null : device.trim();
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels == null ? null : channels.trim();
    }

    public String getLoginZones() {
        return loginZones;
    }

    public void setLoginZones(String loginZones) {
        this.loginZones = loginZones == null ? null : loginZones.trim();
    }

    public String getRoleZones() {
        return roleZones;
    }

    public void setRoleZones(String roleZones) {
        this.roleZones = roleZones == null ? null : roleZones.trim();
    }

	public Set<Integer> getChannelIds() {
		return channelIds;
	}

	public Set<String> getLoginZoneIds() {
		return loginZoneIds;
	}

	public Set<String> getRoleZoneIds() {
		return roleZoneIds;
	}
	
    
	public String getLoginChannels() {
		return loginChannels;
	}

	public void setLoginChannels(String loginChannels) {
		this.loginChannels = loginChannels;
	}

	public Set<Integer> getLoginChannelIds() {
		return loginChannelIds;
	}

	@SuppressWarnings("unchecked")
	public void rebuildAttribute() {
		channelIds = JsonMapper.toObject(channels, Set.class);
		loginZoneIds = JsonMapper.toObject(loginZones, Set.class);
		roleZoneIds = JsonMapper.toObject(roleZones, Set.class);
		loginChannelIds = JsonMapper.toObject(loginChannels, Set.class);
	}
	
	public void jsonAttribute() {
		if(null == channelIds) channelIds = new HashSet<Integer>();
		if(null == loginChannelIds) loginChannelIds = new HashSet<Integer>();
		if(null == loginZoneIds) loginZoneIds = new HashSet<String>();
		if(null == roleZoneIds) roleZoneIds = new HashSet<String>();
		channels = JsonMapper.toJson(channelIds);
		loginChannels = JsonMapper.toJson(loginChannelIds);
		loginZones = JsonMapper.toJson(loginZoneIds);
		roleZones = JsonMapper.toJson(roleZoneIds);
	}
	
	public void addChannelId(Integer channelId) {
		if(null == channelIds) channelIds = new HashSet<Integer>();
		channelIds.add(channelId);
	}
	
	public void addLoginChannelId(Integer channelId) {
		if(null == loginChannelIds) loginChannelIds = new HashSet<Integer>();
		loginChannelIds.add(channelId);
	}
	
	public void addLoginZoneId(String zoneId) {
		if(null == loginZoneIds) loginZoneIds = new HashSet<String>();
		loginZoneIds.add(zoneId);
	}
	
	public void addRoleZoneId(String zoneId) {
		if(null == roleZoneIds) roleZoneIds = new HashSet<String>();
		roleZoneIds.add(zoneId);
	}
	
}