package com.qinglan.sdk.server.application.basic;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.qinglan.sdk.server.common.JsonMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.presentation.basic.dto.HeartbeatPattern;
import com.qinglan.sdk.server.presentation.basic.dto.InitialPattern;
import com.qinglan.sdk.server.presentation.basic.dto.LoginPattern;
import com.qinglan.sdk.server.presentation.basic.dto.LogoutPattern;
import com.qinglan.sdk.server.presentation.basic.dto.OrderGeneratePattern;
import com.qinglan.sdk.server.presentation.basic.dto.QuitPattern;
import com.qinglan.sdk.server.presentation.basic.dto.RoleEstablishPattern;

public class AccountServiceTest  extends BaseTestCase{

	@Resource
	private AccountService accountService;
	
	@After
	public void waitForFinish() {
		try {Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();}
	}
	
	@Test
	public void testPost() throws Exception{
		String url="http://zdsdktest.zhidian3g.cn/account/initial";
			
		String param="platform=Android+4.4.4&networkType=wifi&model=MI+4W&appId=23&location=&resolution=1080+x+1920&platformId=16&phonetype=null&networkCountryIso=null&systemVersion=19&manufacturer=Xiaomi&simoperatorname=null&longitude=&latitude=&imsi=460014860996849&deviceId=864895020636062&clientType=1&";
	
		String reStr=HttpUtils.doPost(url, param, 10000);
		System.out.println(reStr);
	}
	
	@Test
	public void initial() {
		String deviceId = UUID.randomUUID().toString().substring(0, 12);
		InitialPattern initial = new InitialPattern();
		initial.setGameId(23L);
		initial.setPlatformId(15);
		initial.setDeviceId(deviceId);
		initial.setClientType(1);
		initial.setManufacturer("samsung");
		initial.setModel("n7100");
		initial.setSystemVersion("3.1.1");
		initial.setPlatform("android");
		initial.setLatitude(Math.random()*10+"");
		initial.setLongitude(Math.random()*10+"");
		initial.setImsi("46007929717691");
		initial.setLocation("广东");
		initial.setNetworkCountryIso("86");
		initial.setNetworkType("0");
		initial.setPhonetype("TD-LTE");
		initial.setSimoperatorname("China Mobile");
		initial.setResolution("480");
		
		//first initial
		accountService.initial(initial);
		
		//second initial
		accountService.initial(initial);
	}
	
	@Test
	public void login() {
//SELECT * FROM b_role where appId='151110191986' and platformId='1001' and zoneId='1' and roleId='123456' and roleName='123456'
		LoginPattern login = new LoginPattern();
		login.setGameId(Long.parseLong("151110191986"));
		login.setUid("c0e8ef9ed18edfc9553182f266bd6b0c");
		login.setPlatformId(1001);
		login.setDeviceId("370436628004248");
		login.setClientType(1);
		login.setRoleId("22");
		login.setRoleName("big");
		login.setRoleLevel("11");
		login.setZoneId("1");
		login.setZoneName("test分区");
		
		//login zone 1
		accountService.login(login);
		
		//login zone 2
//		LoginPattern login2 = new LoginPattern();
//		BeanUtils.copyProperties(login, login2);
//		login2.setZoneId("2");
//		System.out.println(accountService.login(login2));
	}
	
	@Test
	public void heartbeat() {
		HeartbeatPattern heartbeat = new HeartbeatPattern();
		heartbeat.setGameId(23L);
		heartbeat.setUid("12345678");
		heartbeat.setPlatformId(15);
		heartbeat.setDeviceId("66b9a79d-fbd");
		heartbeat.setClientType(1);
		heartbeat.setRoleId("1");
		heartbeat.setZoneId("1");
		heartbeat.setLoginTime("12345679");
		accountService.heartbeat(heartbeat);
	}
	
	@Test
	public void logout() {
		LogoutPattern logout = new LogoutPattern();
		logout.setGameId(23L);
		logout.setUid("12345678");
		logout.setPlatformId(15);
		logout.setDeviceId("66b9a79d-fbd");
		logout.setClientType(1);
		logout.setRoleId("1");
		logout.setZoneId("1");
		accountService.logout(logout);
	}
	
	@Test
	public void quit() {
		QuitPattern quit = new QuitPattern();
		quit.setGameId(23L);
		quit.setUid("12345678");
		quit.setPlatformId(15);
		quit.setDeviceId("66b9a79d-fbd");
		quit.setClientType(1);
		quit.setRoleId("1");
		quit.setZoneId("1");
		accountService.quit(quit);
	}
	
	@Test
	public void roleEstablish() {
		RoleEstablishPattern roleEstablish = new RoleEstablishPattern();
		roleEstablish.setGameId(23L);
		roleEstablish.setUid("12345678");
		roleEstablish.setPlatformId(15);
		roleEstablish.setDeviceId("66b9a79d-fbd");
		roleEstablish.setClientType(1);
		roleEstablish.setRoleId("1");
		roleEstablish.setRoleName("test");
		roleEstablish.setRoleLevel("");
		roleEstablish.setZoneId("1");
		roleEstablish.setZoneName("test分区");
		accountService.roleEstablish(roleEstablish);
	}
	
	@Test
	public void orderGenerate() {
		OrderGeneratePattern orderGenerate = new OrderGeneratePattern();
		orderGenerate.setGameId(23L);
		orderGenerate.setUid("12345678");
		orderGenerate.setPlatformId(15);
		orderGenerate.setDeviceId("66b9a79d-fbd");
		orderGenerate.setClientType(1);
		orderGenerate.setRoleId("1");
		orderGenerate.setZoneId("1");
		orderGenerate.setCpOrderId(1000*2*Math.random()+"");
		orderGenerate.setExtInfo("test");
		orderGenerate.setAmount(10);
		orderGenerate.setNotifyUrl("http://sdk.com.cn");
		orderGenerate.setFixed(1);
		orderGenerate.setLoginTime("123456789");
		accountService.orderGenerate(orderGenerate);
	}

	@Test
	public void testSelfGenerate(){
		OrderGeneratePattern orderGenerate = new OrderGeneratePattern();
		orderGenerate.setGameId(23L);
		orderGenerate.setUid("12345678");
		orderGenerate.setPlatformId(15);
		orderGenerate.setDeviceId("66b9a79d-fbd");
		orderGenerate.setClientType(1);
		orderGenerate.setRoleId("1");
		orderGenerate.setZoneId("1");
		orderGenerate.setCpOrderId(1000*2*Math.random()+"");
		orderGenerate.setExtInfo("test");
		orderGenerate.setAmount(10);
		orderGenerate.setNotifyUrl("http://sdk.com.cn");
		orderGenerate.setFixed(1);
		orderGenerate.setLoginTime("123456789");
		orderGenerate.setIp("127.0.0.1");
		orderGenerate.setGameName("至尊战绩");
		orderGenerate.setPackageName("com.yaoyue.zzzj");
		orderGenerate.setSelfpay(1);
		Map<String,Object> map = accountService.selforderGenerate(orderGenerate);
		logger.info(JsonMapper.toJson(map));
		String  token = (String) map.get("tokenId");
		Assert.assertTrue(token.length()>0);
	}

	@Test
	public void testUpdateBalance(){
//		int result  = accountService.checkPlatformBalance(100,0,4);
//		logger.info("testUpdateBalance "+result);
	}
}
