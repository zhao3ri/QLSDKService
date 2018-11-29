package com.qinglan.sdk.server.data.infrastructure.event;

import java.io.Serializable;

public interface EventPublisher {
    void publish(Serializable var1);
}
