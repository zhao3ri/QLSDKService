package com.qinglan.sdk.server.service;

import com.qinglan.sdk.server.dao.Game;
import com.qinglan.sdk.server.domain.Order;

public interface GameService {
	
	Game getGameById(long id);
	
	boolean notifyGame(Order order, Game game) throws Exception;
	
}
