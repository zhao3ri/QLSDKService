package com.qinglan.sdk.server.platform.service.impl;

import com.qinglan.sdk.server.service.OrderService;
import com.qinglan.sdk.server.utils.RedisUtil;
import com.qinglan.sdk.server.platform.service.PlatformServicePartThree;
import com.qinglan.sdk.server.platform.service.PlatformUtilsService;
import com.qinglan.sdk.server.reporsitory.BasicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by engine on 2016/10/21.
 */
@Service
public class PlatformServicePartThreeImpl implements PlatformServicePartThree {
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
