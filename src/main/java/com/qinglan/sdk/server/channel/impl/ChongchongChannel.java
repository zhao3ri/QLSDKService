package com.qinglan.sdk.server.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.channel.entity.BaseRequest;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.common.Tools;
import com.qinglan.sdk.server.domain.basic.Order;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.TreeMap;

import static com.qinglan.sdk.server.channel.ChannelConstants.CC_PAY_RESULT_FAILED;
import static com.qinglan.sdk.server.channel.ChannelConstants.CC_PAY_RESULT_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_VERIFY_ERROR;

public class ChongchongChannel extends BaseChannel {
    /**
     * 认证地址
     */
    public static final String VERIFY_URL = "/chongchong/session";
    /**
     * 支付回调地址
     */
    public static final String PAY_RETURN_URL = "/chongchong/pay/return";

    public static final String VERIFY_PARAM = "?user_id=%s&token=%s";
    private static final String PAY_QUERY_URL = "http://android-api.ccplay.cc/api/v2/payment/orderStatus/?transaction_no=%s&developerKey=%s";

    private static final String REQUEST_PARAM_CHANNEL_ORDER_ID = "transactionNo";
    private static final String REQUEST_PARAM_ORDER_ID = "partnerTransactionNo";
    private static final String REQUEST_PARAM_SIGN = "sign";
    private static final String REQUEST_PARAM_STATUS_CODE = "statusCode";
    private static final String REQUEST_PARAM_ORDER_PRICE = "orderPrice";
    private static final String CODE_PAY_SUCCESS = "0000";
    private static final String VERIFY_SUCCESS = "success";

    /**
     * 返回结果：success表示已登录 fail表示未登录
     */
    @Override
    public String verifySession(String... args) {
        checkInit();
        if (null == channelGame || null == channel || null == args || args.length == 0)
            return null;
        String userId = args[0];
        String token = args[1];
        String verifyUrl = channel.getVerifyUrl();
        String result = HttpUtils.get(verifyUrl + String.format(VERIFY_PARAM, userId, token));
        int code = RESPONSE_CODE_VERIFY_ERROR;
        if (StringUtils.isEmpty(result) && result.equals(VERIFY_SUCCESS)) {
            code = RESPONSE_CODE_SUCCESS;
        }
        return JsonMapper.toJson(getResult(code, result));
    }

    @Override
    public String signOrder(BaseRequest request) {
        return null;
    }

    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        String reqStr = getRequestString(request);
        if (StringUtils.isEmpty(reqStr)) {
            return CC_PAY_RESULT_FAILED;
        }
//        ChongchongPayResult result = JsonMapper.toObject(reqStr, ChongchongPayResult.class);
        Map<String, Object> params = new TreeMap<>(Tools.getMapByParams(reqStr, false));
        Order order = getOrder(service, String.valueOf(params.get(REQUEST_PARAM_ORDER_ID).toString())
                , String.valueOf(params.get(REQUEST_PARAM_CHANNEL_ORDER_ID)));
        if (order == null) {
            return CC_PAY_RESULT_FAILED;
        }
        channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return CC_PAY_RESULT_FAILED;
        }
        String signParam = params.get(REQUEST_PARAM_SIGN).toString();
        params.remove(REQUEST_PARAM_SIGN);//sign不参与签名

        String signData = Tools.getParamsString(params) + CONNECTOR + channelGame.getAppKey();
        String sign = MD5.encode(signData);

        if (sign.equals(signParam)) {//校验签名成功
            if (params.get(REQUEST_PARAM_STATUS_CODE).toString().equals(CODE_PAY_SUCCESS)) {
                updateOrder(order, Float.valueOf(String.valueOf(params.get(REQUEST_PARAM_ORDER_PRICE))), service);
                return CC_PAY_RESULT_SUCCESS;
            } else {
                service.payFail(order.getOrderId(), params.get(REQUEST_PARAM_STATUS_CODE).toString());
            }
        }
        return CC_PAY_RESULT_FAILED;
    }

    @Override
    public String queryOrder(Order order) {
        String url = String.format(PAY_QUERY_URL, order.getChannelOrderId(), channelGame.getAppKey());
        return HttpUtils.get(url);
    }
}
