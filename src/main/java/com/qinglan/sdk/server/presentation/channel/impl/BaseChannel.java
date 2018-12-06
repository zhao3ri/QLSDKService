package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Platform;
import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.presentation.channel.IChannel;

public abstract class BaseChannel implements IChannel {
    protected BasicRepository basicRepository;
    protected Platform platform;
    protected PlatformGame platformGame;
    protected boolean isInit = false;

    @Override
    public void init(BasicRepository basicRepository) {
        this.basicRepository = basicRepository;
        isInit = true;
    }

    @Override
    public void init(BasicRepository basicRepository, long gameId, int channelId) {
        this.basicRepository = basicRepository;
        init(basicRepository.getPlatform(channelId), basicRepository.getByPlatformAndGameId(channelId, gameId));
    }

    @Override
    public void init(Platform platform, PlatformGame platformGame) {
        this.platform = platform;
        this.platformGame = platformGame;
        isInit = true;
    }

    protected void checkInit() {
        if (!isInit)
            throw new RuntimeException("Please must be init before using");
    }
}
