package com.qinglan.sdk.server;

import java.util.HashMap;
import java.util.Map;

public final class Constants {
    public static final String RESPONSE_CODE = "code";
    public static final String RESPONSE_MSG = "msg";
    public static final String RESPONSE_PARAM_ERROR = "param error";
    public static final String RESPONSE_TOKEN_ERROR = "token error";
    public static final String RESPONSE_SUCCESS_CODE = "0";
    public static final String RESPONSE_PARAMETER_ILLEGAL_CODE = "1";
    public static final String RESPONSE_STOP_REGIST_CODE = "3";
    public static final String RESPONSE_SERVER_EXCEPTION_CODE = "2";
    public static final String CHANEL_SELF_PAY = "3";
    public static final String RESPONSE_BALANCE_ERROR_CODE = "4";

    public static final String RESULT_OK = "ok";
    public static final String RESULT_ERROR = "error";
    public static final String RESULT_SUCCESS = "success";

    public static final String OS_ANDROID = "Android";
    public static final String OS_IOS = "IOS";

    public static final String JIFENG_DEVELOPER_UID = "8208195";

    public static final String IOS_PAY_KEY = "KkJF820YGjhIfdCjjsXP";

    public static Map<String, String> UUCUN_APPKEY_DESKEY = new HashMap<String, String>();

    //07073、乐嗨嗨平台ID，生成订单号加密密钥
    public static final int LESHAN_PLATFORM_ID = 1060;
    public static final int LEHIHI_PLATFORM_ID = 1073;
    public static final String BASE64_ORDERID_KEY = "eK6B@#%S1F5d$@^^6a1PQAniv1cLhL7w";

    public static final int QBAO_PLATFORM_ID = 1089;

    static {
        UUCUN_APPKEY_DESKEY.put("2V0cTHgBYGzT44fOybHDdV51lt0uFPEn", "1izugwiJ");
    }
}
