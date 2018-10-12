package com.qinglan.sdk.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsLogger {
	private final static Logger STATIS_LOGGER = LoggerFactory.getLogger("statisLogger");
	
	private final static int LOGGER_CODE_INITIAL = 1001;
	private final static int LOGGER_CODE_LOGIN = 1002;
	private final static int LOGGER_CODE_HEARTBEAT = 1003;
	private final static int LOGGER_CODE_LOGOUT = 1004;
	private final static int LOGGER_CODE_QUIT = 1005;
	private final static int LOGGER_CODE_ROLE_ESTABLISH = 1006;
	private final static int LOGGER_CODE_PAY_SUCCESS = 1007;
	private final static int LOGGER_CODE_PAY = 1008;
	
	public static String initial(String message) {
		String msg = LOGGER_CODE_INITIAL + "|" + 1 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	
	public static String login(String message) {
		String msg = LOGGER_CODE_LOGIN + "|" + 2 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	
	public static String heartbeat(String message) {
		String msg = LOGGER_CODE_HEARTBEAT + "|" + 2 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	
	public static String logout(String message) {
		String msg = LOGGER_CODE_LOGOUT + "|" + 2 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	
	public static String quit(String message) {
		String msg = LOGGER_CODE_QUIT + "|" + 2 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}

	public static String roleEstablish(String message) {
		String msg = LOGGER_CODE_ROLE_ESTABLISH + "|" + 1 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	
	public static String paySuccess(String message) {
		String msg = LOGGER_CODE_PAY_SUCCESS + "|" + 2 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	public static String pay(String message) {
		String msg = LOGGER_CODE_PAY + "|" + 2 + "|" + message;
		STATIS_LOGGER.info(msg);
		return msg;
	}
	
	public static void main(String[] args) {
	}
}