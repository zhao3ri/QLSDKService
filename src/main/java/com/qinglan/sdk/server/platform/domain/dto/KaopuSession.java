package com.qinglan.sdk.server.platform.domain.dto;

import com.qinglan.sdk.server.common.JsonMapper;

public class KaopuSession {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String devicetype;
	private String imei;
	private String sign;
	private String r;
	private String tagid;
	private String tag;
	private String appid;
	private String channelkey;
	private String openid;
	private String token;
	
	public String getZdappId() {
		return zdappId;
	}

	public void setZdappId(String zdappId) {
		this.zdappId = zdappId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getR() {
		return r;
	}

	public void setR(String r) {
		this.r = r;
	}

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getChannelkey() {
		return channelkey;
	}

	public void setChannelkey(String channelkey) {
		this.channelkey = channelkey;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
