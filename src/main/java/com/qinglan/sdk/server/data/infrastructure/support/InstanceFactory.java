package com.qinglan.sdk.server.data.infrastructure.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class InstanceFactory implements BeanFactoryAware {
    private static InstanceProvider instanceProvider;

    public InstanceFactory() {
    }

    public static void setInstanceProvider(InstanceProvider provider) {
        instanceProvider = provider;
    }

    public static <T> T getInstance(Class<T> beanType) {
        return instanceProvider.getInstance(beanType);
    }

    public static <T> T getInstance(Class<T> beanType, String beanName) {
        return instanceProvider.getInstance(beanType, beanName);
    }

    public static <T> T getInstance(Class<T> beanType, Class<? extends Annotation> annotationType) {
        return instanceProvider.getInstance(beanType, annotationType);
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        instanceProvider = (InstanceProvider)beanFactory.getBean(InstanceProvider.class);
    }
}

