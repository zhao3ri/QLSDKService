package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.Date;

public class Role implements Serializable {
	
	private static final long serialVersionUID = -7084291503766288863L;
	
	private Long id;
	private Integer clientType;
	private Long gameId;
	private Integer platformId;
	private String zoneId;
	private String roleId;
	private String roleName;
	private Date createTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Role() {
		
	}
	
	public Role(Integer clientType, Long gameId, Integer platformId, String zoneId, String roleId, String roleName) {
		this.clientType = clientType;
		this.gameId = gameId;
		this.platformId = platformId;
		this.zoneId = zoneId;
		this.roleId = roleId;
		this.roleName = roleName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}
	public Integer getPlatformId() {
		return platformId;
	}
	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}