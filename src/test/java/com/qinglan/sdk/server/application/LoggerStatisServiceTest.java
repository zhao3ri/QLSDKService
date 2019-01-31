package com.qinglan.sdk.server.application;

import javax.annotation.Resource;

import com.qinglan.sdk.server.dto.*;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.dto.GameStartPattern;

public class LoggerStatisServiceTest extends BaseTestCase{
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private AccountService accountService;
	@Resource
	
	@Test
	public void initialLogger() throws InterruptedException{
		InitialPattern initial=new InitialPattern();
		initial.setGameId(150212661932L);
		initial.setChannelId(1001);
		initial.setDeviceId("865267028861887");
		initial.setClientType(1);
		initial.setManufacturer("samsung");
		initial.setModel("n7100");
		initial.setApiVersion("3.1.1");
		initial.setOsVersion("android");
		initial.setLatitude("100");
		initial.setLongitude("100");
		initial.setImsi("46007929717691");
		initial.setLocation("广东");
		initial.setNetworkCountryIso("86");
		initial.setNetworkType("0");
		initial.setPhoneType("TD-LTE");
		initial.setSimOperatorName("China Mobile");
		initial.setResolution("480");
		//loggerStatisService.initialLogger(initial);
		
		Thread.sleep(10000);
	}
	
	@Test
	public void loginLogger() throws InterruptedException{
		GameStartPattern login =new GameStartPattern();
		login.setGameId(150212661932L);
		login.setUid("U19238709741111111111111111111111111");
		login.setChannelId(1001);
		login.setDeviceId("865267028861887");
		login.setClientType(1);
		login.setRoleId("1");
		login.setRoleName("test");
		login.setRoleLevel("");
		login.setZoneId("1");
		login.setZoneName("test分区");
		accountService.join(login);
		Thread.sleep(10000);
	}
	
	@Test
	public void heartbeat() throws InterruptedException {
		HeartbeatPattern heartbeat = new HeartbeatPattern();
		heartbeat.setGameId(150212661932L);
		heartbeat.setUid("zxbtest24");
		heartbeat.setChannelId(1019);
		heartbeat.setDeviceId("82776301283093");
		heartbeat.setClientType(1);
		heartbeat.setRoleId("1");
		heartbeat.setZoneId("1");
		heartbeat.setLoginTime("12345679");
		accountService.heartbeat(heartbeat);
		
		Thread.sleep(10000);
	}
	
	@Test
	public void quit() throws InterruptedException {
		QuitPattern quit = new QuitPattern();
		quit.setGameId(150212661932L);
		quit.setUid("zxbtest24");
		quit.setChannelId(1019);
		quit.setDeviceId("82776301283093");
		quit.setClientType(1);
		quit.setRoleId("1");
		quit.setZoneId("1");
		accountService.quit(quit);
		
		Thread.sleep(10000);
	}
	
	@Test
	public void roleEstablishLogger() throws InterruptedException{
		RoleCreatePattern roleEstablish = new RoleCreatePattern();
		roleEstablish.setGameId(150212661932L);
		roleEstablish.setUid("zxbtest24");
		roleEstablish.setChannelId(1019);
		roleEstablish.setDeviceId("827763012830934");
		roleEstablish.setClientType(1);
		roleEstablish.setRoleId("1");
		roleEstablish.setRoleName("啊啊啊啊");
		roleEstablish.setRoleLevel("100级");
		roleEstablish.setZoneId("2");
		roleEstablish.setZoneName("test分区");
		accountService.roleCreate(roleEstablish);
		Thread.sleep(10000);
	}
	
	@Test
	public void orderGenerateLogger() throws InterruptedException{
//		OrderGenerateRequest orderGenerate=new OrderGenerateRequest();
//		orderGenerate.setAppID(1L);
//		orderGenerate.setUid("10000");
//		orderGenerate.setChannelId(1);
//		orderGenerate.setDeviceId("82776301283093");
//		orderGenerate.setClientType(1);
//		orderGenerate.setRoleId("1");
//		orderGenerate.setZoneId("1");
//		orderGenerate.setChannelOrderId(RandomTool.getOrderId());
//		orderGenerate.setExtInfo("test");
//		orderGenerate.setAmount(10);
//		orderGenerate.setNotifyUrl("http://sdk.com.cn");
//		orderGenerate.setFixed(1);
//		orderGenerate.setLoginTime("123456789");
//		
//		loggerStatisService.orderGenerateLogger(orderGenerate);
//		Thread.sleep(10000);
	}
	
	@Test
	public void clearCache(){
		//redisTemplate.delete("zoneTrace_1_10000_1_1_1");
		//redisTemplate.delete("gameTrace_1_10000_1_1");
		
		redisTemplate.opsForValue().set("zoneTrace_1_10000_1_1_1", null);
		redisTemplate.opsForValue().set("gameTrace_1_10000_1_1", null);
	}
}
