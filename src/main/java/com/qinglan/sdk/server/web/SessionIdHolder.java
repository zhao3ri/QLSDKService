package com.qinglan.sdk.server.web;

public class SessionIdHolder {
    private static final ThreadLocal<String> sessionIdHolder = new ThreadLocal();

    public SessionIdHolder() {
    }

    public static String getSessionId() {
        return (String) sessionIdHolder.get();
    }

    public static void pushSessionId(String sid) {
        sessionIdHolder.set(sid);
    }

    public static void popSessionId() {
        sessionIdHolder.remove();
    }
}
