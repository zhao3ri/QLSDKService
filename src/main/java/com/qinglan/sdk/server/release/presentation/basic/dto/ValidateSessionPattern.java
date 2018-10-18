package com.qinglan.sdk.server.release.presentation.basic.dto;

import com.qinglan.sdk.server.common.StringUtil;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
public class ValidateSessionPattern extends BaseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String token ;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean isEmpty() {
		if (StringUtil.isNullOrEmpty(token)){
			return true ;
		}
		return false;
	}
}
