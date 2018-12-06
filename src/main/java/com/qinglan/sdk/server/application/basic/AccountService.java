package com.qinglan.sdk.server.application.basic;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.presentation.basic.dto.*;

public interface AccountService {

    Map<String, Object> initial(InitialPattern params);

    Map<String, Object> login(LoginPattern params);

    Map<String, Object> heartbeat(HeartbeatPattern params);

    Map<String, Object> logout(LogoutPattern params);

    Map<String, Object> quit(QuitPattern params);

    Map<String, Object> roleCreate(RoleCreatePattern params);

    Map<String, Object> orderGenerate(OrderGeneratePattern params);

    Map<String, Object> loginSuccess(LoginSuccessPattern params);

    Map<String, Object> getUserIdByToken(GetUserInfoPattern params);

    Map<String, Object> queryOrder(QueryOrderRequest request);

    Map<String, Object> validateSession(ValidateSessionPattern params);

    Map<String, Object> selforderGenerate(OrderGeneratePattern params);

    String payNotify(HttpServletRequest request);

    String orderSuccessNotify(HttpServletRequest request);

    boolean checkPlatformBalance(int money, PlatformGame platformGame);
}
