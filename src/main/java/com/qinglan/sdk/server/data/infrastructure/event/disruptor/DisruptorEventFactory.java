package com.qinglan.sdk.server.data.infrastructure.event.disruptor;

import com.lmax.disruptor.EventFactory;

public class DisruptorEventFactory implements EventFactory<DisruptorEvent> {
    public DisruptorEventFactory() {
    }

    public DisruptorEvent newInstance() {
        return new DisruptorEvent();
    }
}
