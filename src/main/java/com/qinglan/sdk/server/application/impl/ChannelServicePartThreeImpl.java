package com.qinglan.sdk.server.application.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.application.redis.RedisUtil;
import com.qinglan.sdk.server.application.ChannelServicePartThree;
import com.qinglan.sdk.server.application.ChannelUtilsService;
import com.qinglan.sdk.server.BasicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by engine on 2016/10/21.
 */
@Service
public class ChannelServicePartThreeImpl implements ChannelServicePartThree {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServicePartThreeImpl.class);
    @Resource
    private BasicRepository basicRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private ChannelUtilsService channelUtilsService;
    @Resource
    private RedisUtil redisUtil;
}
