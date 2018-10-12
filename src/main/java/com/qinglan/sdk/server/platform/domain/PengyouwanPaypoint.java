package com.qinglan.sdk.server.platform.domain;

import java.io.Serializable;

import com.qinglan.sdk.server.common.JsonMapper;

public class PengyouwanPaypoint implements Serializable{
	
	private static final long serialVersionUID = 1717907601911885908L;
	
	private String amount;
	private String code;
	private String name;
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
