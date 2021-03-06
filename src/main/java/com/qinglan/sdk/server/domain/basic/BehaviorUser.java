package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;

import com.qinglan.sdk.server.common.DateUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class BehaviorUser implements Serializable {
	private static final long serialVersionUID = 1L;
	@JsonIgnore
	private String uid;
	@JsonIgnore
    private Integer channelId;
	@JsonIgnore
    private Long gameId;
	@JsonIgnore
	private Integer clientType;
	@JsonIgnore
	private String zoneId;
	@JsonIgnore
	private String data;
	@JsonIgnore
	private String roleData;
	
	private long firstInTime;
	private long lastLoginTime;
	private int loginTimesToday;
	private long lastHeartTime;
	private long lastLogoutTime;
	private long firstRoleTime;
	private long firstPayTime;
	private long lastPayTime;
	private int payTimesToday;
	private long loginRecord;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
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
		if (loginRecord == 0) {
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