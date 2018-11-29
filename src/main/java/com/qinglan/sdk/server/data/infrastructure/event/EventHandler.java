package com.qinglan.sdk.server.data.infrastructure.event;

public interface EventHandler {
    boolean canHandle(Object var1);

    void handle(Object var1);
}
