package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.StringUtil;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPayResult;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.utils.HmsSignHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN;
import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN_TYPE;

public class HmsChannel extends BaseChannel {
    /**
     * 认证地址
     */
    public static final String VERIFY_URL = "/ucgame/session";
    /**
     * 支付回调地址
     */
    public static final String PAY_RETURN_URL = "/hms/pay/return";
    /**
     * 支付签名地址
     */
    public static final String PAY_SIGN_URL = "/hms/pay/sign";

    private static final String REQUEST_PARAM_METHOD = "method";
    private static final String REQUEST_PARAM_APPID = "appId";
    private static final String REQUEST_PARAM_CPID = "cpId";
    private static final String REQUEST_PARAM_TS = "ts";
    private static final String REQUEST_PARAM_PLAYER_ID = "playerId";
    private static final String REQUEST_PARAM_PLAYER_LEVEL = "playerLevel";
    private static final String REQUEST_PARAM_PLAYER_SIGN = "playerSSign";
    private static final String REQUEST_PARAM_CP_SIGN = "cpSign";

    private static final String REQUEST_PARAM_CHANNEL_ORDER_ID = "orderId";//渠道订单id
    private static final String REQUEST_PARAM_REQUEST_ID = "requestId";//订单id
    private static final String REQUEST_PARAM_EXT_RESERVED = "extReserved";
    private static final String REQUEST_PARAM_SYS_RESERVED = "sysReserved";
    private static final String REQUEST_PARAM_AMOUNT = "amount";
    private static final int RESULT_PAY_CODE_SUCCESS = 0;
    private static final int RESULT_PAY_CODE_FAIL = 1;

    @Override
    public String verifySession(String... args) {
        checkInit();
        if (null == channelGame || null == channel || null == args || args.length == 0)
            return null;
        String priKey = channelGame.getPrivateKey();
        String verUrl = channel.getVerifyUrl();
        Map<String, Object> mockRequestParams = new HashMap<>();
        mockRequestParams.put(REQUEST_PARAM_METHOD, "external.hms.gs.checkPlayerSign");
        mockRequestParams.put(REQUEST_PARAM_APPID, args[0]);
        if (StringUtil.isNullOrEmpty(args[1])) {
            String cpId = channelGame.getConfig(REQUEST_PARAM_CPID);
            args[1] = cpId;
        }
        mockRequestParams.put(REQUEST_PARAM_CPID, args[1]);
        mockRequestParams.put(REQUEST_PARAM_TS, args[2]);
        mockRequestParams.put(REQUEST_PARAM_PLAYER_ID, args[3]);
        mockRequestParams.put(REQUEST_PARAM_PLAYER_LEVEL, args[4]);
        mockRequestParams.put(REQUEST_PARAM_PLAYER_SIGN, args[5]);
        mockRequestParams.put(REQUEST_PARAM_CP_SIGN, HmsSignHelper.generateCPSign(mockRequestParams, priKey));
        return HttpUtils.post(verUrl, mockRequestParams);
    }

    @Override
    public String signOrder(BaseRequest request) {
        checkInit();
        if (request instanceof HMSPaySignRequest) {
            String sign = HmsSignHelper.sign(((HMSPaySignRequest) request).getContent(), channelGame.getPrivateKey());
            return sign;
        }
        return null;
    }

    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        Map<String, Object> params = getRequestParams(request);
        HMSPayResult result = new HMSPayResult();
        result.setResult(RESULT_PAY_CODE_FAIL);
        if (null == params || params.isEmpty()) {
            return JsonMapper.toJson(result);
        }
        Order order = getOrder(service, String.valueOf(params.get(REQUEST_PARAM_REQUEST_ID).toString())
                , String.valueOf(params.get(REQUEST_PARAM_CHANNEL_ORDER_ID)));
        if (order == null) {
            return JsonMapper.toJson(result);
        }
        channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        String sign = (String) params.get(RESPONSE_KEY_SIGN);
        String signType = (String) params.get(RESPONSE_KEY_SIGN_TYPE);
        String content = HmsSignHelper.getSignData(params);
        if (HmsSignHelper.doCheck(content, sign, channelGame.getPublicKey(), signType)) {
            result.setResult(RESULT_PAY_CODE_SUCCESS);
            updateOrder(order, Double.valueOf(String.valueOf(params.get(REQUEST_PARAM_AMOUNT))) * 100, service);
        } else {
            result.setResult(RESULT_PAY_CODE_FAIL);
            service.payFail(order.getOrderId(), "order sign error");
        }
        return JsonMapper.toJson(result);
    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }

    private Map<String, Object> getRequestParams(HttpServletRequest request) {
        String str = getRequestString(request);
        Map<String, Object> valueMap = new HashMap<String, Object>();
        if (null == str || "".equals(str)) {
            return valueMap;
        }

        String[] valueKey = str.split("&");
        for (String temp : valueKey) {
            String[] single = temp.split("=");
            valueMap.put(single[0], single[1]);
        }
        System.out.println("The parameters in map are : " + valueMap);

        //接口中，如下参数sign和extReserved是URLEncode的，所以需要decode，其他参数直接是原始信息发送，不需要decode
        try {
            String sign = (String) valueMap.get(RESPONSE_KEY_SIGN);
            String extReserved = (String) valueMap.get(REQUEST_PARAM_EXT_RESERVED);
            String sysReserved = (String) valueMap.get(REQUEST_PARAM_SYS_RESERVED);

            if (null != sign) {
                sign = URLDecoder.decode(sign, "utf-8");
                valueMap.put(RESPONSE_KEY_SIGN, sign);
            }
            if (null != extReserved) {
                extReserved = URLDecoder.decode(extReserved, "utf-8");
                valueMap.put(REQUEST_PARAM_EXT_RESERVED, extReserved);
            }

            if (null != sysReserved) {
                sysReserved = URLDecoder.decode(sysReserved, "utf-8");
                valueMap.put(REQUEST_PARAM_SYS_RESERVED, sysReserved);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueMap;
    }
}
