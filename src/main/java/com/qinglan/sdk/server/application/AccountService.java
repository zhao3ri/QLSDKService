package com.qinglan.sdk.server.application;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import com.qinglan.sdk.server.dto.*;

import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;

public interface AccountService {

    Map<String, Object> initial(InitialPattern params);

    Map<String, Object> join(GameStartPattern params);

    Map<String, Object> heartbeat(HeartbeatPattern params);

    Map<String, Object> logout(LogoutPattern params);

    Map<String, Object> quit(QuitPattern params);

    Map<String, Object> roleCreate(RoleCreatePattern params);

    Map<String, Object> orderGenerate(OrderGenerateRequest params);

    Map<String, Object> getToken(TokenPattern params);

    Map<String, Object> getUserIdByToken(GetUserInfoPattern params);

    Map<String, Object> queryOrder(QueryOrderRequest request);

    Map<String, Object> validateSession(ValidateSessionPattern params);

    Map<String, Object> selforderGenerate(OrderGenerateRequest params);

    String payNotify(HttpServletRequest request);

    String orderSuccessNotify(HttpServletRequest request);

    boolean checkChannelBalance(int money, ChannelGameEntity channelGame);
}
