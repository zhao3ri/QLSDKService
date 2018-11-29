package com.qinglan.sdk.server.data.infrastructure.event.impl;

import com.qinglan.sdk.server.data.annotation.event.EventListener;
import com.qinglan.sdk.server.data.infrastructure.event.EventHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class EventListenerBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;
    private SimpleEventPublisher eventPublisher;

    public EventListenerBeanPostProcessor() {
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().getAnnotation(EventListener.class) != null) {
            Method[] var6;
            int var5 = (var6 = bean.getClass().getMethods()).length;

            for(int var4 = 0; var4 < var5; ++var4) {
                Method method = var6[var4];
                EventListener listenerAnnotation = (EventListener)method.getAnnotation(EventListener.class);
                if (listenerAnnotation != null) {
                    Class<?> eventType = method.getParameterTypes()[0];
                    if (listenerAnnotation.asynchronous()) {
                        EventHandler handler = new AsynchronousEventHandler(eventType, beanName, method, this.beanFactory);
                        this.eventPublisher.registerEventHandler(handler);
                    } else {
                        EventHandler handler = new SpringEventHandler(eventType, beanName, method, this.beanFactory);
                        this.eventPublisher.registerEventHandler(handler);
                    }
                }
            }
        }

        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.eventPublisher = (SimpleEventPublisher)beanFactory.getBean(SimpleEventPublisher.class);
    }
}
