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
//		GameStartPattern join =new GameStartPattern();
//		join.setAppID("1");
//		join.setChannelId("2");
//		join.setDeviceId("923456789");
//		join.setClientType("1");
//		join.setRoleId("22222");
//		join.setZoneId("33333");
//		behaviorService.loginBehaviorDevice(join);
	}
	
}
