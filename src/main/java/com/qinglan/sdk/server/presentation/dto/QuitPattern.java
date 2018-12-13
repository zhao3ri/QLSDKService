package com.qinglan.sdk.server.presentation.dto;

import java.io.Serializable;

import lombok.ToString;

@ToString(callSuper = true)
public class QuitPattern extends BaseDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String uid;
	private String zoneId;
	private String roleId;
	private Integer clientType;
	private String deviceId;

	public String getUid() {
		if(null == uid) return uid;
		if(uid.trim().length() > 5 && uid.trim().charAt(4) == '_') {
			return uid.substring(5, uid.length());
		}
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
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
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public boolean isEmpty() {
		if(null == getGameId()) return true;
		if(null == getChannelId()) return true;
		if(null == getUid() || getUid().trim().isEmpty()) return true;
		if(null == getZoneId() || getZoneId().trim().isEmpty()) return true;
		if(null == getRoleId() || getRoleId().trim().isEmpty()) return true;
		if(null == getDeviceId() || getDeviceId().trim().isEmpty()) return true;
		if(null == getClientType()) return true;
		return false;
	}
	
}
