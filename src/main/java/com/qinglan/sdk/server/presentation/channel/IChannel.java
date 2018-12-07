package com.qinglan.sdk.server.presentation.channel;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.domain.basic.ChannelEntity;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;

import javax.servlet.http.HttpServletRequest;


public interface IChannel {
    void init(BasicRepository basicRepository);

    void init(BasicRepository basicRepository, long gameId, int channelId);

    void init(ChannelEntity channel, ChannelGameEntity channelGame);

    String verifySession(String... args);

    String signOrder(BaseRequest request);

    String returnPayResult(HttpServletRequest request, OrderService service);

}
