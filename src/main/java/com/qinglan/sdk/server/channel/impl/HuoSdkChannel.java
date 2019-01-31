package com.qinglan.sdk.server.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.channel.entity.BaseRequest;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.channel.entity.HuoSdkOrderRequest;
import com.qinglan.sdk.server.channel.utils.HuoSdkSignHelper;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.qinglan.sdk.server.channel.ChannelConstants.HUOSDK_PAY_RESULT_FAILED;
import static com.qinglan.sdk.server.channel.ChannelConstants.HUOSDK_PAY_RESULT_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_VERIFY_ERROR;

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
    private static final String REQUEST_PARAM_APP_KEY = "app_key";
    private static final String REQUEST_PARAM_MEM_ID = "mem_id";
    private static final String REQUEST_PARAM_USER_TOKEN = "user_token";
    private static final String REQUEST_PARAM_SIGN = "sign";

    private static final String REQUEST_PARAM_ORDER_ID = "cp_order_id";
    private static final String REQUEST_PARAM_CHANNEL_ORDER_ID = "order_id";
    private static final String REQUEST_PARAM_MONEY = "money";
    private static final String REQUEST_PARAM_ORDER_STATUS = "order_status";
    private static final String REQUEST_PARAM_PAYTIME = "paytime";
    private static final String REQUEST_PARAM_ATTACH = "attach";

    private static final int RESULT_STATUS_SUCCESS = 1;
    private static final String RESULT_STATUS = "status";
    private static final String RESULT_MSG = "msg";

    private static final int STATUS_ORDER_WAITING = 0;
    private static final int STATUS_ORDER_SUCCESS = 1;
    private static final int STATUS_ORDER_FAIL = 2;

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

        int code = RESPONSE_CODE_VERIFY_ERROR;
        String message = "";
        try {
            String result = HttpUtils.doPostToJson(channel.getVerifyUrl(), JsonMapper.toJson(params), 30 * 1000);
            Map<String, Object> responseParams = getResponseParams(result);
            String status = String.valueOf(responseParams.get(RESULT_STATUS));
            if (status.equals(RESULT_STATUS_SUCCESS)) {
                code = RESPONSE_CODE_SUCCESS;
            }
            message = String.valueOf(responseParams.get(RESULT_MSG));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonMapper.toJson(getResult(code, message));
    }

    @Override
    public String signOrder(BaseRequest request) {
        if (request instanceof HuoSdkOrderRequest) {
            String appId = channelGame.getAppID();
            String appKey = channelGame.getAppKey();
            String signStr = REQUEST_PARAM_CHANNEL_ORDER_ID + ((HuoSdkOrderRequest) request).getOrderId()
                    + CONNECTOR + REQUEST_PARAM_MEM_ID + ((HuoSdkOrderRequest) request).getMemId()
                    + CONNECTOR + REQUEST_PARAM_APP_ID + appId
                    + CONNECTOR + REQUEST_PARAM_MONEY + ((HuoSdkOrderRequest) request).getProductPrice()
                    + CONNECTOR + REQUEST_PARAM_ORDER_STATUS + ((HuoSdkOrderRequest) request).getOrderStatus()
                    + CONNECTOR + REQUEST_PARAM_PAYTIME + ((HuoSdkOrderRequest) request).getPayTime()
                    + CONNECTOR + REQUEST_PARAM_ATTACH + ((HuoSdkOrderRequest) request).getExt()
                    + CONNECTOR + REQUEST_PARAM_APP_KEY + appKey;
            String sign = HuoSdkSignHelper.md5(signStr);
            return sign;
        }
        return null;
    }

    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        String result = getRequestString(request);
        if (StringUtils.isEmpty(result)) {
            return HUOSDK_PAY_RESULT_FAILED;
        }
        HuoSdkOrderRequest orderRequest = JsonMapper.toObject(result, HuoSdkOrderRequest.class);
        if (null == orderRequest) {
            return HUOSDK_PAY_RESULT_FAILED;
        }
        String orderId = orderRequest.getCpOrderId();
        String channelOrderId = orderRequest.getOrderId();
        Order order = getOrder(service, orderId, channelOrderId);
        if (order == null) {
            return HUOSDK_PAY_RESULT_FAILED;
        }
        channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        String sign = signOrder(orderRequest);
        if (!StringUtils.isEmpty(sign) && sign.equals(orderRequest.getSign())) {//认证签名
            if (Integer.valueOf(orderRequest.getOrderStatus()) == STATUS_ORDER_SUCCESS) {//支付成功
                updateOrder(order, Double.valueOf(orderRequest.getProductPrice()), service);
                return HUOSDK_PAY_RESULT_SUCCESS;
            } else {
                service.payFail(order.getOrderId(), "order pay fail");
            }
        }
        return HUOSDK_PAY_RESULT_FAILED;
    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }
}
