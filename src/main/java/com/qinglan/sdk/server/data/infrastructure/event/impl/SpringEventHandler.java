package com.qinglan.sdk.server.data.infrastructure.event.impl;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.data.infrastructure.event.EventHandler;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;

public class SpringEventHandler implements EventHandler {
    private final Class<?> eventType;
    private final String beanName;
    private final Method method;
    private final BeanFactory beanFactory;

    public SpringEventHandler(Class<?> eventType, String beanName, Method method, BeanFactory beanFactory) {
        this.eventType = eventType;
        this.beanName = beanName;
        this.method = method;
        this.beanFactory = beanFactory;
    }

    public boolean canHandle(Object event) {
        return event.getClass().getAnnotation(Event.class) != null && this.eventType.isAssignableFrom(event.getClass());
    }

    public void handle(Object event) {
        Object bean = this.beanFactory.getBean(this.beanName);

        try {
            this.method.invoke(bean, event);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
}