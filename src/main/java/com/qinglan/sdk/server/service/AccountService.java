package com.qinglan.sdk.server.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.qinglan.sdk.server.domain.PlatformGame;
import com.qinglan.sdk.server.domain.dto.*;

public interface AccountService {
	
	Map<String, Object> initial(InitialPattern params);
	
	Map<String, Object> login(LoginPattern params);
	
	Map<String, Object> heartbeat(HeartbeatPattern params);
	
	Map<String, Object> logout(LogoutPattern params);
	
	Map<String, Object> quit(QuitPattern params);
	
	Map<String, Object> roleEstablish(RoleEstablishPattern params);
	
	Map<String,Object> orderGenerate(OrderGeneratePattern params);

	Map<String,Object> loginSuccess(LoginSuccessPattern params);
	Map<String,Object> getUserIdByToken(GetUserInfoPattern params);
	Map<String,Object> validateSession(ValidateSessionPattern params);

	Map<String,Object> selforderGenerate(OrderGeneratePattern params);

	public String payNotify(HttpServletRequest request);

	String orderSuccessNotify(HttpServletRequest request);
	boolean checkPlatformBalance(int money, PlatformGame platformGame);
}
