package com.qinglan.sdk.server.presentation.channel;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Platform;
import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.presentation.basic.dto.OrderBasicInfo;

import java.util.Map;


public interface IChannel {
    void init(BasicRepository basicRepository, long gameId, int channelId);

    void init(Platform platform, PlatformGame platformGame);

    Platform getChannelInfo();

    String verifySession(String... args);

    String signOrder(OrderBasicInfo order);

    boolean getPayResult(String json, Map<String, Object> result);
}
