package com.qinglan.sdk.server.application.listener;


import javax.annotation.Resource;

import com.qinglan.sdk.server.data.annotation.event.EventListener;
import com.qinglan.sdk.server.domain.basic.event.OrderPayEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.qinglan.sdk.server.application.redis.RedisUtil;

@Component @EventListener
public class OrderPayListener {
	private final static Logger logger = LoggerFactory.getLogger(OrderPayListener.class);
	
	@Resource(name="redisUtil")
	private RedisUtil redisUtil;
	
	@EventListener
	public void handleOrderPayEvent(OrderPayEvent event){
		try {
			redisUtil.setLpush(event.getOrder());
		} catch (InterruptedException e) {
			logger.error("写入redis队列异常",e);
		}
	}
	
	
}
