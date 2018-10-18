package com.qinglan.sdk.server.release.domain.platform;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class UcGameCallback implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String ver;
	
	private String sign;
	
	private UcGameResponse data;

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

	public UcGameResponse getData() {
		return data;
	}

	public void setData(UcGameResponse data) {
		this.data = data;
	}
	
}
