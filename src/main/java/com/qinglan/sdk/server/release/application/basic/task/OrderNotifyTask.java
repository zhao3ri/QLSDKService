package com.qinglan.sdk.server.release.application.basic.task;

import java.util.Timer;
import java.util.TimerTask;

import com.qinglan.sdk.server.release.domain.basic.event.CPNotifyEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zhidian3g.ddd.infrastructure.event.EventPublisher;
import com.zhidian3g.ddd.infrastructure.support.InstanceFactory;
import com.qinglan.sdk.server.release.application.basic.redis.RedisUtil;


public class OrderNotifyTask {

	private static final Logger logger = LoggerFactory.getLogger(OrderNotifyTask.class);
	private static int MINUTE_MILLIS = 1000 * 60;
	private Timer timer;
	
	public void init() {
		timer = new Timer();
		timer.schedule(new NoticeSchedule(), 1 * MINUTE_MILLIS, 1 * MINUTE_MILLIS);
		logger.debug("schedule initial success");
	}
	
	public void destroy() {
		if(null != timer) {
			timer.cancel();
			logger.debug("schedule cancel success");
		}
	}
	
	private class NoticeSchedule extends TimerTask {
		@Override
		public void run() {
			logger.info("task notify failed order items");
			RedisUtil redisUtil = InstanceFactory.getInstance(RedisUtil.class);
			EventPublisher publisher = InstanceFactory.getInstance(EventPublisher.class);
			try {
				for (int i = 0; i < 10; i++) {
					String value = redisUtil.getBrpop();
					if(StringUtils.isEmpty(value))continue;
					publisher.publish(new CPNotifyEvent((String)value));
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}
}
