package com.qinglan.sdk.server.data.domain;


import java.io.Serializable;

public interface Entity<T> extends Serializable {
    Serializable getId();

    boolean sameIdentityAs(T var1);
}
