package com.qinglan.sdk.server.data.annotation.command;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
    boolean asynchronous() default false;

    boolean unique() default false;

    long uniqueStorageTimeout() default 0L;
}
