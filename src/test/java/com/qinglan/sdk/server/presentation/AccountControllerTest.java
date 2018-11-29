package com.qinglan.sdk.server.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.qinglan.sdk.server.presentation.basic.dto.LoginPattern;
import com.qinglan.sdk.server.application.basic.AccountService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.Constants;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.domain.BehaviorTest;

public class AccountControllerTest  extends BaseTestCase{
	
	private static Logger logger = LoggerFactory.getLogger(AccountControllerTest.class);
	//private static String host = "http://rsservice.y6.cn";
	//private static String host = "http://zdsdktest.zhidian3g.cn/";
	private static String host = "http://localhost:8180";

	@Resource
	private OrderService orderService;


	@Resource
	private AccountService accountService;

	@Test
	public void teststest(){
		orderService.paySuccess("20150406151945699052621803327811");
	}

	
	@Test @SuppressWarnings("unchecked")
	public void initial() {
		Map<String,Object> params = new HashMap<String, Object>();
		
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("deviceId", "c1a73738-abcdea");
		params.put("clientType", "1");
		params.put("manufacturer", "samsung");
		params.put("model", "n7100");
		params.put("systemVersion", "3.1.1");
		params.put("platform", "android");
		params.put("latitude", null);
		params.put("longitude", null);
		params.put("imsi", "46007929717691");
		params.put("location", "");
		params.put("networkCountryIso", "86");
		params.put("networkType", "wifi");
		params.put("phonetype", "TD-LTE");
		params.put("simoperatorname", "China Mobile");
		params.put("resolution", "720 x 1280");
		logger.debug("params: {}", params);

		String url = host + "/account/initial";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
	}

	@Test
	public void testLoginService(){
		LoginPattern loginPattern= new LoginPattern();
		loginPattern.setClientType(1);
		loginPattern.setDeviceId("c1a73738-abcdea");
		loginPattern.setRoleId("A_123456101");
		loginPattern.setRoleName("A角色");
		loginPattern.setRoleLevel("1");
		loginPattern.setZoneId("A_123456101");
		loginPattern.setZoneName("1");
		loginPattern.setAppId(150212661932L);
		loginPattern.setPlatformId(1001);
		accountService.login(loginPattern);
	}

	@Test @SuppressWarnings("unchecked")
	public void login() {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", "A_1234567101");
		params.put("zoneId", "1");
		params.put("zoneName", "1");
		params.put("roleId", "A_123456101");
		params.put("roleName", "A角色");
		params.put("roleLevel", "1");
		params.put("clientType", "1");
		params.put("deviceId", "c1a73738-abcdea");
		logger.debug("params: {}", params);
		
		String url = host + "/account/login";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
		
		// parameter illegal case
		params.put("platformId", "9001");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void roleEstablish(){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", "A_1234567101");
		params.put("zoneId", "1");
		params.put("zoneName", "1");
		params.put("roleId", "A_123456101");
		params.put("roleName", "A角色");
		params.put("clientType", "1");
		params.put("deviceId", "c1a73738-abcdea");
		logger.debug("params: {}", params);
		
		String url = host + "/account/role/establish";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
		
/*		params.remove("platformId");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));
		
		params.put("platformId", "9001");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));*/
	}
	@Test @SuppressWarnings("unchecked")
	public void orderGenerate() throws Exception{
		Runtime.getRuntime().exec(" cmd /c date 2015-05-17"); 
		Thread.sleep(3000);
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", "A_1234567101");
		params.put("zoneId", "1");
		params.put("roleId", "A_123456101");
		params.put("cpOrderId", UUID.randomUUID().toString());
		params.put("extInfo", UUID.randomUUID().toString());
		params.put("amount", "100");
		params.put("notifyUrl", host+"/platform/testcallback");
		params.put("fixed", "1");
		params.put("loginTime", System.currentTimeMillis());
		params.put("clientType", "1");
		params.put("deviceId", "c1a73738-abcdea");
		logger.debug("params: {}", params);
		
		String url = host + "/account/order/generate";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
		
		
		orderService.paySuccess((String)response.get("orderId"));
/*		params.remove("platformId");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));
		
		params.put("platformId", "9001");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));*/
	}
	
	@Test @SuppressWarnings("unchecked")
	public void heartbeat(){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", "A_12345678");
		params.put("zoneId", "1");
		params.put("loginTime", System.currentTimeMillis());
		params.put("roleId", "A_1234567");
		params.put("clientType", "1");
		params.put("deviceId", "c1a73738-abcdef");
		logger.debug("params: {}", params);
		
		String url = host + "/account/heartbeat";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
		
/*		params.remove("platformId");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));
		
		params.put("platformId", "9001");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));*/
	}
	
	@Test @SuppressWarnings("unchecked")
	public void logout(){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", "A_12345678");
		params.put("zoneId", "1");
		params.put("roleId", "A_1234567");
		params.put("clientType", "1");
		params.put("deviceId", "c1a73738-abcdef");
		logger.debug("params: {}", params);
		
		String url = host + "/account/logout";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
		
/*		params.remove("platformId");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));
		
		params.put("platformId", "9001");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));*/
	}
	
	@Test @SuppressWarnings("unchecked")
	public void quit(){
		Map<String,Object> params = new HashMap<String, Object>();
		//params.put("appId", "150204878739");
		params.put("appId", "150212661932");
		params.put("platformId", "1001");
		params.put("uid", "A_12345678");
		params.put("zoneId", "1");
		params.put("roleId", "A_1234567");
		params.put("clientType", "1");
		params.put("deviceId", "c1a73738-abcdef");
		logger.debug("params: {}", params);
		
		String url = host + "/account/quit";
		String result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		Map<String, Object> response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_SUCCESS, response.get(Constants.RESPONSE_CODE));
		
/*		params.remove("platformId");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));
		
		params.put("platformId", "9001");
		result = HttpUtils.post(url, params);
		logger.debug("result: {}", result);
		response = JsonMapper.toObject(result, Map.class);
		assertEquals(Constants.RESPONSE_PARAMETER_ILLEGAL, response.get(Constants.RESPONSE_CODE));*/
	}
	
	@Test
	public void testRoleTest(){
		List<BehaviorTest> list=new ArrayList<BehaviorTest>();
		for (int i = 0; i < 6; i++) {
			BehaviorTest testA=new BehaviorTest();
			Long tempTime=System.currentTimeMillis();
			testA.setrId(System.currentTimeMillis());
			testA.setrName("测试角色名字长度");
			testA.setfCTime(tempTime);
			testA.setfPTime(tempTime);
			testA.setfRTime(tempTime);
			testA.setlHTime(tempTime);
			testA.setlLogoutTime(tempTime);
			testA.setlLTime(tempTime);
			testA.setlPTime(tempTime);
			testA.setlRecord(tempTime);
			testA.setlTToday(10);
			testA.setpRecord(tempTime);
			testA.setpTToday(10);
			list.add(testA);
		}
		String strTemp=JsonMapper.toJson(list);
		List<LinkedHashMap<String, Object>> listTemp=JsonMapper.toObject(strTemp, List.class);
		if(!CollectionUtils.isEmpty(list)){
			for (LinkedHashMap<String, Object> strTemp2 : listTemp) {
				System.out.println(strTemp2.get("rId"));
			}
		}
		
	}
}
