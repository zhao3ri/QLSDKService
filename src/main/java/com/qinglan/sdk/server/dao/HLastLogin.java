package com.qinglan.sdk.server.dao;

import java.util.Date;


public class HLastLogin {
	private Long id;
	private String uid;
	private Integer pid;
	private Integer clientType;
	private Long gameId;
	private String zoneId;
	private Date lastLoginDate;
	private Integer isPaidUser;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
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
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Integer getIsPaidUser() {
		return isPaidUser;
	}
	public void setIsPaidUser(Integer isPaidUser) {
		this.isPaidUser = isPaidUser;
	}
}
