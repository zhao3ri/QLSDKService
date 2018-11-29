package com.qinglan.sdk.server.domain.basic.event;

import com.qinglan.sdk.server.data.annotation.event.Event;
import com.qinglan.sdk.server.presentation.basic.dto.RoleEstablishPattern;

@Event
public class RoleEstablishEvent implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RoleEstablishPattern roleEstablishPattern;

    public RoleEstablishEvent(RoleEstablishPattern roleEstablishPattern) {
        this.roleEstablishPattern = roleEstablishPattern;
    }

    public RoleEstablishPattern getHelper() {
        return roleEstablishPattern;
    }
}
