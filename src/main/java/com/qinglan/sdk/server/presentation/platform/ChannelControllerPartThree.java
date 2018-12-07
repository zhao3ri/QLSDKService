package com.qinglan.sdk.server.presentation.platform;

import com.qinglan.sdk.server.application.platform.ChannelServicePartThree;
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
public class ChannelControllerPartThree {

    private static final Logger logger = LoggerFactory.getLogger(ChannelControllerPartThree.class);

    @Resource
    private ChannelServicePartThree platformServiceTree;
}
