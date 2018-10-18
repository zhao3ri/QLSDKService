package com.qinglan.sdk.server.release.application.basic;


import com.qinglan.sdk.server.release.presentation.basic.dto.InitialPattern;
import org.junit.Test;

import com.qinglan.sdk.server.release.BaseTestCase;

public class BehaviorDeviceServiceTest  extends BaseTestCase{

	
	@Test
	public void initialBehaviorDeviceTest(){
		InitialPattern initial=new InitialPattern();
		initial.setAppId(1L);
		initial.setPlatformId(1);
		initial.setDeviceId("923456789");
		initial.setClientType(1);
		initial.setManufacturer("1");
		initial.setModel("1");
		
		//behaviorService.initialBehaviorDevice(initial);
		
	}
	
	@Test
	public void loginBehaviorDeviceTest(){
//		LoginPattern login =new LoginPattern();
//		login.setAppId("1");
//		login.setPlatformId("2");
//		login.setDeviceId("923456789");
//		login.setClientType("1");
//		login.setRoleId("22222");
//		login.setZoneId("33333");
//		behaviorService.loginBehaviorDevice(login);
	}
	
}
