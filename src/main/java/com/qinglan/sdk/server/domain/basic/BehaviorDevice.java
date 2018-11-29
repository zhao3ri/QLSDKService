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

    private String platforms;

    private String loginZones;

    private String roleZones;
    
    private String loginPlatforms;
    
    private Set<Integer> platformIds;
    
    private Set<String> loginZoneIds;
    
    private Set<String> roleZoneIds;
    
    private Set<Integer> loginPlatformIds;

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

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms == null ? null : platforms.trim();
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

	public Set<Integer> getPlatformIds() {
		return platformIds;
	}

	public Set<String> getLoginZoneIds() {
		return loginZoneIds;
	}

	public Set<String> getRoleZoneIds() {
		return roleZoneIds;
	}
	
    
	public String getLoginPlatforms() {
		return loginPlatforms;
	}

	public void setLoginPlatforms(String loginPlatforms) {
		this.loginPlatforms = loginPlatforms;
	}

	public Set<Integer> getLoginPlatformIds() {
		return loginPlatformIds;
	}

	@SuppressWarnings("unchecked")
	public void rebuildAttribute() {
		platformIds = JsonMapper.toObject(platforms, Set.class);
		loginZoneIds = JsonMapper.toObject(loginZones, Set.class);
		roleZoneIds = JsonMapper.toObject(roleZones, Set.class);
		loginPlatformIds = JsonMapper.toObject(loginPlatforms, Set.class);
	}
	
	public void jsonAttribute() {
		if(null == platformIds) platformIds = new HashSet<Integer>();
		if(null == loginPlatformIds) loginPlatformIds = new HashSet<Integer>();
		if(null == loginZoneIds) loginZoneIds = new HashSet<String>();
		if(null == roleZoneIds) roleZoneIds = new HashSet<String>();
		platforms = JsonMapper.toJson(platformIds);
		loginPlatforms = JsonMapper.toJson(loginPlatformIds);
		loginZones = JsonMapper.toJson(loginZoneIds);
		roleZones = JsonMapper.toJson(roleZoneIds);
	}
	
	public void addPlatformId(Integer platformId) {
		if(null == platformIds) platformIds = new HashSet<Integer>();
		platformIds.add(platformId);
	}
	
	public void addLoginPlatformId(Integer platformId) {
		if(null == loginPlatformIds) loginPlatformIds = new HashSet<Integer>();
		loginPlatformIds.add(platformId);
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