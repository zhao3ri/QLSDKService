package com.qinglan.sdk.server.application.basic;

import com.qinglan.sdk.server.domain.basic.Game;
import com.qinglan.sdk.server.domain.basic.Order;

public interface GameService {
	
	Game getGameById(long id);
	
	boolean notifyGame(Order order, Game game) throws Exception;
	
}
