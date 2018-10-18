package com.qinglan.sdk.server.release.application.basic.task;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTask.class);
	
	private final static Logger STATIS_LOGGER = LoggerFactory.getLogger("statisLogger");
	
	public void init() {
		Timer timer = new Timer();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 30); 
	    calendar.set(Calendar.SECOND, 0);
		timer.scheduleAtFixedRate(new NoticeSchedule(), calendar.getTime(), 1000 * 60 * 60 * 24);
	}
	
	private class NoticeSchedule extends TimerTask  {
		@Override
		public void run() {
			LOGGER.info("timer for write a empty log run()");
			STATIS_LOGGER.info("");
		}
	}
}
