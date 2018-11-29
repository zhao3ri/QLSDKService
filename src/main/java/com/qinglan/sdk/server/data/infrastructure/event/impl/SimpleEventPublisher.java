package com.qinglan.sdk.server.data.infrastructure.event.impl;

import com.qinglan.sdk.server.data.infrastructure.event.EventHandler;
import com.qinglan.sdk.server.data.infrastructure.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class SimpleEventPublisher implements EventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventPublisher.class);
    private Set<EventHandler> eventHandlers = new HashSet();

    public SimpleEventPublisher() {
    }

    public void registerEventHandler(EventHandler handler) {
        this.eventHandlers.add(handler);
    }

    public void publish(Serializable event) {
        this.doPublish(event);
    }

    protected void doPublish(Object event) {
        Iterator var3 = (new ArrayList(this.eventHandlers)).iterator();

        while (var3.hasNext()) {
            EventHandler handler = (EventHandler) var3.next();
            if (handler.canHandle(event)) {
                try {
                    handler.handle(event);
                } catch (Exception var5) {
                    LOGGER.error("event handling error", var5);
                }
            }
        }

    }
}
