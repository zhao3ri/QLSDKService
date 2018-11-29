package com.qinglan.sdk.server.domain.basic;

import com.qinglan.sdk.server.common.DateUtils;

import java.io.Serializable;

public class BehaviorUserRedis implements Serializable {
	private static final long serialVersionUID = 1L;
	private String uid;
    private Integer platformId;
    private Long gameId;
	private Integer clientType;
	private String zoneId;
	private String data;
	private String roleData;
	
	private Long firstInTime;
	private Long lastLoginTime;
	private Integer loginTimesToday;
	private Long lastHeartTime;
	private Long lastLogoutTime;
	private Long firstRoleTime;
	private Long firstPayTime;
	private Long lastPayTime;
	private Integer payTimesToday;
	private Long loginRecord;
	

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
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

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Long getFirstInTime() {
		return firstInTime;
	}

	public void setFirstInTime(Long firstInTime) {
		this.firstInTime = firstInTime;
	}

	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Integer getLoginTimesToday() {
		return loginTimesToday;
	}

	public void setLoginTimesToday(Integer loginTimesToday) {
		this.loginTimesToday = loginTimesToday;
	}

	public Long getLastHeartTime() {
		return lastHeartTime;
	}

	public void setLastHeartTime(Long lastHeartTime) {
		this.lastHeartTime = lastHeartTime;
	}

	public Long getLastLogoutTime() {
		return lastLogoutTime;
	}

	public void setLastLogoutTime(Long lastLogoutTime) {
		this.lastLogoutTime = lastLogoutTime;
	}

	public Long getFirstRoleTime() {
		return firstRoleTime;
	}

	public void setFirstRoleTime(Long firstRoleTime) {
		this.firstRoleTime = firstRoleTime;
	}

	public Long getFirstPayTime() {
		return firstPayTime;
	}

	public void setFirstPayTime(Long firstPayTime) {
		this.firstPayTime = firstPayTime;
	}

	public Long getLastPayTime() {
		return lastPayTime;
	}

	public void setLastPayTime(Long lastPayTime) {
		this.lastPayTime = lastPayTime;
	}

	public Integer getPayTimesToday() {
		return payTimesToday;
	}

	public void setPayTimesToday(Integer payTimesToday) {
		this.payTimesToday = payTimesToday;
	}

	public Long getLoginRecord() {
		return loginRecord;
	}

	public void setLoginRecord(Long loginRecord) {
		this.loginRecord = loginRecord;
	}
	
	public String getRoleData() {
		return roleData;
	}

	public void setRoleData(String roleData) {
		this.roleData = roleData;
	}

	public Long late35Login(){
		if (loginRecord == null || loginRecord == 0) {
			return 0L;
		}
		Integer loginDel = DateUtils.getIntervalDays(lastLoginTime, System.currentTimeMillis());
		if (loginDel > 0) {
			String record = Long.toBinaryString(loginRecord);
			if (record.length() > 34) {
				record = record.substring(record.length() - 34);
			}
			return (Long.parseLong(record, 2) << loginDel);
		}
		return loginRecord;
	}
}