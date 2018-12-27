package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HanfengPayResult;
import com.qinglan.sdk.server.presentation.channel.entity.HanfengVerifyRequest;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.qinglan.sdk.server.ChannelConstants.HANFENG_PAY_RESULT_FAILED;

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

    /**
     * 返回json示例
     * {
     *      "status":	"YHYZ_000",
     *      "msg":	"",
     *      "userId":"dfdsr34235etdd",
     * }
     * */
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
        try {
            return HttpUtils.post(verifyUrl, JsonMapper.toJson(request));
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
        if (result.getStatus().equals(STATUS_PAY_SUCCESS)) {
            if (sign.equals(result.getSign()) && result.getUserId().equals(order.getUid()) && result.getRoleId().equals(order.getRoleId())) {
                updateOrder(order, result.getFee() * 100, service);
            }
            return result.getCpTradeNo();
        } else {
            service.payFail(order.getOrderId(), result.getStatus());
        }
        return HANFENG_PAY_RESULT_FAILED;
    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }
}
