package com.qinglan.sdk.server.application.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.Sign;
import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.domain.basic.Account;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;

public class BasicServiceTest  extends BaseTestCase{

	@Resource
	private BasicRepository basicRepository;
	
	@Test
	public void saveList(){
		List<Account> list=new ArrayList<Account>();
		MyThread mt=null;
		
		for (int i = 1; i <= 100000; i++) {
			Account account=new Account();
			account.setUid("20150115"+i);
			account.setPlatformId(20);
			account.setCreateTime(new Date());
			list.add(account);
			if(i%20000 == 10 && i !=10 ){
				mt=new MyThread(list,basicRepository);
				new Thread(mt).run();
				list.clear();
			}
		}
	}
	
	class MyThread implements Runnable{
		private BasicRepository basicRepository;
		private List<Account> list;
		
		public MyThread(List<Account> list,BasicRepository basicRepository){
			this.list=list;
			this.basicRepository=basicRepository;
		}
		@Override
		public void run() {
			basicRepository.insertbatch(list);
		}
	}
	
	@Test
	public void saveTest(){
		Account account=basicRepository.getAccount(1, "789");
		System.out.println();
	}
	@Test
	public void orderGenerateLogger(){
//		OrderGeneratePattern params = new OrderGeneratePattern();
//		params.setGameId(2L);
//		params.setPlatformId(1);
//		params.setUid("1");
//		params.setZoneId("3");
//		params.setClientType(1);
//		params.setCpOrderId("cpordernumberhjkljhkljh");
//		params.setDeviceId("546549898798656");
//		params.setAmount(10);
//		loggerStatisService.orderGenerateLogger(params);
	}
	
	/*@Test
	public void getUserBehavior(){
//		BehaviorUser behaviorUser = basicRepository.getUserBehavior(1, "1", 2, 123456L);
//		System.out.println(behaviorUser);
	}*/
	
	@Test
	public void getOrder(){
		Order order=basicRepository.getOrderByOrderId("20150121162409644897594105139496");
	}
	
	@Test
	public void notifyGame() throws Exception {
		Order order = basicRepository.getOrderByOrderId("20141225110336875419886036371916");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformId", order.getPlatformId());
		params.put("uid", order.getUid());
		params.put("zoneId", order.getZoneId());
		params.put("roleId", order.getRoleId());
		params.put("cpOrderId", order.getCpOrderId());
		params.put("orderId", order.getOrderId());
		params.put("orderStatus", 1);
		params.put("amount", order.getAmount());
		params.put("extInfo", order.getCpExtInfo());
		params.put("payTime", DateUtils.format(order.getCreateTime(), DateUtils.yyyyMMddHHmmss));
		params.put("paySucTime", DateUtils.format(order.getUpdateTime(), DateUtils.yyyyMMddHHmmss));
		params.put("notifyUrl", order.getNotifyUrl());
		params.put("clientType", order.getClientType());
		params.put("sign", Sign.signByMD5(params, "sarfc6qjze5v0dmsld95clasefysvqwt"));
		System.out.println(params);
	}
}
