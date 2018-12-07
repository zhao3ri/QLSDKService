package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.application.platform.log.ChannelStatsLogger;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;
import com.qinglan.sdk.server.presentation.channel.entity.UCOrderSignRequest;
import com.qinglan.sdk.server.presentation.channel.entity.UCPayResult;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.qinglan.sdk.server.ChannelConstants.UC_PAY_RESULT_FAILED;
import static com.qinglan.sdk.server.ChannelConstants.UC_PAY_RESULT_SUCCESS;

public class UCChannel extends BaseChannel {
    public static final String REQUEST_KEY_SID = "sid";
    public static final String REQUEST_KEY_GAME_ID = "gameId";
    public static final String REQUEST_KEY_ID = "id";
    public static final String REQUEST_KEY_DATA = "data";
    public static final String REQUEST_KEY_GAME = "game";
    public static final String REQUEST_KEY_SIGN = "sign";
    public static final String SIGN_PREFIX = "sid=";

    public static final String PARAM_CALLBACK_INFO = "callbackInfo";
    public static final String PARAM_NOTIFY_URL = "notifyUrl";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_ORDER_ID = "cpOrderId";
    public static final String PARAM_CHANNEL_ORDER_ID = "orderId";
    public static final String PARAM_ACCOUNT_ID = "accountId";

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
     */
    @Override
    public String verifySession(String... args) {
        checkInit();

        if (null == channelGame || null == args || args.length == 0)
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
        try {
            return HttpUtils.doPostToJson(channel.getVerifyUrl(), JsonMapper.toJson(params), 10000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
            String result = getResultString(request);
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

    private String getResultString(HttpServletRequest request) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
        String ln;
        StringBuffer stringBuffer = new StringBuffer();
        while ((ln = in.readLine()) != null) {
            stringBuffer.append(ln);
            stringBuffer.append("\r\n");
        }
        return stringBuffer.toString();
    }

    private void handleOrder(Map<String, Object> payResult, Order order, OrderService service) throws Exception {
        UCPayResult ucPay = Tools.mapToObject(payResult, UCPayResult.class);
        //游戏需根据orderStatus参数的值判断是否给玩家过账虚拟货币。（S为充值成功、F为充值失败，避免假卡、无效卡充值成功）
        if ("S".equals(ucPay.getData().getOrderStatus())) {
            if (Double.valueOf(ucPay.getData().getAmount()) * 100 >= order.getAmount()) {
                service.paySuccess(order.getOrderId());
            } else {
                service.payFail(order.getOrderId(), "order amount error");
                ChannelStatsLogger.error(ChannelStatsLogger.UC, order.getOrderId(), "order amount error");
            }
        } else {
            service.payFail(order.getOrderId(), ucPay.getData().getFailedDesc());
        }
    }

}
