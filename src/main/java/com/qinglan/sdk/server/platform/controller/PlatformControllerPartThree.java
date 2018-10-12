package com.qinglan.sdk.server.platform.controller;

import com.qinglan.sdk.server.platform.service.PlatformServicePartThree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by engine on 2016/10/21.
 */

@Controller
@RequestMapping("/platform3")
public class PlatformControllerPartThree {

    private static final Logger logger = LoggerFactory.getLogger(PlatformControllerPartThree.class);

    @Resource
    private PlatformServicePartThree platformServiceTree;
}
