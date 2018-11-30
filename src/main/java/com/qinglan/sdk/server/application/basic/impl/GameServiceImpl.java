package com.qinglan.sdk.server.application.basic.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.Sign;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Game;
import com.qinglan.sdk.server.domain.basic.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qinglan.sdk.server.application.basic.GameService;

@Service
public class GameServiceImpl implements GameService{
	private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);
	
	@Resource
	private BasicRepository basicRepository;

	@Override
	public Game getGameById(long id) {
		return basicRepository.getGameById(id);
	}

	@Override
	public boolean notifyGame(Order order, Game game) throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformId", order.getPlatformId());
		params.put("uid", order.getUid());
		params.put("zoneId", order.getZoneId());
		params.put("roleId", order.getRoleId());
		params.put("cpOrderId", order.getCpOrderId());
		params.put("orderId", order.getOrderId());
		params.put("orderStatus", 1);
		params.put("amount", order.getAmount());
		params.put("extInfo", order.getExtInfo());
		params.put("payTime", DateUtils.format(order.getCreateTime(), DateUtils.yyyyMMddHHmmss));
		params.put("paySucTime", DateUtils.format(order.getUpdateTime(), DateUtils.yyyyMMddHHmmss));
		params.put("notifyUrl", order.getNotifyUrl());
		params.put("clientType", order.getClientType());
		params.put("sign", Sign.signByMD5(params, game.getSecretKey()));
		
		LOGGER.debug("notifyGame params: " + params);
		
		String ret = HttpUtils.doPost(order.getNotifyUrl(), params);
		
		LOGGER.debug("notifyGame ret: " + ret);
		
		if ("0".equals(ret)) {
			return true;
		}
		return false;
	}
	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformId", "1009");
		params.put("uid", "6113843");
		params.put("zoneId", "4");
		params.put("roleId", "12222");
		params.put("cpOrderId", "1607041649533883");
		params.put("orderId", "20160704165210020008623567416623");
		params.put("orderStatus", "1");
		params.put("amount", "100");
		params.put("payTime", "20160704165210");
		params.put("paySucTime", "20160704165324");
		params.put("notifyUrl", "http://apitest.19you.com/apinotify/96/yl");
		params.put("clientType", "1");
		System.out.println(Sign.signByMD5(params, "nvycbrjtolhrbztocongzlufpdfgrpfu"));
	}
	
	
	
}
