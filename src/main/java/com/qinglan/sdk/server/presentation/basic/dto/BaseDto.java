package com.qinglan.sdk.server.presentation.basic.dto;

import lombok.ToString;

@ToString
public abstract class BaseDto {
	
	private Long gameId;
	private Integer platformId;

	public abstract boolean isEmpty();

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}
}
