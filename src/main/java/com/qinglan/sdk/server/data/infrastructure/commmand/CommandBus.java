package com.qinglan.sdk.server.data.infrastructure.commmand;

public interface CommandBus {
    Object dispatch(Object o);
}
