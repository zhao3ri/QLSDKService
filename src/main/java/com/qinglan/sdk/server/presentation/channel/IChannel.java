package com.qinglan.sdk.server.presentation.channel;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.domain.basic.Platform;
import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public interface IChannel {
    void init(BasicRepository basicRepository);

    void init(BasicRepository basicRepository, long gameId, int channelId);

    void init(Platform platform, PlatformGame platformGame);

    String verifySession(String... args);

    String signOrder(BaseRequest request);

    String returnPayResult(HttpServletRequest request, OrderService service);

}
