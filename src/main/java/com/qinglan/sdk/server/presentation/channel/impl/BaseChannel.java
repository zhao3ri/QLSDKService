package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.basic.ChannelEntity;
import com.qinglan.sdk.server.presentation.channel.IChannel;

public abstract class BaseChannel implements IChannel {
    protected BasicRepository basicRepository;
    protected ChannelEntity channel;
    protected ChannelGameEntity channelGame;
    protected boolean isInit = false;

    @Override
    public void init(BasicRepository basicRepository) {
        this.basicRepository = basicRepository;
        isInit = true;
    }

    @Override
    public void init(BasicRepository basicRepository, long gameId, int channelId) {
        this.basicRepository = basicRepository;
        init(basicRepository.getChannel(channelId), basicRepository.getByChannelAndGameId(channelId, gameId));
    }

    @Override
    public void init(ChannelEntity channel, ChannelGameEntity channelGame) {
        this.channel = channel;
        this.channelGame = channelGame;
        isInit = true;
    }

    protected void checkInit() {
        if (!isInit)
            throw new RuntimeException("Please must be init before using");
    }

    protected Order getOrder(OrderService service, String orderId, String channelOrderId) {
        Order order = service.getOrderByOrderId(orderId);
        if (order != null)
            order.setChannelOrderId(channelOrderId);
        return order;
    }
}
