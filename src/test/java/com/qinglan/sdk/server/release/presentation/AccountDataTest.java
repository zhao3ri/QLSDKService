package com.qinglan.sdk.server.release.presentation;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.release.domain.UserTest;
public class AccountDataTest {
	private static Logger logger = LoggerFactory.getLogger(AccountControllerTest.class);
	private static String host = "http://localhost:8092";
	@Test
	public void testData(){
		try {
			List<UserTest> list=new ArrayList<UserTest>();
			for (int i = 0; i < 10000; i++) {
				String deviceId="DeviceId_E_"+i;
				String uid="Uid_E_"+i;
				for (int j = 1; j < 3; j++) {
					String roleId="RoleId_E_"+i;
					String imsi=46000000L+i+"";
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
/*			List<UserTest> list_11=new ArrayList<UserTest>();
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
			list_20.addAll(list.subList(19001, 20000));*/

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
			
			
			/*listTemp.addAll(list_3);
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
			goData(listTemp,26,31);*/
			
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
		Thread.sleep(1000);
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
		Thread.sleep(1000);
	}
	
	
	public void quit(String deviceId,String uid,String roleId){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", uid);
		params.put("zoneId", "1");
		params.put("roleId", roleId);
		params.put("clientType", "1");
		params.put("deviceId", deviceId);
		String url = host + "/account/quit";
		HttpUtils.post(url, params);
	}
	
	public void logout(String deviceId,String uid,String roleId){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid",uid);
		params.put("zoneId", "1");
		params.put("roleId",roleId);
		params.put("clientType", "1");
		params.put("deviceId",deviceId);
		String url = host + "/account/logout";
		HttpUtils.post(url, params);
	}
	
	public void orderGenerate(String deviceId,String uid,String roleId){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", uid);
		params.put("zoneId", "1");
		params.put("roleId", roleId);
		params.put("extInfo", "1234567889");
		params.put("cpOrderId", UUID.randomUUID().toString());
		params.put("amount", "100");
		params.put("notifyUrl", host+"/platform/testcallback");
		params.put("fixed", "1");
		params.put("loginTime", System.currentTimeMillis());
		params.put("clientType", "1");
		params.put("deviceId", deviceId);
		logger.debug("params: {}", params);
		
		String url = host + "/account/order/generate";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		params.clear();
		params.put("orderId", (String)response.get("orderId"));
		HttpUtils.post(host+"/platform/callback", params);
	}
	
	public void heartbeat(String deviceId,String uid,String roleId){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", uid);
		params.put("zoneId", "1");
		params.put("loginTime", System.currentTimeMillis());
		params.put("roleId", roleId);
		params.put("clientType", "1");
		params.put("deviceId", deviceId);
		String url = host + "/account/heartbeat";
		HttpUtils.post(url, params);
	}
	
	public void login(String deviceId,String uid,String roleId) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", uid);
		params.put("zoneId", "1");
		params.put("zoneName", "测试分区");
		params.put("roleId", roleId);
		params.put("roleName", "角色_"+roleId);
		params.put("roleLevel", "1");
		params.put("clientType", "1");
		params.put("deviceId", deviceId);
		logger.debug("params: {}", params);
		String url = host + "/account/login";
		HttpUtils.post(url, params);
	}
	
	public void roleEstablish(String deviceId,String uid,String roleId){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", uid);
		params.put("zoneId", "1");
		params.put("zoneName", "测试分区");
		params.put("roleId", roleId);
		params.put("roleName", "角色_"+roleId);
		params.put("clientType", "1");
		params.put("deviceId", deviceId);
		HttpUtils.post(host + "/account/role/establish", params);
	}
	
	public void initial(String uuid,String imsi){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("deviceId", uuid);
		params.put("clientType", "1");
		params.put("manufacturer", "samsung");
		params.put("model", "n7100");
		params.put("systemVersion", "3.1.1");
		params.put("platform", "android");
		params.put("latitude", null);
		params.put("longitude", null);
		params.put("imsi", imsi);
		params.put("location", "");
		params.put("networkCountryIso", "86");
		params.put("networkType", "wifi");
		params.put("phonetype", "TD-LTE");
		params.put("simoperatorname", "China Mobile");
		params.put("resolution", "720 x 1280");
		HttpUtils.post(host + "/account/initial", params);
	}
	
	@Test
	public void textData() throws Exception{
		System.out.println(DateUtils.format(new Date()));
		File file=new File("C://Users//Administrator//Desktop//statis//statis.log");
		InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GBK");//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        List<String> list=new ArrayList<String>();
        int i=1;
        while((lineTxt = bufferedReader.readLine()) != null){
        	String temp="";
        	if(i<10){
        		temp="0"+i;
        	}else{
        		temp=i+"";
        	}
        	String[] tempLOG=lineTxt.split("\\|");
        	if(tempLOG[2].lastIndexOf("201504"+temp)!=0 ){
        		File file2=new File("C://Users//Administrator//Desktop//statis//statis.log.2015-04-"+temp);
        		BufferedWriter output = new BufferedWriter(new FileWriter(file2));  
        		for (String string : list) {
        			output.write(string.toString());
				}
        		i++;
        		list.clear();
        	}
        	if(tempLOG[0].equals("1002")){
        		String a=tempLOG[39];
				String time=DateUtils.toStringDate(new Date(Long.valueOf(a)));
				lineTxt=lineTxt.replace(a, time);
        	}
        	if(tempLOG[0].equals("1007")){
        		String a=tempLOG[21];
				String time=DateUtils.toStringDate(new Date(Long.valueOf(a)));
				lineTxt=lineTxt.replace(a, time);
        	}
        	list.add(lineTxt+"\n");
        }
        System.out.println(DateUtils.format(new Date()));
	}
}
