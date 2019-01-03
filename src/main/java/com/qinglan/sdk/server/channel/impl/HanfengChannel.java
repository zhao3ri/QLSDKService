package com.qinglan.sdk.server.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.channel.entity.BaseRequest;
import com.qinglan.sdk.server.channel.entity.HanfengVerifyRequest;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.channel.entity.HanfengPayResult;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static com.qinglan.sdk.server.ChannelConstants.HANFENG_PAY_RESULT_FAILED;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_SUCCESS;
import static com.qinglan.sdk.server.Constants.RESPONSE_CODE_VERIFY_ERROR;
import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_DATA;

public class HanfengChannel extends BaseChannel {
    /**
     * 认证地址
     */
    public static final String VERIFY_URL = "/hanfeng/session";
    /**
     * 支付回调地址
     */
    public static final String PAY_RETURN_URL = "/hanfeng/pay/return";
    private static final String SEPARATOR = "|";
    private static final String STATUS_PAY_SUCCESS = "0";
    private static final String STATUS_VERIFY_SUCCESS = "YHYZ_000";

    private static final String RESPONSE_USER_ID = "userId";
    private static final String RESPONSE_STATUS = "status";
    private static final String RESPONSE_MSG = "msg";

    /**
     * 第三方返回json示例
     * {
     * "status":	"YHYZ_000",
     * "msg":	"",
     * "userId":"dfdsr34235etdd",
     * }
     */
    @Override
    public String verifySession(String... args) {
        checkInit();
        if (null == channelGame || null == channel || null == args || args.length == 0)
            return null;
        HanfengVerifyRequest request = JsonMapper.toObject(args[0], HanfengVerifyRequest.class);
        String preSign = request.getAppId() + SEPARATOR + request.getChannel()
                + SEPARATOR + request.getUserId() + SEPARATOR + request.getSid()
                + SEPARATOR + request.getVersion() + SEPARATOR + channelGame.getAppKey();
        String sign = MD5.encode(preSign.getBytes());
        if (StringUtils.isEmpty(sign)) {
            return null;
        }
        request.setSign(sign);
        String verifyUrl = channel.getVerifyUrl();
        int code = RESPONSE_CODE_VERIFY_ERROR;
        String message = "";
        String userId = "";
        try {
            String result = HttpUtils.post(verifyUrl, JsonMapper.toJson(request));
            Map<String, Object> responseParams = getResponseParams(result);
            String status = String.valueOf(responseParams.get(RESPONSE_STATUS));
            if (status.equals(STATUS_VERIFY_SUCCESS)) {
                code = RESPONSE_CODE_SUCCESS;
                userId = String.valueOf(responseParams.get(RESPONSE_USER_ID));
            }
            message = String.valueOf(responseParams.get(RESPONSE_MSG));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> res = getResult(code, message);
        if (!StringUtils.isEmpty(userId)) {
            Map<String, Object> data = new HashMap<>();
            addData(data, RESPONSE_USER_ID, userId);
            res.put(RESPONSE_KEY_DATA, data);
        }
        return JsonMapper.toJson(res);
    }

    @Override
    public String signOrder(BaseRequest request) {
        return null;
    }

    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        String str = getRequestString(request);
        if (StringUtils.isEmpty(str)) {
            return HANFENG_PAY_RESULT_FAILED;
        }
        HanfengPayResult result = JsonMapper.toObject(str, HanfengPayResult.class);
        if (result == null) {
            return HANFENG_PAY_RESULT_FAILED;
        }
        Order order = getOrder(service, result.getCpTradeNo(), "");
        if (order == null) {
            return HANFENG_PAY_RESULT_FAILED;
        }
        channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        String preSign = result.getCpTradeNo() + SEPARATOR + result.getAppId() + SEPARATOR
                + result.getUserId() + SEPARATOR + result.getRoleId() + SEPARATOR
                + result.getServerId() + SEPARATOR + result.getChannel() + SEPARATOR
                + result.getItemId() + SEPARATOR + result.getItemAmount() + SEPARATOR
                + result.getPrivateField() + SEPARATOR + result.getMoney() + SEPARATOR
                + result.getStatus() + SEPARATOR + channelGame.getAppKey();
        String sign = MD5.encode(preSign.getBytes());
        if (sign.equals(result.getSign()) && result.getUserId().equals(order.getUid()) && result.getRoleId().equals(order.getRoleId())) {
            if (result.getStatus().equals(STATUS_PAY_SUCCESS)) {
                updateOrder(order, result.getFee() * 100, service);
                return result.getCpTradeNo();
            } else {
                service.payFail(order.getOrderId(), result.getStatus());
            }
        }
        return HANFENG_PAY_RESULT_FAILED;
    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }
}
