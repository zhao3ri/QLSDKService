package com.qinglan.sdk.server.presentation.basic.dto;

import com.qinglan.sdk.server.common.StringUtil;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
public class LoginSuccessPattern extends BaseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String extend ;


	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	@Override
	public boolean isEmpty() {
		if (StringUtil.isNullOrEmpty(extend)){
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
