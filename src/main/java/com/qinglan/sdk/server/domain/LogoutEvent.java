package com.qinglan.sdk.server.domain;

import com.qinglan.sdk.server.domain.dto.LogoutPattern;
import com.zhidian3g.ddd.annotation.event.Event;

@Event
public class LogoutEvent implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private LogoutPattern logoutPattern;

    public LogoutEvent(LogoutPattern logoutPattern) {
        this.logoutPattern = logoutPattern;
    }

    public LogoutPattern getHelper() {
        return logoutPattern;
    }
}
