package com.qinglan.sdk.server.data.infrastructure.support.impl;

import com.qinglan.sdk.server.data.infrastructure.support.InstanceProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class SpringInstanceProvider implements InstanceProvider, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public SpringInstanceProvider() {
    }

    public SpringInstanceProvider(String... locations) {
        this.applicationContext = new ClassPathXmlApplicationContext(locations);
    }

    public SpringInstanceProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SpringInstanceProvider(Class... annotatedClasses) {
        this.applicationContext = new AnnotationConfigApplicationContext(annotatedClasses);
    }

    public <T> T getInstance(Class<T> beanType) {
        return this.applicationContext.getBean(beanType);
    }

    public <T> T getInstance(Class<T> beanType, String beanName) {
        return this.applicationContext.getBean(beanName, beanType);
    }

    public <T> T getInstance(Class<T> beanType, Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return this.getInstance(beanType);
        } else {
            Map<String, T> results = this.applicationContext.getBeansOfType(beanType);
            List<T> resultsWithAnnotation = new ArrayList();
            Iterator var6 = results.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, T> entry = (Map.Entry)var6.next();
                if (this.applicationContext.findAnnotationOnBean((String)entry.getKey(), annotationType) != null) {
                    resultsWithAnnotation.add(entry.getValue());
                }
            }

            if (resultsWithAnnotation.isEmpty()) {
                return null;
            } else if (resultsWithAnnotation.size() == 1) {
                return resultsWithAnnotation.get(0);
            } else {
                return null;
            }
        }
    }

    public Object getBean(String beanName) {
        return this.applicationContext.getBean(beanName);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}