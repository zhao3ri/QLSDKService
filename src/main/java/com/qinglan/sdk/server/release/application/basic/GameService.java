package com.qinglan.sdk.server.release.application.basic;

import com.qinglan.sdk.server.release.domain.basic.Game;
import com.qinglan.sdk.server.release.domain.basic.Order;

public interface GameService {
	
	Game getGameById(long id);
	
	boolean notifyGame(Order order, Game game) throws Exception;
	
}
