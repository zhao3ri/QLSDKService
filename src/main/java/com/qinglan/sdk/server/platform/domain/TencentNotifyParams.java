package com.qinglan.sdk.server.platform.domain;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

public class TencentNotifyParams implements Serializable{
	private static final long serialVersionUID = 1051083036318812368L;
	
	private String appid;
	private String openid;
	private String openkey;
	private String pay_token;
	private String pf;
	private String pfkey;
	private String zoneid;
	private String session_id;
	private String session_type;
	private String orderId;
	private Integer amount;
	private String appkey;
	private String serverName;
	private Integer gameCoinRatio;
	private int pollTimes;
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getOpenkey() {
		return openkey;
	}
	public void setOpenkey(String openkey) {
		this.openkey = openkey;
	}
	public String getPay_token() {
		return pay_token;
	}
	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
	}
	public String getPf() {
		return pf;
	}
	public void setPf(String pf) {
		this.pf = pf;
	}
	public String getPfkey() {
		return pfkey;
	}
	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}
	public String getZoneid() {
		return zoneid;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getSession_type() {
		return session_type;
	}
	public void setSession_type(String session_type) {
		this.session_type = session_type;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public Integer getGameCoinRatio() {
		return gameCoinRatio;
	}
	public void setGameCoinRatio(Integer gameCoinRatio) {
		this.gameCoinRatio = gameCoinRatio;
	}
	public int getPollTimes() {
		return pollTimes;
	}
	public void setPollTimes(int pollTimes) {
		this.pollTimes = pollTimes;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
