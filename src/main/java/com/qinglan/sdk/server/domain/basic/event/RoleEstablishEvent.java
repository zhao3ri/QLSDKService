package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.dto.RoleCreatePattern;

@Event
public class RoleEstablishEvent implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RoleCreatePattern roleCreatePattern;

    public RoleEstablishEvent(RoleCreatePattern roleCreatePattern) {
        this.roleCreatePattern = roleCreatePattern;
    }

    public RoleCreatePattern getHelper() {
        return roleCreatePattern;
    }
}
