package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;
import com.qinglan.sdk.server.presentation.channel.utils.HuoSdkSignHelper;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.qinglan.sdk.server.ChannelConstants.HUOSDK_PAY_RESULT_FAILED;

public class HuoSdkChannel extends BaseChannel {
    /**
     * 认证地址
     */
    public static final String VERIFY_URL = "/57k/session";
    /**
     * 支付回调地址
     */
    public static final String PAY_RETURN_URL = "/57k/pay/return";

    private static final String REQUEST_PARAM_APP_ID = "app_id";
    private static final String REQUEST_PARAM_MEM_ID = "mem_id";
    private static final String REQUEST_PARAM_USER_TOKEN = "user_token";
    private static final String REQUEST_PARAM_SIGN = "sign";

    private static final String REQUEST_PARAM_ORDER_ID = "order_id";
    private static final String REQUEST_PARAM_MONEY = "money";
    private static final String REQUEST_PARAM_ORDER_STATUS = "order_status";
    private static final String REQUEST_PARAM_PAYTIME = "paytime";
    private static final String REQUEST_PARAM_ATTACH = "attach";

    /**
     * {
     * "status": "0",
     * "msg": "请求参数为空 app_id"
     * }
     */
    @Override
    public String verifySession(String... args) {
        if (null == channelGame || null == channel || null == args || args.length == 0)
            return null;
        String appId = args[0];
        String memId = args[1];
        String userToken = args[2];
        String appKey = channelGame.getAppKey();
        String sign = HuoSdkSignHelper.sign(appId, memId, userToken, appKey);
        Map<String, String> params = new HashMap<>();
        params.put(REQUEST_PARAM_APP_ID, appId);
        params.put(REQUEST_PARAM_MEM_ID, memId);
        params.put(REQUEST_PARAM_USER_TOKEN, userToken);
        params.put(REQUEST_PARAM_SIGN, sign);
        try {
            return HttpUtils.doPostToJson(channel.getVerifyUrl(), JsonMapper.toJson(params), 30 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String signOrder(BaseRequest request) {
        return null;
    }

    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        String result = getRequestString(request);
        if (StringUtils.isEmpty(result)) {
            return HUOSDK_PAY_RESULT_FAILED;
        }

        return null;
    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }
}
