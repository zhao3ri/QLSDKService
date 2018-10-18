package com.qinglan.sdk.server.release.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.release.application.basic.AccountService;
import com.qinglan.sdk.server.release.domain.UserTest;
import com.qinglan.sdk.server.release.presentation.basic.dto.HeartbeatPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.InitialPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.LoginPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.LogoutPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.OrderGeneratePattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.QuitPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.RoleEstablishPattern;

public class AccountData2Test {
	
	@Resource
	private AccountService accountService;
	
	public void testData(){
		try {
			List<UserTest> list=new ArrayList<UserTest>();
			for (int i = 0; i < 10000; i++) {
				String deviceId="DeviceId_L_"+i;
				String uid="Uid_L_"+i;
				String imsi=46000000L+i+"L";
				for (int j = 1; j < 3; j++) {
					String roleId="RoleId_L_"+i+"_"+j;
					UserTest user=new UserTest();
					user.setDeviceId(deviceId);
					user.setRoleId(roleId);
					user.setUid(uid);
					user.setImsi(imsi);
					list.add(user);
				}
			}
			List<UserTest> list_1=new ArrayList<UserTest>();
			list_1.addAll(list.subList(0, 1000));
			List<UserTest> list_2=new ArrayList<UserTest>();
			list_2.addAll(list.subList(1001, 2000));
			List<UserTest> list_3=new ArrayList<UserTest>();
			list_3.addAll(list.subList(2001, 3000));
			List<UserTest> list_4=new ArrayList<UserTest>();
			list_4.addAll(list.subList(3001, 4000));
			List<UserTest> list_5=new ArrayList<UserTest>();
			list_5.addAll(list.subList(4001, 5000));
			List<UserTest> list_6=new ArrayList<UserTest>();
			list_6.addAll(list.subList(5001, 6000));
			List<UserTest> list_7=new ArrayList<UserTest>();
			list_7.addAll(list.subList(6001, 7000));
			List<UserTest> list_8=new ArrayList<UserTest>();
			list_8.addAll(list.subList(7001, 8000));
			List<UserTest> list_9=new ArrayList<UserTest>();
			list_9.addAll(list.subList(8001, 9000));
			List<UserTest> list_10=new ArrayList<UserTest>();
			list_10.addAll(list.subList(9001, 10000));

			List<UserTest> list_11=new ArrayList<UserTest>();
			list_11.addAll(list.subList(10001, 11000));
			List<UserTest> list_12=new ArrayList<UserTest>();
			list_12.addAll(list.subList(11001, 12000));
			List<UserTest> list_13=new ArrayList<UserTest>();
			list_13.addAll(list.subList(12001, 13000));
			List<UserTest> list_14=new ArrayList<UserTest>();
			list_14.addAll(list.subList(13001, 14000));
			List<UserTest> list_15=new ArrayList<UserTest>();
			list_15.addAll(list.subList(14001, 15000));
			List<UserTest> list_16=new ArrayList<UserTest>();
			list_16.addAll(list.subList(15001, 16000));
			List<UserTest> list_17=new ArrayList<UserTest>();
			list_17.addAll(list.subList(16001, 17000));
			List<UserTest> list_18=new ArrayList<UserTest>();
			list_18.addAll(list.subList(17001, 18000));
			List<UserTest> list_19=new ArrayList<UserTest>();
			list_19.addAll(list.subList(18001, 19000));
			List<UserTest> list_20=new ArrayList<UserTest>();
			list_20.addAll(list.subList(19001, 20000));

			List<UserTest> listTemp=new ArrayList<UserTest>();
			
			listTemp.addAll(list_1);
			initialData(listTemp,1);
			goData(listTemp,1,5);
			listTemp.clear();
			
			listTemp.addAll(list_2);
			initialData(listTemp,6);
			listTemp.addAll(list_1);
			goData(listTemp,6,9);
			listTemp.clear();
			
			listTemp.addAll(list_3);
			listTemp.addAll(list_4);
			initialData(listTemp,9);
			goData(listTemp,9,13);
			listTemp.clear();
			
			listTemp.addAll(list_5);
			listTemp.addAll(list_6);
			initialData(listTemp,14);
			listTemp.addAll(list_1);
			listTemp.addAll(list_2);
			goData(listTemp,14,19);
			listTemp.clear();
			
			listTemp.addAll(list_7);
			listTemp.addAll(list_8);
			initialData(listTemp,20);
			goData(listTemp,20,25);
			listTemp.clear();
			
			listTemp.addAll(list_9);
			listTemp.addAll(list_10);
			initialData(listTemp,26);
			listTemp.addAll(list_7);
			listTemp.addAll(list_8);
			goData(listTemp,26,31);
			listTemp.clear();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void initialData(List<UserTest> list,int time) throws Exception{
		Runtime.getRuntime().exec(" cmd /c date 2015-04-"+time); 
		Thread.sleep(3000);
		for (UserTest userTest : list) {
			initial(userTest.getDeviceId(),userTest.getImsi());//初始化
			roleEstablish(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//创角
			login(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//登录
			heartbeat(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//心跳
			Random random=new Random();
			if(random.nextInt(10)%2 == 0){
				logout(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//退出
			}else{
				quit(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//注销
			}
		}
	}
	
	public void goData(List<UserTest> list,int timeStart,int timeEnd) throws Exception{
		for (int i = timeStart; i <= timeEnd; i++) {
			Runtime.getRuntime().exec(" cmd /c date 2015-04-"+i); 
			Thread.sleep(3000);
			for (int j = 0; j < 50; j++) {
				Random random=new Random();
				int number=random.nextInt(list.size());
				UserTest userTest=list.get(number);
				initial(userTest.getDeviceId(),userTest.getImsi());//初始化
				login(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//登录
				heartbeat(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//心跳
				orderGenerate(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());
				if(random.nextInt(10)%2 == 0){
					logout(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//退出
				}else{
					quit(userTest.getDeviceId(),userTest.getUid(),userTest.getRoleId());//注销
				}
			}
		}
	}
	
	
	public void quit(String deviceId,String uid,String roleId){
		QuitPattern quitPattern=new QuitPattern();
		quitPattern.setAppId(150212661932L);
		quitPattern.setPlatformId(1001);
		quitPattern.setDeviceId(deviceId);
		quitPattern.setUid(uid);
		quitPattern.setZoneId("1");
		quitPattern.setRoleId(roleId);
		quitPattern.setClientType(1);
		accountService.quit(quitPattern);
	}
	
	public void logout(String deviceId,String uid,String roleId){
		LogoutPattern logoutPattern=new LogoutPattern();
		logoutPattern.setAppId(150212661932L);
		logoutPattern.setPlatformId(1001);
		logoutPattern.setDeviceId(deviceId);
		logoutPattern.setUid(uid);
		logoutPattern.setZoneId("1");
		logoutPattern.setRoleId(roleId);
		logoutPattern.setClientType(1);
		accountService.logout(logoutPattern);
	}
	
	public void orderGenerate(String deviceId,String uid,String roleId){
		
		OrderGeneratePattern orderGenerate=new OrderGeneratePattern();
		orderGenerate.setAppId(150212661932L);
		orderGenerate.setPlatformId(1001);
		orderGenerate.setDeviceId(deviceId);
		orderGenerate.setUid(uid);
		orderGenerate.setZoneId("1");
		orderGenerate.setRoleId(roleId);
		orderGenerate.setRoleName("角色_"+roleId);
		orderGenerate.setClientType(1);
		orderGenerate.setExtInfo("123456789");
		orderGenerate.setCpOrderId(UUID.randomUUID().toString());
		orderGenerate.setAmount(100);
		orderGenerate.setNotifyUrl("http://localhost:8092/platform/testcallback");
		orderGenerate.setFixed(1);
		orderGenerate.setLoginTime(System.currentTimeMillis()+"");
		Map<String, Object> response = accountService.orderGenerate(orderGenerate);
		HttpUtils.get("http://192.168.3.222:8092/platform/callback?orderId="+(String)response.get("orderId"));
	}
	
	public void heartbeat(String deviceId,String uid,String roleId){
		HeartbeatPattern heartbeat=new HeartbeatPattern();
		heartbeat.setAppId(150212661932L);
		heartbeat.setPlatformId(1001);
		heartbeat.setDeviceId(deviceId);
		heartbeat.setUid(uid);
		heartbeat.setZoneId("1");
		heartbeat.setRoleId(roleId);
		heartbeat.setClientType(1);
		heartbeat.setLoginTime(System.currentTimeMillis()+"");
		accountService.heartbeat(heartbeat);
	}
	
	public void login(String deviceId,String uid,String roleId) {
		LoginPattern loginPattern=new LoginPattern();
		loginPattern.setAppId(150212661932L);
		loginPattern.setPlatformId(1001);
		loginPattern.setDeviceId(deviceId);
		loginPattern.setUid(uid);
		loginPattern.setZoneId("1");
		loginPattern.setZoneName("测试分区");
		loginPattern.setRoleId(roleId);
		loginPattern.setRoleName("角色_"+roleId);
		loginPattern.setRoleLevel("1");
		loginPattern.setClientType(1);
		accountService.login(loginPattern);
	}
	
	public void roleEstablish(String deviceId,String uid,String roleId){
		RoleEstablishPattern roleEstablish=new RoleEstablishPattern();
		roleEstablish.setAppId(150212661932L);
		roleEstablish.setPlatformId(1001);
		roleEstablish.setDeviceId(deviceId);
		roleEstablish.setUid(uid);
		roleEstablish.setZoneId("1");
		roleEstablish.setZoneName("测试分区");
		roleEstablish.setRoleId(roleId);
		roleEstablish.setRoleName("角色_"+roleId);
		roleEstablish.setRoleLevel("1");
		roleEstablish.setClientType(1);
		accountService.roleEstablish(roleEstablish);		
	}
	
	public void initial(String deviceId,String imsi){
		InitialPattern initial=new InitialPattern();
		initial.setAppId(150212661932L);
		initial.setPlatformId(1001);
		initial.setDeviceId(deviceId);
		initial.setClientType(1);
		initial.setManufacturer("samsung");
		initial.setModel("n7100");
		initial.setSystemVersion("3.1.1");
		initial.setPlatform("android");
		initial.setImsi(imsi);
		initial.setNetworkCountryIso("86");
		initial.setNetworkType("wifi");
		initial.setPhonetype("TD-LTE");
		initial.setSimoperatorname("China Mobile");
		initial.setResolution("720 x 1280");
		accountService.initial(initial);
	}
}
