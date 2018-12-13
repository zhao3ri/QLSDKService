package com.qinglan.sdk.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class HeepayTradeConfig {
    private final static Logger LOGGER = Logger.getLogger(HeepayTradeConfig.class);

    public static final String HEEPAY_REQUEST_KEY_VERSION = "version";
    public static final String HEEPAY_REQUEST_KEY_JNET_BILL_NO = "jnet_bill_no";
    public static final String HEEPAY_REQUEST_KEY_AGENT_ID = "agent_id";
    public static final String HEEPAY_REQUEST_KEY_AGENT_BILL_ID = "agent_bill_id";
    public static final String HEEPAY_REQUEST_KEY_AGENT_BILL_TIME = "agent_bill_time";
    public static final String HEEPAY_REQUEST_KEY_PAY_TYPE = "pay_type";
    public static final String HEEPAY_REQUEST_KEY_PAY_AMT = "pay_amt";
    public static final String HEEPAY_REQUEST_KEY_NOTIFY_URL = "notify_url";
    public static final String HEEPAY_REQUEST_KEY_USER_IP = "user_ip";
    public static final String HEEPAY_REQUEST_KEY_KEY = "key";
    public static final String HEEPAY_REQUEST_KEY_SIGN = "sign";
    public static final String HEEPAY_REQUEST_KEY_RETURN_URL = "return_url";
    public static final String HEEPAY_REQUEST_KEY_GOODS_NAME = "goods_name";
    public static final String HEEPAY_REQUEST_KEY_GOODS_NUM = "goods_num";
    public static final String HEEPAY_REQUEST_KEY_GOODS_NOTE = "goods_note";
    public static final String HEEPAY_REQUEST_KEY_REMARK = "remark";
    public static final String HEEPAY_REQUEST_KEY_META_OPTION = "meta_option";
    public static final String HEEPAY_XML_VALUE_TOKEN_ID = "token_id";

    public static final String HEEPAY_RESULT_KEY_RESULT = "result";
    public static final String HEEPAY_RESULT_KEY_PAY_MSG = "pay_message";
    public static final String HEEPAY_RESULT_KEY_SIGN = "sign";

    public static final String HEEPAY_META_OPTION_OS = "s";
    public static final String HEEPAY_META_OPTION_APP_NAME = "n";
    public static final String HEEPAY_META_OPTION_PACKAGE = "id";

    public static final String HEEPAY_PAY_TYPE_WECHA = "30";
    public static final String HEEPAY_PAY_TYPE_ALIPAY = "22";

    public static final int REQUEST_PAY_TYPE_WECHAT = 2;

    public static final String RESULT_KEY_PAY_TYPE = "payType";
    public static final String RESULT_KEY_AGENT_ID = "agentId";
    public static final String RESULT_KEY_TOKEN_ID = "tokenId";
    public static final String RESULT_KEY_ORDER_ID = "orderId";
    public static final String RESULT_ERROR = "error";
    public static final String RESULT_OK = "ok";
    public static final String RESULT_CODE_DEFAULT = "0";
    public static final String RESULT_CODE_PAY_FAIL = "-1";
    public static final String RESULT_CODE_SUCCESS = "1";

    public static final Properties properties = new Properties();
    public static final HeepayTradeConfig INSTANCE = new HeepayTradeConfig();

    private HeepayTradeConfig() {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("recharge_config.properties"));
        } catch (IOException e) {
            LOGGER.error("load config file recharge_config.properties exception", e);
        }
    }

    public static HeepayTradeConfig getInstance() {
        return INSTANCE;
    }

    private String get(String key) {
        return properties.getProperty(key);
    }

    public String getInitUrl() {
        return get("heepaysdk.init.url");
    }

    public String getPhoneInitUrl() {
        return get("heepay.init.url");
    }

    public String getHeepayAgentid() {
        return get("heepay.charge.agentid");
    }

    public String getHeepayRetrunUrl() {
        return get("heepay.return.url");
    }

    public String getAlipayAgentid() {
        return get("payalipay.charge.agentid");
    }

    public int getSelfPay() {
        return Integer.parseInt(get("zhidian.self"));
    }

    public String getCallbackUrl() {
        return get("heepaysdk.callback.url");
    }

    public String getWechatPayKey() {
        return get("heepay.pay.key");
    }

    public String getAliPayKey() {
        return get("payalipay.pay.key");
    }
}
