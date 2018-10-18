package com.qinglan.sdk.server.release.presentation.basic.dto;

import lombok.ToString;

@ToString
public abstract class BaseDto {
	
	private Long appId;
	private Integer platformId;

	public abstract boolean isEmpty();

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}
}
