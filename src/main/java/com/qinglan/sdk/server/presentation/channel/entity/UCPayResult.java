package com.qinglan.sdk.server.presentation.channel.entity;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class UCPayResult implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String ver;
	
	private String sign;
	
	private UCPayData data;

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public UCPayData getData() {
		return data;
	}

	public void setData(UCPayData data) {
		this.data = data;
	}
	
}
