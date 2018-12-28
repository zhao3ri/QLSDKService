package com.qinglan.sdk.server.application;


import com.qinglan.sdk.server.dto.InitialPattern;
import org.junit.Test;

import com.qinglan.sdk.server.release.BaseTestCase;

public class BehaviorDeviceServiceTest  extends BaseTestCase{

	
	@Test
	public void initialBehaviorDeviceTest(){
		InitialPattern initial=new InitialPattern();
		initial.setGameId(1L);
		initial.setChannelId(1);
		initial.setDeviceId("923456789");
		initial.setClientType(1);
		initial.setManufacturer("1");
		initial.setModel("1");
		
		//behaviorService.initialBehaviorDevice(initial);
		
	}
	
	@Test
	public void loginBehaviorDeviceTest(){
//		LoginPattern login =new LoginPattern();
//		login.setAppID("1");
//		login.setChannelId("2");
//		login.setDeviceId("923456789");
//		login.setClientType("1");
//		login.setRoleId("22222");
//		login.setZoneId("33333");
//		behaviorService.loginBehaviorDevice(login);
	}
	
}
