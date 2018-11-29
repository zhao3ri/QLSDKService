package com.qinglan.sdk.server.data.infrastructure.support;

import java.lang.annotation.Annotation;

public interface InstanceProvider {
    <T> T getInstance(Class<T> type);

    <T> T getInstance(Class<T> type, String beanName);

    <T> T getInstance(Class<T> type, Class<? extends Annotation> annotationType);
}
