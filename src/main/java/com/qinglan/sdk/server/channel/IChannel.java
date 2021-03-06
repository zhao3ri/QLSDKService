package com.qinglan.sdk.server.channel;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.channel.entity.BaseRequest;
import com.qinglan.sdk.server.domain.basic.ChannelEntity;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.domain.basic.Order;

import javax.servlet.http.HttpServletRequest;


public interface IChannel {
    void init(BasicRepository basicRepository);

    void init(BasicRepository basicRepository, long gameId, int channelId);

    void init(ChannelEntity channel, ChannelGameEntity channelGame);

    String verifySession(String... args);

    String signOrder(BaseRequest request);

    String returnPayResult(HttpServletRequest request, OrderService service);

    String queryOrder(Order order);
}
