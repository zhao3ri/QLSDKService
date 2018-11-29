package com.qinglan.sdk.server.data.domain;

import java.io.Serializable;

public interface ValueObject<T> extends Serializable {
    boolean sameValueAs(T var1);
}