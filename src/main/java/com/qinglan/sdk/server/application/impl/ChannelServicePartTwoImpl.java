package com.qinglan.sdk.server.application.impl;

import com.lenovo.pay.sign.JsonUtil;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.application.ChannelUtilsService;
import com.qinglan.sdk.server.application.log.ChannelStatsLogger;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.presentation.channel.entity.HuoSdkVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.entity.YSVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.impl.HuoSdkChannel;
import com.qinglan.sdk.server.presentation.channel.impl.YSChannel;
import com.qinglan.sdk.server.presentation.channel.IChannel;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.impl.HmsChannel;
import com.qinglan.sdk.server.application.redis.RedisUtil;
import com.qinglan.sdk.server.application.ChannelServicePartTwo;
import com.qinglan.sdk.server.BasicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by engine on 2016/10/21.
 */
@Service
public class ChannelServicePartTwoImpl implements ChannelServicePartTwo {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServicePartTwoImpl.class);
    @Resource
    private BasicRepository basicRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private ChannelUtilsService channelUtilsService;
    @Resource
    private RedisUtil redisUtil;

}
