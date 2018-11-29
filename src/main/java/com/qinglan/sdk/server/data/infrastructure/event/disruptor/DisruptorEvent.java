package com.qinglan.sdk.server.data.infrastructure.event.disruptor;

import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;

public class DisruptorEvent {
    private Object realEvent;
    private String beanName;
    private Method method;
    private BeanFactory beanFactory;

    public void setRealEvent(Object realEvent) {
        this.realEvent = realEvent;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public DisruptorEvent() {
    }

    public Object getRealEvent() {
        return this.realEvent;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Method getMethod() {
        return this.method;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }
}
