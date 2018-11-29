package com.qinglan.sdk.server.application.channel;

import javax.servlet.http.HttpServletRequest;

public interface IChannel {
    String verify(HttpServletRequest request);

    String verifySession(Session session);

    void generateOrder(HttpServletRequest request);

    void submitData();
}
