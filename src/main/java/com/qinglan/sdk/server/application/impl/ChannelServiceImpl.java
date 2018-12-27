package com.qinglan.sdk.server.application.impl;

import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.application.ChannelUtilsService;
import com.qinglan.sdk.server.application.log.ChannelStatsLogger;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.presentation.channel.IChannel;
import com.qinglan.sdk.server.presentation.channel.entity.*;
import com.qinglan.sdk.server.presentation.channel.impl.*;
import com.qinglan.sdk.server.application.redis.RedisUtil;
import com.qinglan.sdk.server.application.ChannelService;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.platform.YaoyueCallback;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.qinglan.sdk.server.ChannelConstants.UC_PAY_RESULT_FAILED;

@Service
public class ChannelServiceImpl implements ChannelService {

    private static final Logger logger = LoggerFactory.getLogger(ChannelServiceImpl.class);
    @Resource
    private BasicRepository basicRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private ChannelUtilsService channelUtilsService;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public String verifyYaoyue(YaoyueCallback zhidian) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (StringUtils.isEmpty(zhidian.getMerchantId()) || StringUtils.isEmpty(zhidian.getAppId()) || StringUtils.isEmpty(zhidian.getUserName())
                || StringUtils.isEmpty(zhidian.getTradeNo()) || StringUtils.isEmpty(zhidian.getChannelCode()) || StringUtils.isEmpty(zhidian.getAmount())
                || StringUtils.isEmpty(zhidian.getCreateTime()) || StringUtils.isEmpty(zhidian.getSign())) {
            map.put("statusCode", 2);
            map.put("errorMsg", "参数无效");
            map.put("tradeNo", zhidian.getTradeNo());
            return JsonMapper.toJson(map);
        }
        //记录日志
        ChannelStatsLogger.info(ChannelStatsLogger.ZHIDIAN, "merchantId:" + zhidian.getMerchantId() + " appId:" + zhidian.getAppId() + " userName:" + zhidian.getAppId()
                + " tradeNo:" + zhidian.getTradeNo() + " channelCode:" + zhidian.getChannelCode() + " amount:" + zhidian.getAmount()
                + " createTime:" + zhidian.getCreateTime() + " area:" + zhidian.getArea() + " chid:" + zhidian.getChid() +
                " note:" + zhidian.getNote() + " sign:" + zhidian.getSign());

        try {
            //note穿透 OrderId
            Order order = orderService.getOrderByOrderId(zhidian.getNote());
            if (order == null) {
                map.put("statusCode", 2);
                map.put("errorMsg", "orderId无效");
                map.put("tradeNo", zhidian.getTradeNo());
                return JsonMapper.toJson(map);
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                map.put("statusCode", 2);
                map.put("errorMsg", "orderId无效");
                map.put("tradeNo", zhidian.getTradeNo());
                return JsonMapper.toJson(map);
            }

            String signStr = Sign.encode(zhidian.getMerchantId(), zhidian.getAppId(), zhidian.getUserName()
                    , zhidian.getTradeNo(), zhidian.getChannelCode(), zhidian.getAmount(), zhidian.getCreateTime(), channelGame.getConfigParamsList().get(0));

            if (zhidian.getSign().equals(signStr)) {
                if (Double.valueOf(zhidian.getAmount()) * 100 >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.ZHIDIAN, order.getOrderId(), "order amount error");
                }
                map.put("statusCode", 0);
                map.put("errorMsg", "接收成功");
                map.put("tradeNo", zhidian.getTradeNo());
                return JsonMapper.toJson(map);
            } else {
                map.put("statusCode", 3);
                map.put("errorMsg", "签名无效");
                map.put("tradeNo", zhidian.getTradeNo());
                return JsonMapper.toJson(map);
            }
        } catch (Exception e) {
            map.put("statusCode", 4);
            map.put("errorMsg", "接收数据异常");
            map.put("tradeNo", zhidian.getTradeNo());
            ChannelStatsLogger.error(ChannelStatsLogger.ZHIDIAN, zhidian.getNote(), "zhidian verifyYaoyue error:" + e);
            return JsonMapper.toJson(map);
        }
    }

    @Override
    public String verifyUcSession(UCVerifyRequest ucSession) {
        if (ucSession.getGameId() == 0 || ucSession.getChannelId() == 0
                || StringUtils.isEmpty(ucSession.getSid()) || StringUtils.isEmpty(ucSession.getAppID())) {
            return "";
        }
        return verify(UCChannel.class, ucSession, ucSession.getSid(), ucSession.getAppID());
    }

    @Override
    public String ucPayReturn(HttpServletRequest request) {
        try {
            return payReturn(UCChannel.class,request);
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.UC, request.getQueryString(), "uc ucPayReturn error:" + e);
        }
        return UC_PAY_RESULT_FAILED;
    }

    @Override
    public String signOrderHuawei(HMSPaySignRequest request) {
        IChannel channel = new HmsChannel();
        channel.init(basicRepository, request.getGameId(), request.getChannelId());
        return channel.signOrder(request);
    }

    @Override
    public String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ChannelStatsLogger.info(ChannelStatsLogger.HMS, HttpUtils.getRequestParams(request).toString());

        String result = payReturn(HmsChannel.class, request);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("HMS Response string: " + result);
        PrintWriter out = response.getWriter();

        out.print(result);
        out.close();
        return result;
    }

    @Override
    public String verifyHuawei(HMSVerifyRequest request) {
        return verify(HmsChannel.class, request, request.getAppID(), request.getCpID(), request.getTs()
                , request.getPlayerId(), request.getPlayerLevel(), request.getPlayerSSign());
    }

    @Override
    public String yeshenPayReturn(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.YESHEN, HttpUtils.getRequestParams(request).toString());
        return payReturn(YSChannel.class, request);
    }

    @Override
    public String verifyYeshen(YSVerifyRequest request) {
        return verify(YSChannel.class, request, request.getAccessToken(), request.getUid(), request.getAppID());
    }

    @Override
    public String huoSdkPayReturn(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.YESHEN, HttpUtils.getRequestParams(request).toString());
        return payReturn(HuoSdkChannel.class, request);
    }

    @Override
    public String verifyHuoSdk(HuoSdkVerifyRequest request) {
        return verify(HuoSdkChannel.class, request, request.getAppId(), request.getMemId(), request.getUserToken());
    }

    @Override
    public String hanfengPayReturn(HttpServletRequest request) {
        return null;
    }

    @Override
    public String verifyHangfeng(HanfengVerifyRequest request) {
        return verify(HanfengChannel.class, request, JsonMapper.toJson(request));
    }

    private <T extends IChannel> String verify(Class<T> cls, BaseRequest request, String... args) {
        IChannel channel = getChannel(cls);
        if (channel == null) {
            return null;
        }
        channel.init(basicRepository, request.getGameId(), request.getChannelId());
        //顺序需相同
        String result = channel.verifySession(args);
        return result;
    }

    private <T extends IChannel> String payReturn(Class<T> cls, HttpServletRequest request) {
        IChannel channel = getChannel(cls);
        if (channel == null) {
            return null;
        }
        channel.init(basicRepository);
        String result = channel.returnPayResult(request, orderService);
        return result;
    }

    private <T extends IChannel> IChannel getChannel(Class<T> cls) {
        IChannel channel = null;
        try {
            channel = cls.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return channel;
    }
}
