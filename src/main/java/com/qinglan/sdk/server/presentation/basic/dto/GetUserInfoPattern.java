package com.qinglan.sdk.server.presentation.basic.dto;

import com.qinglan.sdk.server.common.StringUtil;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
public class GetUserInfoPattern extends BaseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public boolean isEmpty() {
		if (StringUtil.isNullOrEmpty(sessionId)){
			return true ;
		}
		if (null == getGameId()){
			return true ;
		}
		if (null == getChannelId()){
			return true ;
		}
		return false;
	}
}
