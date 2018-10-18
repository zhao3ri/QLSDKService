package com.qinglan.sdk.server.release.presentation.basic.dto;

import java.io.Serializable;
import java.util.Date;


import com.qinglan.sdk.server.common.StringUtil;

import lombok.ToString;

@ToString(callSuper = true)
public class RoleEstablishPattern extends BaseDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String uid;
	private String zoneId;
	private String zoneName;
	private String roleId;
	private String roleName;
	private String roleLevel;
	private String deviceId;
	private Integer clientType;
	private Date creatTime ;

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
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
		if(null == getAppId()) return true;
		if(null == getPlatformId()) return true;
		if(null == getUid() || getUid().trim().isEmpty()) return true;
		if(null == getZoneId() || getZoneId().trim().isEmpty()) return true;
		if(null == getRoleId() || getRoleId().trim().isEmpty()) return true;
		if(null == getRoleName() || getRoleName().trim().isEmpty()) return true;
		if(null == getDeviceId() || getDeviceId().trim().isEmpty()) return true;
		if(null == getClientType()) return true;
		return false;
	}
}
