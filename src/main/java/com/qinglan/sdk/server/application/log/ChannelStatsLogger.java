package com.qinglan.sdk.server.application.log;

import com.qinglan.sdk.server.common.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class ChannelStatsLogger {
    private final static Logger PLATFORM_LOGGER = LoggerFactory.getLogger("platformLogger");

    public static final String UC = "uc";
    public static final String HMS = "huawei";
    public static final String YESHEN = "yeshen";
    public static final String HUOSDK = "57k";
    public static final String ZHIDIAN = "zhidian";

    public static final void info(String type, String message) {
        PLATFORM_LOGGER.info(type + "|" + message + "|" + DateUtils.format(new Date()));
    }

    public static final void error(String type, String requestMsg, String message) {
        PLATFORM_LOGGER.error(type + "|" + requestMsg + "|" + message + "|" + DateUtils.format(new Date()));
    }

}