package com.qinglan.sdk.server.domain;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.JsonMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ZoneTrace {
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
		return DateUtils.getIntervalDays(lastLoginTime, System.currentTimeMillis()) == 0 ? loginTimesToday : 0;
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
		return DateUtils.getIntervalDays(lastPayTime, System.currentTimeMillis()) == 0 ? payTimesToday : 0;
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
	//首次付费
	public int isFirstPayUser(){
		return (firstPayTime == null || firstPayTime == 0) ? 1 : 0;
	}
	
	//是否该游戏今天首次付费
	public int isFirstPayToday(){
		if (lastPayTime == null || lastPayTime == 0) {
			return 1;
		}
		if (DateUtils.getIntervalDays(lastPayTime, new Date().getTime()) == 0) {
			return 0;
		}
		return 1;
	}
	
	//是否该游戏当月第一次充值
	public int isFirstPayMonth(){
		if (lastPayTime == null || lastPayTime == 0) {
			return 1;
		}
		if (DateUtils.sameMonth(lastPayTime, new Date().getTime())) {
			return 0;
		}
		return 1;
	}
	
	//是否该游戏第一次创角
	public int isFirstRole(){
		return (firstRoleTime == null || firstRoleTime == 0) ? 1 : 0;
	}
	
	//是否该游戏回流账号
	public int isBackUser(){
		if (lastLoginTime == null || lastLoginTime == 0) {
			return 0;
		}
		return DateUtils.getIntervalDays(lastLoginTime, new Date().getTime()) > 7 ? 1 : 0;
	}
	
	//是否该游戏回流付费账号
	public int isBackPayUser(){
		if (lastLoginTime == null || lastLoginTime == 0) {
			return 0;
		}
		if (firstPayTime == null || firstPayTime == 0) {
			return 0;
		}
		return DateUtils.getIntervalDays(lastLoginTime, new Date().getTime()) > 7 ? 1 : 0;
	}
	
	//是否今天第一次登陆游戏
	public int isFirstLoginToday(){
		if (lastLoginTime == null || lastLoginTime == 0) {
			return 1;
		}
		return DateUtils.getIntervalDays(lastLoginTime, new Date().getTime()) > 0 ? 1 : 0;
	}
	
	//用户是否游戏留存
	public Map<Integer, Integer> userKeep(){
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		boolean isFirstLoginToday = isFirstLoginToday() == 1;
		Long currentTime = new Date().getTime();
		
		boolean isNextDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 1;
		boolean isThreeDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 2;
		boolean isFourDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 3;
		boolean isFiveDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 4;
		boolean isSixDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 5;
		boolean isSevenDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 6;
		boolean isFourteenDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 13;
		boolean isThirtyDayKeep = DateUtils.getIntervalDays(firstInTime, currentTime) == 29;
		
		result.put(2, (isNextDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(3, (isThreeDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(4, (isFourDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(5, (isFiveDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(6, (isSixDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(7, (isSevenDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(14, (isFourteenDayKeep && isFirstLoginToday) ? 1 : 0);
		result.put(30, (isThirtyDayKeep && isFirstLoginToday) ? 1 : 0);
		return result;
	}
	
	//最近35天登陆情况
	public Long late35Login(){
		if (loginRecord == null) {
			return 1L;
		}
		Integer loginDel = DateUtils.getIntervalDays(lastLoginTime, System.currentTimeMillis());
		if (loginDel > 0) {
			String record = Long.toBinaryString(loginRecord);
			if (record.length() > 34) {
				record = record.substring(record.length() - 34);
			}
			return (Long.parseLong(record, 2) << loginDel) + 1;
		}
		return loginRecord;
	}
	
	//最近7天登陆情况
	public String late7Login(){
		Long late35Login = late35Login();
		String record = Long.toBinaryString(late35Login);
		if (record.length() > 7) {
			return record.substring(record.length() - 7);
		}else {
			StringBuffer addBuffer = new StringBuffer();
			for (int i = 0; i < 7 - record.length(); i++) {
				addBuffer.append("0");
			}
			return addBuffer.append(record).toString();
		}
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
