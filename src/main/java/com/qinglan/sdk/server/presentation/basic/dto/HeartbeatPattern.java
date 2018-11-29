package com.qinglan.sdk.server.presentation.basic.dto;

import java.io.Serializable;

import lombok.ToString;

@ToString(callSuper = true)
public class HeartbeatPattern extends BaseDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String uid;
	private String zoneId;
	private String roleId;
	private String loginTime;
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
	public String getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
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
		if(null == getLoginTime() || getLoginTime().trim().isEmpty()) return true;
		if(null == getDeviceId() || getDeviceId().trim().isEmpty()) return true;
		if(null == getClientType()) return true;
		return false;
	}
	
	public static void main(String[] args) {
		String b = null;
		System.out.println(b);
		System.out.println(bb("bbrr"));
		System.out.println(bb("1001_bb"));
		System.out.println(bb("1001_"));
		System.out.println(bb("1001_b"));
		System.out.println(bb("bbrrbbbbb"));
	}
	
	public static String bb(String uid) {
		if(null == uid) return uid;
		
		if(uid.trim().length() > 5 && uid.trim().charAt(4) == '_') {
			return uid.substring(5, uid.length());
		}
		return uid;
	}
}
