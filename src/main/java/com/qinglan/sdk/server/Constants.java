package com.qinglan.sdk.server;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String RESPONSE_KEY_CODE = "code";
    public static final String RESPONSE_KEY_CREATE_TIME = "createTime";
    public static final String RESPONSE_KEY_LOGIN_TIME = "loginTime";
    public static final String RESPONSE_KEY_ORDER_ID = "orderId";
    public static final String RESPONSE_KEY_ORDER_STATUS = "status";
    public static final String RESPONSE_KEY_ORDER_NOTIFY_STATUS = "notifyStatus";
    public static final String RESPONSE_KEY_NOTIFY_URL = "notifyUrl";

    public static final String RESPONSE_KEY_SIGN = "sign";
    public static final String RESPONSE_KEY_SIGN_TYPE = "signType";

    public static final int RESPONSE_CODE_SUCCESS = 0;
    public static final int RESPONSE_CODE_PARAMETER_ILLEGAL = 1;
    public static final int RESPONSE_CODE_STOP_REGIST = 3;
    public static final int RESPONSE_CODE_SERVER_EXCEPTION = 2;
    public static final int RESPONSE_CODE_CHANEL_SELF_PAY = 3;
    public static final int RESPONSE_CODE_BLANCE_ERROR = 4;

    public static final int GAME_CHANNEL_CODE_REGISTE_STATUS_DEFAULT = 0;
    public static final int GAME_CHANNEL_CODE_REGISTE_STATUS_DISABLE = 1;

    public static final int CHANNEL_STATUS_NORMAL = 1;

    public static final String JIFENG_DEVELOPER_UID = "8208195";

    public static final String IOS_PAY_KEY = "KkJF820YGjhIfdCjjsXP";

    public static final Map<String, String> UUCUN_APPKEY_DESKEY = new HashMap<String, String>();

    //07073、乐嗨嗨平台ID，生成订单号加密密钥
    public static final int LESHAN_PLATFORM_ID = 1060;
    public static final int LEHIHI_PLATFORM_ID = 1073;
    public static final String BASE64_ORDERID_KEY = "eK6B@#%S1F5d$@^^6a1PQAniv1cLhL7w";

    public static final int QBAO_PLATFORM_ID = 1089;

    static {
        UUCUN_APPKEY_DESKEY.put("2V0cTHgBYGzT44fOybHDdV51lt0uFPEn", "1izugwiJ");
    }
}
