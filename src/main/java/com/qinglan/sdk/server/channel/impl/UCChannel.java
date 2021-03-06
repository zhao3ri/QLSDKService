package com.qinglan.sdk.server.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.application.log.ChannelStatsLogger;
import com.qinglan.sdk.server.channel.entity.BaseRequest;
import com.qinglan.sdk.server.channel.entity.UCResponse;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.channel.entity.UCOrderSignRequest;
import com.qinglan.sdk.server.channel.entity.UCPayResult;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.qinglan.sdk.server.channel.ChannelConstants.UC_PAY_RESULT_FAILED;
import static com.qinglan.sdk.server.channel.ChannelConstants.UC_PAY_RESULT_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_VERIFY_ERROR;
import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_DATA;

public class UCChannel extends BaseChannel {
    /**
     * 认证地址
     */
    public static final String VERIFY_URL = "/ucgame/session";
    /**
     * 支付回调地址
     */
    public static final String PAY_RETURN_URL = "/ucgame/pay/return";

    private static final String REQUEST_KEY_SID = "sid";
    private static final String REQUEST_KEY_GAME_ID = "gameId";
    private static final String REQUEST_KEY_ID = "id";
    private static final String REQUEST_KEY_DATA = "data";
    private static final String REQUEST_KEY_GAME = "game";
    public static final String REQUEST_KEY_SIGN = "sign";
    private static final String SIGN_PREFIX = "sid=";

    private static final String PARAM_CALLBACK_INFO = "callbackInfo";
    private static final String PARAM_NOTIFY_URL = "notifyUrl";
    private static final String PARAM_AMOUNT = "amount";
    private static final String PARAM_ORDER_ID = "cpOrderId";
    private static final String PARAM_CHANNEL_ORDER_ID = "orderId";
    private static final String PARAM_ACCOUNT_ID = "accountId";

    private static final String RESULT_ORDER_STATUS_SUCCESS = "S";

    /**
     * 第三方平台认证
     * 请求参数json示例如下：
     * {
     * "id":1332406591685,
     * "game":{"gameId":5},
     * "data":{
     * "sid":"110adf4c-f2d3-4be5-8a9c-3741a83e5853"
     * },
     * "sign":"bb926c2a9944e9b4f2f6639d928dc95c"
     * }
     * 返回json示例：
     * {
     * "id":1332406591685,
     * "state":{
     * "code":1,
     * "msg":"jjjjj"
     * },
     * "data":{
     * "accountId":"110adf4c-f2d3-4be5-8a9c-3741a83e5853"，
     * "creator":"tt",
     * "nickName":"yoyo"
     * }
     */
    @Override
    public String verifySession(String... args) {
        checkInit();

        if (null == channelGame || null == channel || null == args || args.length == 0)
            return null;
        String sid = args[0];
        String appID = null;
        if (args.length > 1)
            appID = args[1];
        if (null == appID || appID.isEmpty())
            appID = channelGame.getAppID();
        String appKey = channelGame.getAppKey();

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> game = new HashMap<>();
        data.put(REQUEST_KEY_SID, sid);
        game.put(REQUEST_KEY_GAME_ID, appID);
        params.put(REQUEST_KEY_ID, System.currentTimeMillis());
        params.put(REQUEST_KEY_DATA, data);
        params.put(REQUEST_KEY_GAME, game);
        params.put(REQUEST_KEY_SIGN, MD5.encode(SIGN_PREFIX + sid + appKey));
        int code = RESPONSE_CODE_VERIFY_ERROR;
        String msg = "";
        Map<String, Object> resData = new HashMap<>();
        try {
            String result = HttpUtils.doPostToJson(channel.getVerifyUrl(), JsonMapper.toJson(params), 10000);
            UCResponse response = JsonMapper.toObject(result, UCResponse.class);
            if (null != response) {
                if (Integer.valueOf(response.state.get(UCResponse.RESPONSE_KEY_CODE).toString()) == UCResponse.RESPONSE_SUCCESS_CODE) {
                    code = RESPONSE_CODE_SUCCESS;
                    resData = response.data;
                }
                msg = String.valueOf(response.state.get(UCResponse.RESPONSE_KEY_MSG));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> res = getResult(code, msg);
        res.put(RESPONSE_KEY_DATA, resData);
        return JsonMapper.toJson(res);
    }

    @Override
    public String signOrder(BaseRequest request) {
        checkInit();
        if (request instanceof UCOrderSignRequest) {
            String appKey = channelGame.getAppKey();
            Map<String, String> signMap = new TreeMap<>();
            signMap.put(PARAM_CALLBACK_INFO, ((UCOrderSignRequest) request).getExtInfo());
            signMap.put(PARAM_NOTIFY_URL, ((UCOrderSignRequest) request).getNotifyUrl());
            signMap.put(PARAM_AMOUNT, ((UCOrderSignRequest) request).getAmount().toString());
            signMap.put(PARAM_ORDER_ID, ((UCOrderSignRequest) request).getOrderId());
            signMap.put(PARAM_ACCOUNT_ID, ((UCOrderSignRequest) request).getUid());
            String sign = Sign.aliSign(signMap, appKey);
            return sign;
        }
        return null;
    }

    /**
     * 获取支付结果
     * 参数json示例如下：
     * {
     * "ver": "2.0",
     * "data":{
     * "orderId":"abcf1330",
     * "gameId":123,
     * "accountId":"12221222211123",
     * "creator":"JY",
     * "payWay":1,
     * "amount":"100.00",
     * "callbackInfo":"custominfo=xxxxx#user=xxxx",
     * "orderStatus":"S",
     * "failedDesc":"",
     * "cpOrderId":"1234567"
     * },
     * "sign":"6362e564f832d2e8bbcbd50e75409d47"
     * }
     */
    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        checkInit();
        try {
            String result = getRequestString(request);
            if (StringUtils.isEmpty(result))
                return UC_PAY_RESULT_FAILED;
            //记录日志
            ChannelStatsLogger.info(ChannelStatsLogger.UC, result);

            Map<String, Object> payResult = JsonMapper.getMapper().readValue(result, new TypeReference<Map<String, Object>>() {
            });
            if (payResult.get(REQUEST_KEY_DATA) == null || StringUtils.isEmpty(payResult.get(REQUEST_KEY_SIGN).toString()))
                return UC_PAY_RESULT_FAILED;

            Map<String, Object> data = (Map<String, Object>) payResult.get(REQUEST_KEY_DATA);
            if (data == null || data.isEmpty()) {
                return UC_PAY_RESULT_FAILED;
            }
            //note穿透 OrderId
            Order order = getOrder(service, String.valueOf(data.get(PARAM_ORDER_ID).toString())
                    , String.valueOf(data.get(PARAM_CHANNEL_ORDER_ID)));
            if (order == null) {
                return UC_PAY_RESULT_FAILED;
            }
            if (basicRepository == null) {
                return UC_PAY_RESULT_FAILED;
            }
            channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            String apiKey = channelGame.getAppKey();
            String sign = Sign.signParamsByMD5(data, apiKey);
            if (sign.equals(payResult.get(REQUEST_KEY_SIGN).toString())) {
                handleOrder(payResult, order, service);
                return UC_PAY_RESULT_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UC_PAY_RESULT_FAILED;

    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }

    private void handleOrder(Map<String, Object> payResult, Order order, OrderService service) throws Exception {
        UCPayResult ucPay = Tools.mapToObject(payResult, UCPayResult.class);
        //游戏需根据orderStatus参数的值判断是否给玩家过账虚拟货币。（S为充值成功、F为充值失败，避免假卡、无效卡充值成功）
        if (RESULT_ORDER_STATUS_SUCCESS.equals(ucPay.getData().getOrderStatus())) {
//            if (Double.valueOf(ucPay.getData().getAmount()) * 100 >= order.getAmount()) {
//                service.paySuccess(order.getOrderId());
//            } else {
//                service.payFail(order.getOrderId(), "order amount error");
//                ChannelStatsLogger.error(ChannelStatsLogger.UC, order.getOrderId(), "order amount error");
//            }
            updateOrder(order, Double.valueOf(ucPay.getData().getAmount()) * 100, service);
        } else {
            service.payFail(order.getOrderId(), ucPay.getData().getFailedDesc());
        }
    }

}
