package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.dto.GameStartPattern;

@Event
public class GameStartEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GameStartPattern gameStartPattern;

	public GameStartEvent(GameStartPattern gameStartPattern) {
		this.gameStartPattern = gameStartPattern;
	}
	
	public GameStartPattern getHelper() {
		return gameStartPattern;
	}
}
