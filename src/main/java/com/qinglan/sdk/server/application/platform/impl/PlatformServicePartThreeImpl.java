package com.qinglan.sdk.server.application.platform.impl;

import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.application.basic.redis.RedisUtil;
import com.qinglan.sdk.server.application.platform.PlatformServicePartThree;
import com.qinglan.sdk.server.application.platform.PlatformUtilsService;
import com.qinglan.sdk.server.BasicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by engine on 2016/10/21.
 */
@Service
public class PlatformServicePartThreeImpl implements PlatformServicePartThree{
    private static final Logger logger = LoggerFactory.getLogger(PlatformServicePartThreeImpl.class);
    @Resource
    private BasicRepository basicRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private PlatformUtilsService platformUtilsService;
    @Resource
    private RedisUtil redisUtil;
}
