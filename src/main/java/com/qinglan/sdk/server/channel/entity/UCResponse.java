package com.qinglan.sdk.server.channel.entity;

import java.util.Map;

public class UCResponse {
    public static final String RESPONSE_KEY_CODE = "code";
    public static final String RESPONSE_KEY_MSG = "msg";
    public static final String RESPONSE_KEY_CREATOR = "creator";
    public static final String RESPONSE_KEY_ACCOUNT_ID = "accountId";
    public static final String RESPONSE_KEY_NICKNAME = "nickName";

    public static final int RESPONSE_SUCCESS_CODE = 1;

    public long id;
    public Map<String, Object> state;
    public Map<String, Object> data;
}
