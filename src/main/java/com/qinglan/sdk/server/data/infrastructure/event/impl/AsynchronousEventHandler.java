package com.qinglan.sdk.server.data.infrastructure.event.impl;

import com.lmax.disruptor.RingBuffer;
import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.data.infrastructure.event.EventHandler;
import com.qinglan.sdk.server.data.infrastructure.event.disruptor.DisruptorEvent;
import com.qinglan.sdk.server.data.infrastructure.event.disruptor.DisruptorManager;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;

public class AsynchronousEventHandler implements EventHandler {
    private final Class<?> eventType;
    private final String beanName;
    private final Method method;
    private final BeanFactory beanFactory;

    public AsynchronousEventHandler(Class<?> eventType, String beanName, Method method, BeanFactory beanFactory) {
        this.eventType = eventType;
        this.beanName = beanName;
        this.method = method;
        this.beanFactory = beanFactory;
    }

    public boolean canHandle(Object event) {
        return event.getClass().getAnnotation(Event.class) != null && this.eventType.isAssignableFrom(event.getClass());
    }

    public void handle(Object event) {
        DisruptorManager disruptorManager = (DisruptorManager) beanFactory.getBean(DisruptorManager.class);
        RingBuffer<DisruptorEvent> ringBuffer = disruptorManager.getDisruptor().getRingBuffer();
        long sequence = ringBuffer.next();

        try {
            DisruptorEvent e = (DisruptorEvent) ringBuffer.get(sequence);
            e.setBeanFactory(beanFactory);
            e.setBeanName(this.beanName);
            e.setMethod(this.method);
            e.setRealEvent(event);
        } finally {
            ringBuffer.publish(sequence);
        }

    }

}
