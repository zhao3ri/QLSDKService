package com.qinglan.sdk.server.dto;

import lombok.ToString;

@ToString
public abstract class BaseDto {
	private Long gameId;
	private Integer channelId;

	public abstract boolean isEmpty();

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
}
