package com.qinglan.sdk.server.dto;

import java.io.Serializable;

import com.qinglan.sdk.server.common.StringUtil;

import lombok.ToString;

@ToString(callSuper = true)
public class GameStartPattern extends BaseDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String uid;
	private String zoneId;
	private String zoneName;
	private String roleId;
	private String roleName;
	private String roleLevel;
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
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		if (null == roleName) {
			return null;
		}
		return StringUtil.replaceBlank(roleName.replace("|", "#"));
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleLevel() {
		return roleLevel;
	}
	public void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
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

	@Override
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
