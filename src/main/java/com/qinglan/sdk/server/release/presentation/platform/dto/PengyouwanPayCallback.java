package com.qinglan.sdk.server.release.presentation.platform.dto;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

public class PengyouwanPayCallback implements Serializable{

	private static final long serialVersionUID = 7769425506050193794L;
	
	private String ver;
	private String tid;
	private String sign;
	private String gamekey;
	private String channel;
	private String cp_orderid;
	private String ch_orderid;
	private String amount;
	private String cp_param;
	
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getGamekey() {
		return gamekey;
	}
	public void setGamekey(String gamekey) {
		this.gamekey = gamekey;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getCp_orderid() {
		return cp_orderid;
	}
	public void setCp_orderid(String cp_orderid) {
		this.cp_orderid = cp_orderid;
	}
	public String getCh_orderid() {
		return ch_orderid;
	}
	public void setCh_orderid(String ch_orderid) {
		this.ch_orderid = ch_orderid;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCp_param() {
		return cp_param;
	}
	public void setCp_param(String cp_param) {
		this.cp_param = cp_param;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
