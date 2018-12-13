package com.qinglan.sdk.server.application.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lenovo.pay.sign.CpTransSyncSignValid;
import com.lenovo.pay.sign.JsonUtil;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.application.ChannelUtilsService;
import com.qinglan.sdk.server.application.log.ChannelStatsLogger;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.domain.basic.ChannelEntity;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.presentation.dto.channel.*;
import com.qinglan.sdk.server.utils.channel.qq.JSONException;
import com.qinglan.sdk.server.presentation.channel.IChannel;
import com.qinglan.sdk.server.presentation.channel.entity.UCVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.impl.UCChannel;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.uudev.payment.cloud.util.DesUtils;
import com.qinglan.sdk.server.utils.channel.utils.HMACSHA1;
import com.qinglan.sdk.server.utils.channel.utils.HMacMD5;
import com.qinglan.sdk.server.utils.channel.fansdk.FansdkSigleUtils;
import com.qinglan.sdk.server.utils.channel.fansdk.UOrder;
import com.qinglan.sdk.server.utils.channel.hongshouzhi.HongShouZhiUtil;
import com.qinglan.sdk.server.utils.channel.lewan.util.encrypt.AES;
import com.qinglan.sdk.server.utils.channel.lewan.util.encrypt.EncryUtil;
import com.qinglan.sdk.server.utils.channel.lewan.util.encrypt.RSA;
import com.qinglan.sdk.server.utils.channel.qq.JSONObject;
import com.qinglan.sdk.server.utils.channel.qq.OpenApiV3;
import com.qinglan.sdk.server.utils.channel.qq.OpensnsException;
import com.qinglan.sdk.server.utils.channel.qqmssdk.MsOpenApiV3;
import com.qinglan.sdk.server.utils.channel.quicksdk.IOSDesUtil;
import com.qinglan.sdk.server.utils.channel.quicksdk.QuickXmlBean;
import com.qinglan.sdk.server.utils.channel.quicksdk.XmlUtils;
import com.qinglan.sdk.server.utils.channel.xiao7.VerifyXiao7;
import com.qinglan.sdk.server.Constants;
import com.qinglan.sdk.server.application.redis.RedisUtil;
import com.qinglan.sdk.server.application.ChannelService;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.platform.MMYPayResult;
import com.qinglan.sdk.server.domain.platform.YaoyueCallback;
import com.qinglan.sdk.server.domain.platform.YouleCallback;
import egame.openapi.common.RequestParasUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

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
        if (ucSession.getGameId() == 0 || ucSession.getPlatformId() == 0
                || StringUtils.isEmpty(ucSession.getSid()) || StringUtils.isEmpty(ucSession.getAppID())) {
            return "";
        }
        IChannel channel = getChannel(UCChannel.class);
        if (channel == null) {
            return "";
        }
        channel.init(basicRepository, ucSession.getGameId(), ucSession.getPlatformId());
        String result = channel.verifySession(ucSession.getSid(), ucSession.getAppID());
        return result;
//        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(ucSession.getChannelId()), Long.valueOf(ucSession.getGameId()));
//        if (channelGame == null) return "";
//        String apiKey = channelGame.getConfigParamsList().get(0);
//        String toUrl = channelGame.getConfigParamsList().get(1);
//        Map<String, Object> map = new HashMap<String, Object>();
//        Map<String, Object> mapData = new HashMap<String, Object>();
//        Map<String, Object> mapGame = new HashMap<String, Object>();
//        mapData.put("sid", ucSession.getSid());
//        mapGame.put("gameId", ucSession.getAppID());
//        map.put("id", System.currentTimeMillis());
//        map.put("data", mapData);
//        map.put("game", mapGame);
//        map.put("sign", MD5.encode("sid=" + ucSession.getSid() + apiKey));
//        try {
//            return HttpUtils.doPostToJson(toUrl, JsonMapper.toJson(map), 10000);
//        } catch (Exception e) {
//            logger.error("uc verifyUcSession error", e);
//        }
//        return "";
    }

    @Override
    public String ucPayReturn(HttpServletRequest request) {
        try {
            IChannel channel = getChannel(UCChannel.class);
            channel.init(basicRepository);
            String result = channel.returnPayResult(request, orderService);
            return result;
//
//            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
//            if (channelGame == null) return UC_PAY_RESULT_FAILED;
//            String apiKey = channelGame.getConfigParamsList().get(0);
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("amount", ucgameObj.getData().getAmount());
//            map.put("accountId", ucgameObj.getData().getAccountId());
//            map.put("callbackInfo", ucgameObj.getData().getCallbackInfo());
//            map.put("cpOrderId", ucgameObj.getData().getChannelOrderId());
//            map.put("creator", ucgameObj.getData().getCreator());
//            map.put("failedDesc", ucgameObj.getData().getFailedDesc());
//            map.put("gameId", ucgameObj.getData().getGameId());
//            map.put("orderId", ucgameObj.getData().getOrderId());
//            map.put("orderStatus", ucgameObj.getData().getOrderStatus());
//            map.put("payWay", ucgameObj.getData().getPayWay());
//            String signStr = Sign.signParamsByMD5(map, apiKey);
//            if (signStr.equals(ucgameObj.getSign())) {
//                if ("S".equals(ucgameObj.getData().getOrderStatus())) {
//                    if (Double.valueOf(ucgameObj.getData().getAmount()) * 100 >= order.getAmount()) {
//                        orderService.paySuccess(order.getOrderId());
//                    } else {
//                        orderService.payFail(order.getOrderId(), "order amount error");
//                        ChannelStatsLogger.error(ChannelStatsLogger.UC, order.getOrderId(), "order amount error");
//                    }
//                } else {
//                    orderService.payFail(order.getOrderId(), ucgameObj.getData().getFailedDesc());
//                }
//                return UC_PAY_RESULT_SUCCESS;
//            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.UC, request.getQueryString(), "uc ucPayReturn error:" + e);
        }
        return UC_PAY_RESULT_FAILED;
    }

    @Override
    public String verifyXiaomiSession(XiaomiSession xiaomiSession) {
        if (StringUtils.isEmpty(xiaomiSession.getZdappId()) || StringUtils.isEmpty(xiaomiSession.getPlatformId())
                || StringUtils.isEmpty(xiaomiSession.getUid()) || StringUtils.isEmpty(xiaomiSession.getSession())
                || StringUtils.isEmpty(xiaomiSession.getAppId())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(xiaomiSession.getPlatformId()), Long.valueOf(xiaomiSession.getZdappId()));
        if (channelGame == null) return "";
        String secretKey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);

        Map<String, String> params = new HashMap<String, String>();
        params.put("appId", xiaomiSession.getAppId());
        params.put("uid", xiaomiSession.getUid());
        params.put("session", xiaomiSession.getSession());

        try {
            return HttpUtils.get(channelUtilsService.getRequestUrlXiaomi(params, verifySessionUrl, secretKey), 10000);
        } catch (Exception e) {
            logger.error("xiaomi verifyXiaomiSession error", e);
        }
        return "";
    }

    @Override
    public String verifyXiaomi(HttpServletRequest request) {
        Map<String, Integer> jsonMap = new HashMap<String, Integer>();
        //记录日志
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.XIAOMI, requestValue);
        //note穿透 cpOrderId
        Order order = orderService.getOrderByOrderId(request.getParameter("cpOrderId"));
        if (order == null) {
            jsonMap.put("errcode", 1506);
            return JsonMapper.toJson(jsonMap);
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            jsonMap.put("errcode", 3515);
            return JsonMapper.toJson(jsonMap);
        }
        String secretKey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, String> signParams = channelUtilsService.getSignParamsXiaomi(request.getQueryString());
            String tmpSign = channelUtilsService.getSignXiaomi(signParams, secretKey);
            String sign = request.getParameter("signature");
            if (tmpSign.equals(sign)) {
                if ("TRADE_SUCCESS".equals(request.getParameter("orderStatus"))) {
                    if (Integer.valueOf(request.getParameter("payFee")) >= order.getAmount()) {
                        orderService.paySuccess(order.getOrderId());
                    } else {
                        orderService.payFail(order.getOrderId(), "order amount error");
                        ChannelStatsLogger.error(ChannelStatsLogger.XIAOMI, order.getOrderId(), "order amount error");
                    }
                } else {
                    orderService.payFail(order.getOrderId(), "订单支付失败");
                }
                jsonMap.put("errcode", 200);
            } else {
                jsonMap.put("errcode", 1525);
            }
        } catch (Exception e) {
            jsonMap.put("errcode", 1001);
            ChannelStatsLogger.error(ChannelStatsLogger.XIAOMI, requestValue, "xiaomi verifyXiaomi error:" + e);
        }
        return JsonMapper.toJson(jsonMap);

    }

    @Override
    public String verifyQihooSession(QihooSession qihooSession) {
        if (StringUtils.isEmpty(qihooSession.getZdappId()) || StringUtils.isEmpty(qihooSession.getPlatformId())
                || StringUtils.isEmpty(qihooSession.getAccess_token())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(qihooSession.getPlatformId()), Long.valueOf(qihooSession.getZdappId()));
        if (channelGame == null)
            return "";
        String verifySessionUrl = channelGame.getConfigParamsList().get(2);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", qihooSession.getAccess_token());
        params.put("fields", "id,name,avatar,sex,area,nick");
        try {
            return HttpUtils.postToHttps(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("qihoo verifyqihooSession error", e);
        }
        return "";
    }

    @Override
    @SuppressWarnings("unchecked")
    public String verifyQihoo(HttpServletRequest request) {
        Map<String, String[]> paramterMap = request.getParameterMap();
        HashMap<String, String> params = new HashMap<String, String>();
        String k, v;
        Iterator<String> iterator = paramterMap.keySet().iterator();
        while (iterator.hasNext()) {
            k = iterator.next();
            String arr[] = paramterMap.get(k);
            v = (String) arr[0];
            params.put(k, v);
        }

        //记录日志
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.QIHOO, requestValue);
        try {
            //note穿透 cpOrderId
            Order order = orderService.getOrderByOrderId(params.get("app_order_id"));
            if (order == null) return "not my order";

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) return "verify failed";

            String appkey = channelGame.getConfigParamsList().get(0);
            String appsecret = channelGame.getConfigParamsList().get(1);
            if (!channelUtilsService.isValidRequestQihoo(params, appkey, appsecret)) {
                return "invalid request";
            }
            //判断是否成功
            if ("success".equals(params.get("gateway_flag"))) {
                if (Integer.valueOf(params.get("amount")) >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.QIHOO, order.getOrderId(), "order amount error");
                }
            } else {
                orderService.payFail(order.getOrderId(), "订单支付失败");
            }
            return "ok";
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.QIHOO, requestValue, "qihoo verifyQihoo error:" + e);
        }
        return "invalid request";
    }

    @Override
    public String verifyBaiduSession(BaiduSession baiduSession) {
        if (StringUtils.isEmpty(baiduSession.getZdappId()) || StringUtils.isEmpty(baiduSession.getPlatformId())
                || StringUtils.isEmpty(baiduSession.getAccessToken()) || StringUtils.isEmpty(baiduSession.getAppId())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(baiduSession.getPlatformId()), Long.valueOf(baiduSession.getZdappId()));
        if (channelGame == null)
            return "";
        String secretKey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);
        String md5Key = Sign.encode(baiduSession.getAppId(), baiduSession.getAccessToken(), secretKey);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("AppId", baiduSession.getAppId());
        params.put("AccessToken", baiduSession.getAccessToken());
        params.put("Sign", md5Key);
        try {
            return HttpUtils.post(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("baidu verifyBaiduSession error", e);
        }
        return "";
    }

    @Override
    public String verifyBaidu(HttpServletRequest request) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String appid = request.getParameter("AppID");
        String orderSerial = request.getParameter("OrderSerial");//SDK 系统内部订单号
        String cooperatorOrderSerial = request.getParameter("CooperatorOrderSerial");//CP 订单号
        String content = request.getParameter("Content");//Content通过Request读取的数据已经自动解码
        String sign = request.getParameter("Sign");
        //记录日志
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.BAIDU, requestValue);
        if (appid == null || orderSerial == null || cooperatorOrderSerial == null || content == null || sign == null) {
            jsonMap.put("ResultCode", 4);
            jsonMap.put("ResultMsg", "参数错误");
            return JsonMapper.toJson(jsonMap);
        }
        try {
            //note穿透 cpOrderId
            Order order = orderService.getOrderByOrderId(cooperatorOrderSerial);
            if (order == null) {
                jsonMap.put("ResultCode", 4);
                jsonMap.put("ResultMsg", "参数错误");
                return JsonMapper.toJson(jsonMap);
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                jsonMap.put("ResultCode", 4);
                jsonMap.put("ResultMsg", "参数错误");
                return JsonMapper.toJson(jsonMap);
            }
            String secretKey = channelGame.getConfigParamsList().get(0);
            //先对接收到的通知进行验证
            StringBuilder strSign = new StringBuilder();
            strSign.append(appid);
            strSign.append(orderSerial);
            strSign.append(cooperatorOrderSerial);
            strSign.append(content);
            strSign.append(secretKey);
            if (MD5.encode(strSign.toString()).equals(sign)) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> objMap = JsonMapper.toObject(Base64.decode(content), HashMap.class);
                if (objMap == null) return null;
                Integer orderStatus = (Integer) objMap.get("OrderStatus");
                if (orderStatus == 1) {
                    if (Double.valueOf((String) objMap.get("OrderMoney")) * 100 >= order.getAmount()) {
                        orderService.paySuccess(order.getOrderId());
                    } else {
                        orderService.payFail(order.getOrderId(), "order amount error");
                        ChannelStatsLogger.error(ChannelStatsLogger.BAIDU, order.getOrderId(), "order amount error");
                    }
                } else {
                    orderService.payFail(order.getOrderId(), "订单支付失败");
                }
                jsonMap.put("ResultCode", 1);
                jsonMap.put("ResultMsg", "成功");
                jsonMap.put("Sign", MD5.encode(appid + 1 + secretKey));
            } else {
                jsonMap.put("ResultCode", 3);
                jsonMap.put("ResultMsg", "Sign无效");
                jsonMap.put("Sign", MD5.encode(appid + 3 + secretKey));
            }
        } catch (Exception e) {
            jsonMap.put("ResultCode", 2);
            jsonMap.put("ResultMsg", "失败");
            ChannelStatsLogger.error(ChannelStatsLogger.BAIDU, requestValue, "baidu verifyBaidu error:" + e);
        }
        return JsonMapper.toJson(jsonMap);
    }

    @Override
    public String verifyAnzhiSession(AnzhiSession anzhiSession) {
        if (StringUtils.isBlank(anzhiSession.getSid())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(anzhiSession.getPlatformId()), Long.valueOf(anzhiSession.getZdappId()));
        if (channelGame == null)
            return "";

        String appkey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("time", DateUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
        params.put("appkey", appkey);
        params.put("account", anzhiSession.getAccount());
        params.put("sid", anzhiSession.getSid());
        params.put("sign", Base64.encode(appkey + anzhiSession.getAccount() + anzhiSession.getSid()) + "appsecret");

        try {
            return HttpUtils.post(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("verifyAnzhiSession http post error", e);
        }
        return "";
    }

    @Override
    public String verifyAnzhi(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            ChannelStatsLogger.error(ChannelStatsLogger.ANZHI, "", "verifyAnzhi error: id is null");
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(1004, Long.valueOf(id));
        if (channelGame == null) {
            return "error";
        }

        String appsecret = channelGame.getConfigParamsList().get(2);

        String data = Des3Util.decrypt(request.getParameter("data"), appsecret);

        ChannelStatsLogger.info(ChannelStatsLogger.ANZHI, data);
        try {
            AnzhiPayCallback back = JsonMapper.toObject(data, AnzhiPayCallback.class);
            Order order = orderService.getOrderByOrderId(back.getCpInfo());
            if (order == null) {
                return "error";
            }
            channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "error";
            }
            if (1 == back.getCode()) {
                if ((null == back.getRedBagMoney() && back.getOrderAmount() >= order.getAmount())
                        || (null != back.getRedBagMoney() && back.getRedBagMoney() + back.getOrderAmount() >= order.getAmount())) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.ANZHI, order.getOrderId(), "order amount error");
                }
            } else {
                orderService.payFail(order.getOrderId(), "订单支付失败");
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.ANZHI, data, "anzhi verifyAnzhi error:" + e);
            return "error";
        }
        return "success";
    }

    @Override
    public String verifyWdjSession(WdjSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getUid())
                || StringUtils.isBlank(session.getToken())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";

        String appKeyId = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", session.getUid());
        params.put("token", session.getToken());
        params.put("appkey_id", appKeyId);
        try {
            return HttpUtils.post(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("verifyWdjSession error", e);
        }
        return "";
    }

    @Override
    public String verifyWdj(HttpServletRequest request) {
        String content = request.getParameter("content");
        String sign = request.getParameter("sign");
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.WDJ, requestValue);
        if (StringUtils.isBlank(content) || StringUtils.isBlank(sign)) {
            return "fail";
        }
        try {
            WdjPayCallback callback = JsonMapper.toObject(content, WdjPayCallback.class);
            if (null == callback) {
                return "fail";
            }

            Order order = orderService.getOrderByOrderId(callback.getOut_trade_no());
            if (null == order) {
                return "fail";
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "fail";
            }
            String publicKey = channelGame.getConfigParamsList().get(2);
            if (channelUtilsService.vaildWdjSign(content, sign, publicKey)) {
                if (callback.getMoney() >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.WDJ, order.getOrderId(), "order amount error");
                }
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.WDJ, requestValue, "wdj verifyWdj error:" + e);
            return "fail";
        }
    }

    @Override
    public String verifyDownjoy(HttpServletRequest request) {
        String result = request.getParameter("result");
        String money = request.getParameter("money");
        String orderNo = request.getParameter("order");
        String memberId = request.getParameter("mid");
        String dateTime = request.getParameter("time");
        String signature = request.getParameter("signature");
        String transno = request.getParameter("ext");// 调用支付接口时传递进入的

        //记录日志
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.DOWNJOY, requestValue);

        if (StringUtils.isEmpty(result) || StringUtils.isEmpty(transno)) {
            return "failure";
        }
        Order order = basicRepository.getOrderByOrderId(transno);
        if (null == order) {
            return "failure";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(
                order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "failure";
        }

        String paymentKey = channelGame.getConfigParamsList().get(0);
        ; // 12位支付密钥,当乐分配
        // 字符串组成顺序不能变。
        StringBuffer sb = new StringBuffer();
        sb.append("order=").append(orderNo)
                .append("&money=").append(money)
                .append("&mid=").append(memberId)
                .append("&time=").append(dateTime)
                .append("&result=").append(result)
                .append("&ext=").append(transno);
        // 生成签名。
        String sig = MD5.encode(sb.toString() + "&key=" + paymentKey).toLowerCase();
        try {
            if (result != null && sig.equalsIgnoreCase(signature)) { // 验证通过
                if ("1".equals(result)) {
                    if (Double.valueOf(money) * 100 >= order.getAmount()) {
                        orderService.paySuccess(order.getOrderId());
                    } else {
                        orderService.payFail(order.getOrderId(), "order amount error");
                        ChannelStatsLogger.error(ChannelStatsLogger.DOWNJOY, order.getOrderId(), "order amount error");
                    }
                } else {
                    orderService.payFail(order.getOrderId(), "订单支付失败");
                }
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.DOWNJOY, requestValue, "downjoy verifyDownjoy error:" + e);
            return "failure";
        }
    }

    @Override
    public String verifySougouSession(SougouSession sougouSession) {
        if (StringUtils.isBlank(sougouSession.getGid()) || StringUtils.isBlank(sougouSession.getPlatformId()) || StringUtils.isBlank(sougouSession.getSession_key())
                || StringUtils.isBlank(sougouSession.getUser_id()) || StringUtils.isBlank(sougouSession.getGid())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(sougouSession.getPlatformId()), Long.valueOf(sougouSession.getZdappId()));
        if (channelGame == null)
            return "";

        String secretKey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(2);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("gid", sougouSession.getGid());
        params.put("user_id", sougouSession.getUser_id());
        params.put("session_key", sougouSession.getSession_key());
        String auth = Sign.signByMD5(params, secretKey);
        params.put("auth", auth);
        try {
            return HttpUtils.post(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("baidu verifySougouSession error", e);
        }
        return "";
    }

    public String verifySougou(HttpServletRequest request) {
        String appdata = request.getParameter("appdata");
        String auth = request.getParameter("auth");
        //记录日志
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.SOUGOU, requestValue);
        try {
            Order order = orderService.getOrderByOrderId(appdata);
            if (order == null) {
                return "ERR_100";
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "ERR_100";
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("amount1", request.getParameter("amount1"));
            params.put("amount2", request.getParameter("amount2"));
            params.put("date", request.getParameter("date"));
            params.put("appdata", appdata);
            params.put("gid", request.getParameter("gid"));
            params.put("sid", request.getParameter("sid"));
            params.put("oid", request.getParameter("oid"));
            params.put("realAmount", request.getParameter("realAmount"));
            params.put("role", request.getParameter("role"));
            params.put("time", request.getParameter("time"));
            params.put("uid", request.getParameter("uid"));
            String signAuth = Sign.signByMD5(params, "&" + channelGame.getConfigParamsList().get(1));
            if (StringUtils.equals(auth, signAuth)) {
                if (Integer.valueOf(request.getParameter("realAmount")) * 100 >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.SOUGOU, order.getOrderId(), "order amount error");
                }
                return "OK";
            } else {
                return "ERR_200";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.SOUGOU, requestValue, "sougou verifySougou error:" + e);
            return "ERR_500";
        }
    }

    @Override
    public String verifyKupaiSession(KupaiSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getCode()) || StringUtils.isBlank(session.getAppId())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";

        String appKey = channelGame.getConfigParamsList().get(0);
        String url = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", session.getAppId());
        params.put("client_secret", appKey);
        params.put("code", session.getCode());
        params.put("redirect_uri", appKey);
        try {
            return HttpUtils.postToHttps(url, params);
        } catch (Exception e) {
            logger.error("verifyKupaiSession error", e);
        }
        return "";
    }

    @Override
    public String verifyKupai(HttpServletRequest request) {
        String transdata = request.getParameter("transdata");
        String sign = request.getParameter("sign");
        String signtype = request.getParameter("signtype");

        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.KUPAI, requestValue);
        try {
            KupaiPayCallback callback = JsonMapper.toObject(transdata, KupaiPayCallback.class);
            if (StringUtils.isBlank(transdata) || StringUtils.isBlank(sign) || StringUtils.isBlank(signtype) || null == callback) {
                return "FAILURE";
            }

            String orderNo = callback.getCporderid();
            if (StringUtils.isBlank(orderNo)) {
                return "FAILURE";
            }
            Order order = basicRepository.getOrderByOrderId(orderNo);
            if (null == order) {
                return "FAILURE";
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "FAILURE";
            }
            String publicKey = channelGame.getConfigParamsList().get(2);
            if (channelUtilsService.verifyKupai(transdata, sign, publicKey)) {
                if (callback.getResult() == null || callback.getResult() == 1) {
                    orderService.payFail(order.getOrderId(), "callback notify order payfail");
                    return "SUCCESS";
                }

                if (callback.getMoney() * 100 >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.KUPAI, order.getOrderId(), "order amount error");
                }
                return "SUCCESS";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.KUPAI, requestValue, "kupai verifyKupai error:" + e);
            return "FAILURE";
        }
        return "FAILURE";
    }

    @Override
    public String verifyOppoSession(OppoSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getSsoid())) {
            return "";
        }
        try {
            String token = URLEncoder.encode(session.getToken(), "UTF-8");
            String ssoid = URLEncoder.encode(session.getSsoid(), "UTF-8");

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
            if (channelGame == null)
                return "";

            String url = channelGame.getConfigParamsList().get(1);
            String appkey = channelGame.getConfigParamsList().get(2);
            String appSecret = channelGame.getConfigParamsList().get(3);
            url = url + "?fileId=" + ssoid + "&token=" + token;

            logger.debug("url: {}", url);

            StringBuilder baseStr = new StringBuilder();
            baseStr.append("oauthConsumerKey=").append(URLEncoder.encode(appkey, "UTF-8"))
                    .append("&oauthToken=").append(token)
                    .append("&oauthSignatureMethod=").append(URLEncoder.encode("HMAC-SHA1", "UTF-8"))
                    .append("&oauthTimestamp=").append(System.currentTimeMillis())
                    .append("&oauthNonce=").append(System.currentTimeMillis())
                    .append("&oauthVersion=").append("1.0").append("&");

            logger.debug("baseStr: {}", baseStr);

            String sign = URLEncoder.encode(String.valueOf(Base64.encode(HMACSHA1.HmacSHA1Encrypt(baseStr.toString(), appSecret + "&"))), "UTF-8");
            logger.debug("sign: {}", sign);

            Map<String, String> headerParams = new HashMap<String, String>();
            headerParams.put("param", baseStr.toString());
            headerParams.put("oauthSignature", sign);

            return HttpUtils.get(url, headerParams);
        } catch (Exception e) {
            logger.error("verifyOppoSession exception", e);
            return "";
        }
    }

    @Override
    public String verifyOppo(HttpServletRequest request) {
        String format = "result=%s&resultMsg=%s";

        //记录日志
        String requestValue = getRequestKeyValue(request);
        ChannelStatsLogger.info(ChannelStatsLogger.OPPO, requestValue);

        String partnerOrder = request.getParameter("partnerOrder");
        String sign = request.getParameter("sign");
        try {
            if (StringUtils.isBlank(sign) || StringUtils.isBlank(partnerOrder)) {
                return String.format(format, "FAIL", "partnerOrder or sign is empty");
            }
            Order order = orderService.getOrderByOrderId(partnerOrder);
            if (null == order) {
                return String.format(format, "FAIL", "can not find order : orderNO: " + partnerOrder);
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                return String.format(format, "FAIL", "this is an invalid order");
            }
            String key = channelGame.getConfigParamsList().get(0);

            if (channelUtilsService.validOppoSign(request, key)) {
                if (Integer.valueOf(request.getParameter("price")) >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.OPPO, order.getOrderId(), "order amount error");
                }
                return String.format(format, "OK", "SUCCESS");
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.OPPO, requestValue, "verifyOppo exception: " + e);
            return String.format(format, "FAIL", "service exception");
        }
        return String.format(format, "FAIL", "service exception");
    }

    @Override
    public String verify91Session(Varify91Session session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getSessionId())
                || StringUtils.isBlank(session.getAppId()) || StringUtils.isBlank(session.getUin())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";


        String appKey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("AppId", session.getAppId());
        params.put("Act", "4");
        params.put("Uin", session.getUin());
        params.put("SessionId", session.getSessionId());
        params.put("Sign", Sign.encode(session.getAppId(), "4", session.getUin(), session.getSessionId(), appKey));
        try {
            return HttpUtils.post(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("verifySougouSession error", e);
        }
        return "";
    }

    @Override
    public String verify91(HttpServletRequest request) {
        Map<String, String> params = HttpUtils.getRequestParams(request);
        ChannelStatsLogger.info(ChannelStatsLogger.JIUYAO, params.toString());

        Map<String, String> result = new HashMap<String, String>();

        Order order = orderService.getOrderByOrderId(params.get("CooOrderSerial").toString());
        if (order == null) {
            result.put("ErrorCode", "4");
            result.put("ErrorDesc", "商户订单号不存在");
            return JsonMapper.toJson(result);
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("ErrorCode", "0");
            result.put("ErrorDesc", "平台游戏未关联！");
            return JsonMapper.toJson(result);
        }
        try {
            String sign = params.get("Sign").toString();
            params.remove("Sign");
            String authSign = Sign.encode(params);
            if (StringUtils.equals(sign, authSign)) {
                orderService.paySuccess(order.getOrderId());
                result.put("ErrorCode", "1");
                result.put("ErrorDesc", "success");
                return JsonMapper.toJson(result);
            } else {
                result.put("ErrorCode", "5");
                result.put("ErrorDesc", "sign unvalid");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.JIUYAO, params.toString(), "verify91 error:" + e);
            result.put("ErrorCode", "3");
            result.put("ErrorDesc", "server exception");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyGioneeSession(GioneeSession session) {
        String port = "443";
        String host = "id.gionee.com";
        String url = "/account/verify.do";
        String method = "POST";

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getAmigoToken())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";

        String apiKey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);
        String secretKey = channelGame.getConfigParamsList().get(2);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", apiKey);
        params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("nonce", UUID.randomUUID());
        params.put("mac", channelUtilsService.getGioneeMac(host, port, secretKey, params.get("ts").toString(), params.get("nonce").toString(), method, url));
        params.put("AmigoToken", session.getAmigoToken());
        try {
            return HttpUtils.postToHttps(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("verifyGioneeSession error", e);
        }
        return "";
    }

    @Override
    public String gioneeOrderCreate(HttpServletRequest request) {
        logger.debug(HttpUtils.getRequestParams(request).toString());

        String orderId = request.getParameter("orderId");
        String goodsName = request.getParameter("goodsName");
        String playerId = request.getParameter("playerId");

        Order order = orderService.getOrderByOrderId(orderId);
        if (null == order) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        ChannelEntity channel = basicRepository.getChannel(order.getChannelId());
        if (channelGame == null || channel == null) {
            return "";
        }
        String notifyUrl = channel.getChannelCallbackUrl();
        String apiKey = channelGame.getConfigParamsList().get(0);
        String privateKey = channelGame.getConfigParamsList().get(3);
        String orderCreateUrl = channelGame.getConfigParamsList().get(5);

        DecimalFormat format = new DecimalFormat("0.00");

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("player_id", playerId);
            params.put("api_key", apiKey);
            params.put("deal_price", format.format((double) order.getAmount() / 100));
            params.put("deliver_type", "1");
            params.put("notify_url", notifyUrl);
            params.put("out_order_no", orderId);
            params.put("subject", goodsName);
            params.put("submit_time", DateUtils.format(new Date(), DateUtils.yyyyMMddHHmmss).toString());
            params.put("total_fee", format.format((double) order.getAmount() / 100));
            params.put("sign", channelUtilsService.getOrderCreateSign(params, privateKey));

            String result = HttpUtils.post(orderCreateUrl, JsonMapper.toJson(params));
            logger.debug("gionee order create result : " + result);
            return result;
        } catch (Exception e) {
            logger.error("gioneeOrderCreate error", e);
        }
        return "";
    }

    @Override
    public String verifyGionee(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.GIONEE, HttpUtils.getRequestParams(request).toString());

        String orderId = request.getParameter("out_order_no");
        String sign = request.getParameter("sign");
        Order order = orderService.getOrderByOrderId(orderId);
        if (null == order) {
            return "fail";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "fail";
        }
        String publicKey = channelGame.getConfigParamsList().get(4);
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", request.getParameter("api_key"));
        params.put("close_time", request.getParameter("close_time"));
        params.put("create_time", request.getParameter("create_time"));
        params.put("deal_price", request.getParameter("deal_price"));
        params.put("out_order_no", orderId);
        params.put("pay_channel", request.getParameter("pay_channel"));
        params.put("submit_time", request.getParameter("submit_time"));
        params.put("user_id", request.getParameter("user_id"));
        try {
            if (channelUtilsService.validGioneeSign(params, publicKey, sign)) {
                if (order.getAmount() > Double.valueOf(request.getParameter("deal_price")) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.GIONEE, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "success";
                }

                orderService.paySuccess(order.getOrderId());
                return "success";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.GIONEE, HttpUtils.getRequestParams(request).toString(), "verifyGionee error:" + e);
            return "fail";
        }
        return "fail";
    }

    @Override
    public String verifyVivoSession(VivoSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getAuthtoken())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";

        String validUrl = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", session.getAuthtoken());

        try {
            return HttpUtils.postToHttps(validUrl, params);
        } catch (Exception e) {
            logger.error("verifyVivoSession error", e);
        }
        return "";
    }

    @Override
    public String vivoPaySign(VivoPaySign vivoPaySign) {
        logger.debug(vivoPaySign.toString());
        Order order = basicRepository.getOrderByOrderId(vivoPaySign.getCpOrderNumber());
        if (order == null) {
            return "";
        }
        ChannelEntity channel = basicRepository.getChannel(order.getChannelId());
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());

        String cpKey = channelGame.getConfigParamsList().get(0);
        String signUrl = channelGame.getConfigParamsList().get(2);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("version", "1.0.0");
        params.put("cpId", vivoPaySign.getCpId());
        params.put("appId", vivoPaySign.getAppId());
        params.put("cpOrderNumber", vivoPaySign.getCpOrderNumber());
        params.put("notifyUrl", channel.getChannelCallbackUrl());
        params.put("orderTime", DateUtils.toStringDate(new Date()));
        params.put("orderAmount", order.getAmount());
        params.put("orderTitle", vivoPaySign.getOrderTitle());
        params.put("orderDesc", vivoPaySign.getOrderDesc());
        params.put("extInfo", StringUtils.isBlank(vivoPaySign.getExtInfo()) ? "normal notes" : vivoPaySign.getExtInfo());
        params.put("signature", Sign.signByMD5(params, "&" + MD5.encode(cpKey).toLowerCase()).toLowerCase());
        params.put("signMethod", "MD5");

        try {
            return HttpUtils.post(signUrl, params);
        } catch (Exception e) {
            logger.error("vivoPaySign error", e);
        }
        return "";
    }

    public String verifyVivo(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.VIVO, HttpUtils.getRequestParams(request).toString());

        try {
            String respCode = request.getParameter("respCode");
            String tradeStatus = request.getParameter("tradeStatus");
            String orderAmount = request.getParameter("orderAmount");
            if (!StringUtils.equals("200", respCode) || !StringUtils.equals("0000", tradeStatus)) {
                return "400";
            }
            String orderId = request.getParameter("cpOrderNumber");
            Order order = orderService.getOrderByOrderId(orderId);
            if (null == order) {
                return "400";
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "400";
            }

            String key = channelGame.getConfigParamsList().get(0);
            if (VivoSignUtils.verifySignature(HttpUtils.getRequestParams(request), key)) {
                if (order.getAmount() > Integer.valueOf(orderAmount)) {
                    ChannelStatsLogger.info(ChannelStatsLogger.VIVO, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "200";
                }

                orderService.paySuccess(order.getOrderId());
                return "200";
            } else {
                return "403";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.VIVO, HttpUtils.getRequestParams(request).toString(), "verifyVivo error:" + e);
            return "500";
        }
    }

    @Override
    public String verifyAppchinaSession(AppchinaSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getTicket()) || StringUtils.isBlank(session.getAppId())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";
        String appKey = channelGame.getConfigParamsList().get(0);
        String verifySessionUrl = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ticket", session.getTicket());
        params.put("app_id", session.getAppId());
        params.put("app_key", appKey);
        try {
            return HttpUtils.post(verifySessionUrl, params);
        } catch (Exception e) {
            logger.error("verifyAppchinaSession error", e);
        }
        return "";
    }

    @Override
    public String verifyAppchina(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.APPCHINA, HttpUtils.getRequestParams(request).toString());

        String transdata = request.getParameter("transdata");
        String sign = request.getParameter("sign");
        if (StringUtils.isBlank(transdata) || StringUtils.isBlank(sign)) {
            return "FAIL";
        }
        AppchinaPayCallback back = JsonMapper.toObject(transdata, AppchinaPayCallback.class);
        String orderId = back.getExorderno();
        Order order = orderService.getOrderByOrderId(orderId);
        if (null == order) {
            return "FAIL";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "FAIL";
        }
        String appKey = channelGame.getConfigParamsList().get(0);
        try {
            if (channelUtilsService.validAppchinaSign(transdata, sign, appKey)) {
                if (0 == back.getResult()) {
                    if (order.getAmount() > Integer.valueOf(back.getMoney())) {
                        ChannelStatsLogger.info(ChannelStatsLogger.APPCHINA, "order amount error");
                        orderService.payFail(order.getOrderId(), "order amount error");
                        return "SUCCESS";
                    }

                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "回调订单充值失败,transid : " + back.getTransid());
                }
                return "SUCCESS";
            } else {
                logger.debug("签名错误！");
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.APPCHINA, HttpUtils.getRequestParams(request).toString(), "verifyAppchina error:" + e);
            return "FAIL";
        }
        return "FAIL";
    }

    @Override
    public String verifyOuwanSession(OuwanSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getUid()) || null == session.getTimestamp()) {
            return "";
        }
        if (System.currentTimeMillis() - session.getTimestamp() > 10 * 60 * 1000) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null)
            return "";
        String secretKey = channelGame.getConfigParamsList().get(0);

        String authSign = MD5.encode(session.getUid() + "&" + session.getTimestamp() + "&" + secretKey);

        if (StringUtils.equals(session.getSign(), authSign)) {
            return "success";
        }
        return "";
    }

    @Override
    public String verifyOuwan(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.OUWAN, params.toString());

        String orderId = request.getParameter("callbackInfo");
        String amount = request.getParameter("amount");
        String sign = request.getParameter("sign");
        String orderStatus = request.getParameter("orderStatus");

        Order order = orderService.getOrderByOrderId(orderId);
        if (null == order) {
            return "fail";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "fail";
        }

        String secretKey = channelGame.getConfigParamsList().get(0);
        params.remove("sign");

        String authSign = Sign.signParamsByMD5(params, secretKey);
        if (StringUtils.equals(sign, authSign)) {
            if (!StringUtils.equals("1", orderStatus)) {
                orderService.payFail(order.getOrderId(), "ouwan callback payfail");
                return "success";
            }

            if (order.getAmount() > Double.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.OUWAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "success";
            }

            orderService.paySuccess(order.getOrderId());
            return "success";
        }
        return "fail";
    }


    @SuppressWarnings("unchecked")
    private String getRequestKeyValue(HttpServletRequest request) {
        StringBuffer buffer = new StringBuffer();
        Map<String, String[]> map = request.getParameterMap();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> it = set.iterator();
        while (it.hasNext()) {
            Entry<String, String[]> entry = it.next();
            buffer.append(entry.getKey()).append("=");
            for (String i : entry.getValue()) {
                buffer.append(i);
            }
            buffer.append("&");
        }
        return buffer.toString();
    }

    @Override
    public String verifyYoukuSession(YoukuSession session) {
        logger.debug("verifyYoukuSession params : " + session.toString());
        if (null == session || StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getSessionid())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }
        String appKey = channelGame.getConfigParamsList().get(0);
        String verifyUrl = channelGame.getConfigParamsList().get(1);
        String payKey = channelGame.getConfigParamsList().get(2);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sessionid", session.getSessionid());
        params.put("appkey", appKey);
        params.put("sign", HMacMD5.getHmacMd5Str(payKey, "appkey=" + appKey + "&sessionid=" + session.getSessionid()));
        try {
            return HttpUtils.post(verifyUrl, params);
        } catch (Exception e) {
            logger.error("verifyYoukuSession error", e);
        }
        return "";
    }

    @Override
    public String verifyYouku(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.YOUKU, HttpUtils.getRequestParams(request).toString());

        Map<String, String> map = new HashMap<String, String>();

        String orderId = request.getParameter("apporderID");
        String uid = request.getParameter("uid");
        String price = request.getParameter("price");
        String sign = request.getParameter("sign");
        String result = request.getParameter("result");

        if ("2".equals(result)) {
            logger.warn("该订单部分金额支付成功,", orderId);
        }

        try {
            Order order = orderService.getOrderByOrderId(orderId);
            if (null == order) {
                map.put("status", "failed");
                map.put("desc", "未找到订单");
                return JsonMapper.toJson(map);
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            ChannelEntity channel = basicRepository.getChannel(order.getChannelId());
            if (channelGame == null || channel == null) {
                map.put("status", "failed");
                map.put("desc", "订单信息有误");
                return JsonMapper.toJson(map);
            }
            String url = channel.getChannelCallbackUrl();
            String payKey = channelGame.getConfigParamsList().get(2);
            String authSign = HMacMD5.getHmacMd5Str(payKey, url + "?apporderID=" + orderId + "&price=" + price + "&uid=" + uid);
            if (!StringUtils.equals(authSign, sign)) {
                logger.debug("签名有误！");
                map.put("status", "failed");
                map.put("desc", "数字签名有误");
                return JsonMapper.toJson(map);
            }

            if (order.getAmount() > Integer.valueOf(price)) {
                ChannelStatsLogger.info(ChannelStatsLogger.YOUKU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                map.put("status", "success");
                map.put("desc", "通知成功");
                return JsonMapper.toJson(map);
            }

            orderService.paySuccess(order.getOrderId());
            map.put("status", "success");
            map.put("desc", "通知成功");
            return JsonMapper.toJson(map);
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.YOUKU, HttpUtils.getRequestParams(request).toString(), "verifyYouku error:" + e);

            map.put("status", "failed");
            map.put("desc", "服务器异常");
            return JsonMapper.toJson(map);
        }
    }

    @Override
    public String verifyJifengSession(JifengSession session) {
        logger.debug("verifyJifengSession params : " + session.toString());
        if (null == session || StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getToken())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String verifyUrl = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", session.getToken());
        try {
            return HttpUtils.post(verifyUrl, params);
        } catch (Exception e) {
            logger.error("verifyJifengSession error", e);
        }
        return "";
    }

    @Override
    @SuppressWarnings("unused")
    public String verifyJifeng(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.JIFENG, HttpUtils.getRequestParams(request).toString());

        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        String uid = Constants.JIFENG_DEVELOPER_UID;
        try {
            String authSign = MD5.encode((uid + time).getBytes("UTF-8"));
            if (StringUtils.equals(authSign, sign)) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document d = db.parse(request.getInputStream());
                String order_id = d.getElementsByTagName("order_id").item(0).getFirstChild().getNodeValue();
                String appkey = d.getElementsByTagName("appkey").item(0).getFirstChild().getNodeValue();
                String cost = d.getElementsByTagName("cost").item(0).getFirstChild().getNodeValue();
                String create_time = d.getElementsByTagName("create_time").item(0).getFirstChild().getNodeValue();

                Order order = orderService.getOrderByOrderId(order_id);
                if (null == order) {
                    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><ErrorCode>0</ErrorCode><ErrorDesc>找不到订单!</ErrorDesc></response>";
                }

                ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
                if (channelGame == null) {
                    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><ErrorCode>0</ErrorCode><ErrorDesc>订单信息有误!</ErrorDesc></response>";
                }

                if (order.getAmount() > Integer.valueOf(cost) * 10) {
                    ChannelStatsLogger.info(ChannelStatsLogger.JIFENG, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><ErrorCode>1</ErrorCode><ErrorDesc>Success</ErrorDesc></response>";
                }
                orderService.paySuccess(order.getOrderId());
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><ErrorCode>1</ErrorCode><ErrorDesc>Success</ErrorDesc></response>";
            } else {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><ErrorCode>0</ErrorCode><ErrorDesc>签名错误！</ErrorDesc></response>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            ChannelStatsLogger.error(ChannelStatsLogger.JIFENG, HttpUtils.getRequestParams(request).toString(), "verifyJifeng error:" + e);
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><ErrorCode>0</ErrorCode><ErrorDesc>服务器异常！</ErrorDesc></response>";
        }
    }

    @Override
    public String signHTCPayContent(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        String zdappId = request.getParameter("zdappId");
        String platformId = request.getParameter("platformId");
        String content = request.getParameter("content");
        if (StringUtils.isBlank(zdappId) || StringUtils.isBlank(platformId) || StringUtils.isBlank(content)) {
            result.put("data", "");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity platform = basicRepository.getByChannelAndGameId(Integer.valueOf(platformId), Long.valueOf(zdappId));
        String privateKey = platform.getConfigParamsList().get(1);

        result.put("data", channelUtilsService.signHTCPayContent(content, privateKey));
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyHTCSession(HTCSession session) {
        logger.debug(session.toString());
        Map<String, String> result = new HashMap<String, String>();

        if (null == session || StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getContent()) || StringUtils.isBlank(session.getSign())) {
            result.put("flag", "false");
            result.put("msg", "参数为空");
            return JsonMapper.toJson(result);
        }
        ChannelGameEntity platform = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        String publicKey = platform.getConfigParamsList().get(0);
        try {
            boolean flag = channelUtilsService.verifyHTC(session.getContent(), session.getSign(), publicKey);
            result.put("flag", String.valueOf(flag));
            result.put("msg", flag ? "success" : "签名校验错误！");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyHTCSession error：", e);
            result.put("flag", "false");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    @SuppressWarnings("unused")
    public String verifyHTC(HttpServletRequest request) {
        try {
            String payContent = new String(IOUtils.toByteArray(request.getInputStream()), "utf-8");
            Map<String, Object> params = changeToParamters(payContent);

            ChannelStatsLogger.info(ChannelStatsLogger.HTC, params.toString());

            String sign_type = URLDecoder.decode(params.get("sign_type").toString(), "utf-8");
            String sign = URLDecoder.decode(params.get("sign").toString(), "utf-8");
            String orderInfo = URLDecoder.decode(params.get("order").toString(), "utf-8");
            HTCPayCallback back = JsonMapper.toObject(orderInfo, HTCPayCallback.class);

            if (!"1".equals(back.getResult_code())) {
                orderService.payFail(back.getGame_order_id(), "htc back pay result_code=" + back.getResult_code());
                return "fail";
            }

            Order order = orderService.getOrderByOrderId(back.getGame_order_id());
            if (null == order) {
                return "fail";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                return "fail";
            }

            String publicKey = channelGame.getConfigParamsList().get(0);
            if (channelUtilsService.verifyHTC(orderInfo, sign, publicKey)) {
                if (order.getAmount() > Integer.valueOf(back.getReal_amount())) {
                    ChannelStatsLogger.info(ChannelStatsLogger.HTC, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "success";
                }

                orderService.paySuccess(order.getOrderId());
                return "success";
            } else {
                ChannelStatsLogger.error(ChannelStatsLogger.HTC, HttpUtils.getRequestParams(request).toString(), "签名验证错误！");
                return "fail";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.HTC, HttpUtils.getRequestParams(request).toString(), "verifyHTC error:" + e);
            return "fail";
        }
    }

    private Map<String, Object> changeToParamters(String payContent) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(payContent)) {
            String[] paramertes = payContent.split("&");
            for (String parameter : paramertes) {
                String[] p = parameter.split("=");
                map.put(p[0], p[1].replaceAll("\"", ""));
            }
        }
        return map;
    }

    @Override
    public String verifyMeizuSession(MeizuSession session) {
        logger.debug("verifyMeizuSession params : " + session.toString());
        if (null == session || StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getApp_id()) || StringUtils.isBlank(session.getSession_id()) || StringUtils.isBlank(session.getUid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String secretKey = channelGame.getConfigParamsList().get(0);
        String verifyUrl = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("app_id", session.getApp_id());
        params.put("session_id", session.getSession_id());
        params.put("uid", session.getUid());
        params.put("ts", System.currentTimeMillis());
        params.put("sign", Sign.signByMD5(params, ":" + secretKey));
        params.put("sign_type", "md5");
        try {
            return HttpUtils.postToHttps(verifyUrl, params);
        } catch (Exception e) {
            logger.error("verifyMeizuSession error", e);
        }
        return "";
    }

    @Override
    public String meizuPaySign(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        logger.debug(params.toString());

        if (null == params || params.get("app_id") == null || params.get("cp_order_id") == null || params.get("uid") == null
                || params.get("zdappId") == null || params.get("platformId") == null) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(params.get("platformId").toString()), Long.valueOf(params.get("zdappId").toString()));
        if (null == channelGame) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }
        params.remove("zdappId");
        params.remove("platformId");
        params.remove("sign_type");
        params.remove("sign");

        String secretKey = channelGame.getConfigParamsList().get(0);
        String sign = Sign.signByMD5(params, ":" + secretKey);

        result.put("code", "0");
        result.put("sign", sign);
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyMeizu(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.MEIZU, params.toString());

        Map<String, String> result = new HashMap<String, String>();
        try {
            String cp_order_id = params.get("cp_order_id").toString();
            Order order = orderService.getOrderByOrderId(cp_order_id);
            if (null == order) {
                result.put("code", "120013");
                result.put("message", "未找到该订单！");
                return JsonMapper.toJson(result);
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                result.put("code", "120013");
                result.put("message", "订单信息有误！");
                return JsonMapper.toJson(result);
            }
            String secretKey = channelGame.getConfigParamsList().get(0);
            String sign = params.get("sign").toString();
            params.remove("sign_type");
            params.remove("sign");
            String authSign = Sign.signByMD5(params, ":" + secretKey);
            if (!StringUtils.equals(authSign, sign)) {
                result.put("code", "120013");
                result.put("message", "验证签名错误！");
                return JsonMapper.toJson(result);
            }

            if (order.getAmount() > Double.valueOf(params.get("total_price").toString()) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.MEIZU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                result.put("code", "200");
                return JsonMapper.toJson(result);
            }

            orderService.paySuccess(order.getOrderId());
            result.put("code", "200");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.MEIZU, params.toString(), "verifyMeizu error:" + e);
            result.put("code", "900000");
            result.put("message", "服务器错误");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyNduo(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.NDUO, params.toString());

        try {
            String orderId = request.getParameter("appTradeNo");
            String amount = request.getParameter("amount");
            Order order = orderService.getOrderByOrderId(orderId);
            if (null == order) {
                return "failed";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                return "failed";
            }

            String secretKey = channelGame.getConfigParamsList().get(0);
            params.remove("sign");
            String authSign = Sign.signByMD5(params, secretKey);
            if (!StringUtils.equals(request.getParameter("sign"), authSign)) {
                return "failed";
            }

            if (order.getAmount() > Integer.valueOf(amount)) {
                ChannelStatsLogger.info(ChannelStatsLogger.NDUO, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "success";
            }

            orderService.paySuccess(order.getOrderId());
            return "success";
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.NDUO, params.toString(), "verifyNduo error :" + e);
            return "failed";
        }
    }

    @Override
    public String verifyYoulongSession(YoulongSession session) {
        logger.debug("verifyYoulongSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getPid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String verifyUrl = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", session.getToken());
        params.put("pid", session.getPid());
        params.put("ip", IpUtils.getIpAddr(request));
        try {
            return HttpUtils.post(verifyUrl, params);
        } catch (Exception e) {
            logger.error("verifyYoulongSession error", e);
        }
        return "";
    }

    @Override
    public String verifyYoulong(HttpServletRequest request) {
        Map<String, String> params = HttpUtils.getRequestParams(request);
        ChannelStatsLogger.info(ChannelStatsLogger.YOULONG, params.toString());

        String orderId = request.getParameter("orderId");
        String userName = request.getParameter("userName");
        String amount = request.getParameter("amount");
        String extra = request.getParameter("extra");
        String flag = request.getParameter("flag");
        String nonceStr = request.getParameter("nonceStr");
        String ourOrderNum = request.getParameter("ourOrderNum");

        try {
            Order order = orderService.getOrderByOrderId(orderId);
            if (null == order) {
                return "can not find this order";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                return "this is an unvalid order : channelGame isnull";
            }

            String key = channelGame.getConfigParamsList().get(0);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("amount", amount);
            map.put("extra", extra);
            map.put("nonceStr", nonceStr);
            map.put("orderId", orderId);
            map.put("ourOrderNum", ourOrderNum);
            map.put("userName", userName);
            /** 生成签名*/
            String authSign = Sign.signByMD5ValNullSkip(map, key).toUpperCase();
            ChannelStatsLogger.info(ChannelStatsLogger.YOULONG, "authSign:" + authSign + "flag:" + flag);

            if (StringUtils.equals(flag, authSign)) {
                ChannelStatsLogger.info(ChannelStatsLogger.YOULONG, "sign check success");
                if (order.getAmount() > Float.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.YOULONG, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "OK";
                }

                orderService.paySuccess(order.getOrderId());
                return "OK";
            } else {
                logger.info("verifyYoulong签名校验错误！");
                return "sign unvalid";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.YOULONG, params.toString(), "verifyYoulong error :" + e);
            return "server error";
        }
    }

    @Override
    public String verifyLenovoSession(LenovoSession session) {
        Map<String, String> result = new HashMap<String, String>();
        logger.debug("verifyLenovoSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getToken())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String realm = channelGame.getConfigParamsList().get(0);
        String verifyUrl = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lpsust", session.getToken());
        params.put("realm", realm);
        try {
            String resultXml = HttpUtils.post(verifyUrl, params);
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(resultXml)));
            Document doc = parser.getDocument();
            Node node = doc.getFirstChild();
            if ("IdentityInfo".equals(node.getNodeName())) {
                result.put("code", "0");
                NodeList nodeList = node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node item = nodeList.item(i);
                    if ("AccountID".equals(item.getNodeName()))
                        result.put("accountId", item.getTextContent());
                    if ("Username".equals(item.getNodeName()))
                        result.put("userName", item.getTextContent());
                    if ("DeviceID".equals(item.getNodeName()))
                        result.put("deviceID", item.getTextContent());
                    if ("verified".equals(item.getNodeName()))
                        result.put("verified", item.getTextContent());
                }
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "1");
                result.put("msg", "用户验证错误");
                logger.info("verifyLenovoSession back : " + resultXml);
            }
            return null;
        } catch (Exception e) {
            logger.error("verifyLenovoSession error", e);
            return "";
        }

    }

    @Override
    public String verifyLenovo(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.LENOVO, HttpUtils.getRequestParams(request).toString());

        try {
            String transdata = request.getParameter("transdata");
            String sign = request.getParameter("sign");
            if (StringUtils.isBlank(transdata) || StringUtils.isBlank(sign)) {
                return "FAILURE";
            }

            LenovoPayCallback back = JsonMapper.toObject(transdata, LenovoPayCallback.class);
            if (null == back) {
                return "FAILURE";
            }

            Order order = basicRepository.getOrderByOrderId(back.getExorderno());
            if (null == order) {
                return "FAILURE";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                return "FAILURE";
            }

            String privateKey = channelGame.getConfigParamsList().get(2);
            if (CpTransSyncSignValid.validSign(transdata, sign, privateKey)) {
                if (order.getAmount() > Integer.valueOf(back.getMoney())) {
                    ChannelStatsLogger.info(ChannelStatsLogger.LENOVO, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "SUCCESS";
                }

                orderService.paySuccess(order.getOrderId());
                return "SUCCESS";
            } else {
                return "FAILURE";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.LENOVO, HttpUtils.getRequestParams(request).toString(), "verifyLenovo error :" + e);
            return "FAILURE";
        }

    }

    @Override
    public String verifyKudong(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.KUDONG, HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();
        String uid = request.getParameter("uid");
        String oid = request.getParameter("oid");
        String gold = request.getParameter("gold");
        String sid = request.getParameter("sid");
        String time = request.getParameter("time");
        String eif = request.getParameter("eif");
        String sign = request.getParameter("sign");
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(oid) || StringUtils.isBlank(gold)
                || StringUtils.isBlank(time) || StringUtils.isBlank(sign) || StringUtils.isBlank(eif)) {
            result.put("error_code", "1");
            result.put("error_message", "存在参数为空！");
            return JsonMapper.toJson(result);
        }

        Order order = basicRepository.getOrderByOrderId(eif);
        if (null == order) {
            result.put("error_code", "1");
            result.put("error_message", "找不到订单！");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            result.put("error_code", "1");
            result.put("error_message", "订单信息有误！");
            return JsonMapper.toJson(result);
        }
        String payKey = channelGame.getConfigParamsList().get(0);
        try {
            String authSign = MD5.encode(uid + "-" + sid + "-" + oid + "-" + gold + "-" + time + "-" + payKey).toUpperCase();
            if (StringUtils.equals(authSign, sign)) {
                logger.debug("酷动充值回调签名正确");
                if (order.getAmount() > Double.valueOf(gold) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.KUDONG, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    result.put("error_code", "0");
                    result.put("error_message", "success");
                    return JsonMapper.toJson(result);
                }
                //orderService.paySuccess(order.getOrderId());
                result.put("error_code", "0");
                result.put("error_message", "success");
                return JsonMapper.toJson(result);
            } else {
                logger.debug("酷动充值回调签名错误sign{}, authSign{}", sign, authSign);
                result.put("error_code", "1");
                result.put("error_message", "存在参数为空！");
                return JsonMapper.toJson(result);
            }

        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.KUDONG, HttpUtils.getRequestParams(request).toString(), "verifyKudong error :" + e);
            result.put("error_code", "1");
            result.put("error_message", "服务器异常！");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyLetv(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.LETV, params.toString());

        String sign = params.get("sign").toString();
        String out_trade_no = params.get("out_trade_no").toString();

        Order order = basicRepository.getOrderByOrderId(out_trade_no);
        if (null == order) {
            return "failure";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "failure";
        }

        if (!"TRADE_SUCCESS".equals(request.getParameter("trade_result"))) {
            return "failure";
        }

        try {
            String secretKey = channelGame.getConfigParamsList().get(0);
            params.remove("sign");
            String authSign = Sign.signByMD5(params, "&key=" + secretKey);
            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                if (order.getAmount() > Double.valueOf(request.getParameter("price")) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.LETV, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "success";
                }
                orderService.paySuccess(order.getOrderId());
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.LETV, params.toString(), "verifyLetv error :" + e);
        }
        return "failure";
    }

    @Override
    public String verify19meng(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.YIJIUMENG, params.toString());

        String orderId = request.getParameter("orderId");
        String uid = request.getParameter("uid");
        String amount = request.getParameter("amount");
        String coOrderId = request.getParameter("coOrderId");
        String sign = request.getParameter("sign");
        String success = request.getParameter("success");

        Order order = basicRepository.getOrderByOrderId(coOrderId);
        if (null == order) {
            return "FAIL";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "FAIL";
        }
        if (!"0".equals(success)) {
            return "FAIL";
        }
        try {
            String secret = channelGame.getConfigParamsList().get(0);
            String content = "orderId=" + orderId + "&uid=" + uid + "&amount=" + amount + "&coOrderId=" + coOrderId + "&success=0&secret=" + secret;
            String authSign = MD5.encode(content);

            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                if (order.getAmount() > Integer.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.YIJIUMENG, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "SUCCESS";
                }
                orderService.paySuccess(order.getOrderId());
                return "SUCCESS";
            } else {
                return "FAIL";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.YIJIUMENG, params.toString(), "verify19meng error :" + e);
        }
        return "FAIL";
    }

    @Override
    public String verifyKuwo(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.KUWO, params.toString());

        String serverid = request.getParameter("serverid");
        String time = request.getParameter("time");
        String userid = request.getParameter("userid");
        String orderid = request.getParameter("orderid");
        String amount = request.getParameter("amount");
        String ext1 = request.getParameter("ext1");
        String ext2 = request.getParameter("ext2");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(ext1);
        if (null == order) {
            return "-2";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "-2";
        }

        try {
            String key = channelGame.getConfigParamsList().get(0);
            String content = "serverid=" + serverid + "&time=" + time + "&userid=" + userid + "&orderid=" + orderid + "&amount=" + amount + "&ext1=" + ext1 + "&ext2=" + ext2 + "&key=" + key;
            String authSign = MD5.encode(content);

            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                if (order.getAmount() > Integer.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.KUWO, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "0";
                }
                orderService.paySuccess(order.getOrderId());
                return "0";
            } else {
                return "-1";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.KUWO, params.toString(), "verifyKuwo error :" + e);
        }
        return "-6";
    }

    @Override
    public String verifyMumayi(HttpServletRequest request) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            ChannelStatsLogger.info(ChannelStatsLogger.MUMAYI, stringBuffer.toString());
            MMYPayResult mmYPayResult = JsonMapper.toObject(stringBuffer.toString(), MMYPayResult.class);
            if (mmYPayResult == null || StringUtils.isEmpty(mmYPayResult.getTradeSign()) || StringUtils.isEmpty(mmYPayResult.getOrderID())) {
                return "fail";
            }
            Order order = basicRepository.getOrderByOrderId(mmYPayResult.getProductDesc());
            if (null == order) {
                return "fail";
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (null == channelGame) {
                return "fail";
            }
            if (channelUtilsService.verifyMmy(mmYPayResult.getTradeSign(), channelGame.getConfigParamsList().get(0), mmYPayResult.getOrderID())) {
                if ("success".equals(mmYPayResult.getTradeState())) {
                    if (order.getAmount() <= Double.valueOf(mmYPayResult.getProductPrice()) * 100) {
                        orderService.paySuccess(order.getOrderId());
                        return "success";
                    } else {
                        orderService.payFail(order.getOrderId(), "order amount error");
                        return "success";
                    }
                }
                orderService.payFail(order.getOrderId(), "pay state callback fail");
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.MUMAYI, stringBuffer.toString(), "verifyMumayi error :" + e);
        }
        return "fail";
    }

    @Override
    public String verifyPlaySession(PlaySession session) {
        logger.debug("verifyPlaySession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getCode())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }
        String client_id = channelGame.getConfigParamsList().get(0);
        String client_secret = channelGame.getConfigParamsList().get(1);
        String verifyUrl = channelGame.getConfigParamsList().get(2);
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", session.getCode());
        params.put("client_secret", client_secret);
        try {
            // 进行数字签名，并把签名相关字段放入请求参数MAP
            RequestParasUtil.signature("2", client_id, client_secret, "MD5", "v1.0", params);
            // 发起请求
            String result = RequestParasUtil.sendPostRequest(verifyUrl, params);
            logger.debug("verifyPlaySession result : " + result);
            return result;
        } catch (Exception e) {
            logger.error("verifyPlaySession error", e);
            return "";
        }
    }

    @Override
    public String verifyPlaySms(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info("playSms", params.toString());

        String cp_order_id = request.getParameter("cp_order_id");
        String correlator = request.getParameter("correlator");
        String order_time = request.getParameter("order_time");
        String method = request.getParameter("method");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(cp_order_id);
        if (null == order) {
            return channelUtilsService.playSmsXml(order, cp_order_id, correlator, false);
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return channelUtilsService.playSmsXml(order, cp_order_id, correlator, false);
        }
        try {
            String signMD5 = Sign.encode(cp_order_id, correlator, order_time, method, channelGame.getConfigParamsList().get(1));
            if (StringUtils.equalsIgnoreCase(signMD5, sign)) {
                return channelUtilsService.playSmsXml(order, cp_order_id, correlator, true);
            }
        } catch (Exception e) {
            ChannelStatsLogger.error("playSms", params.toString(), "verifyPlaySms error :" + e);
        }
        return channelUtilsService.playSmsXml(order, cp_order_id, correlator, false);
    }

    @Override
    public String verifyPlay(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.PLAY, params.toString());

        String cp_order_id = request.getParameter("cp_order_id");
        String correlator = request.getParameter("correlator");
        String result_code = request.getParameter("result_code");
        String fee = request.getParameter("fee");
        String pay_type = request.getParameter("pay_type");
        String method = request.getParameter("method");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(cp_order_id);
        if (null == order) {
            return channelUtilsService.playXml(cp_order_id, false);
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return channelUtilsService.playXml(cp_order_id, false);
        }
        try {
            String signMD5 = Sign.encode(cp_order_id, correlator, result_code, fee, pay_type, method, channelGame.getConfigParamsList().get(1));
            if (StringUtils.equalsIgnoreCase(signMD5, sign)) {
                if ("00".equals(result_code)) {
                    if (order.getAmount() <= Double.valueOf(fee) * 100) {
                        orderService.paySuccess(order.getOrderId());
                        return channelUtilsService.playXml(cp_order_id, true);
                    }
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return channelUtilsService.playXml(cp_order_id, true);
                }
                orderService.payFail(order.getOrderId(), "pay state callback fail ");
                return channelUtilsService.playXml(cp_order_id, true);
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.PLAY, params.toString(), "verifyPlay error :" + e);
        }
        return channelUtilsService.playXml(cp_order_id, false);
    }

    @Override
    public String verifyJiuduSession(JiuduSession session) {
        logger.debug("verifyJiuduSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getGameId()) || StringUtils.isBlank(session.getSid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String cpId = channelGame.getConfigParamsList().get(0);
        String key = channelGame.getConfigParamsList().get(1);
        String url = channelGame.getConfigParamsList().get(2);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "gameServerLogin");
        params.put("cpId", cpId);
        params.put("sid", session.getSid());
        params.put("gameId", session.getGameId());
        params.put("paramSign", MD5.encode(cpId + session.getSid() + session.getGameId() + key).toUpperCase());
        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyJiuduSession error", e);
        }
        return "";
    }

    @Override
    public String verifyJiudu(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.JIUDU, params.toString());

        String asyx_order_id = request.getParameter("asyx_order_id");
        String subject = request.getParameter("subject");
        String subject_desc = request.getParameter("subject_desc");
        if (StringUtils.isNotBlank(subject_desc)) {
            subject_desc = subject_desc.trim();
        }
        String trade_status = request.getParameter("trade_status");
        String amount = request.getParameter("amount");
        String channel = request.getParameter("channel");
        String order_creatdt = request.getParameter("order_creatdt");
        String order_paydt = request.getParameter("order_paydt");
        String asyx_game_id = request.getParameter("asyx_game_id");
        String pay_order_id = request.getParameter("pay_order_id");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(subject_desc);
        if (null == order) {
            return "fail";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "fail";
        }
        if (!"1".equals(trade_status)) {
            return "fail";
        }

        try {
            String payKey = channelGame.getConfigParamsList().get(3);
            String content = asyx_order_id + subject + subject_desc + trade_status + amount +
                    channel + order_creatdt + order_paydt + asyx_game_id + pay_order_id;
            String authSign = HMacMD5.getHmacMd5Str(payKey, content);
            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                logger.debug("jiudu pay call back sign valid success");
                if (order.getAmount() > Double.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.JIUDU, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "success";
                }
                orderService.paySuccess(order.getOrderId());
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.JIUDU, params.toString(), "verifyJiudu error :" + e);
        }
        return "fail";

    }

    @Override
    public String verifyPaojiaoSession(PaojiaoSession session) {
        logger.debug("verifyPaojiaoSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getAppId()) || StringUtils.isBlank(session.getToken())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", session.getToken());
        params.put("appId", session.getAppId());
        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyPaojiaoSession error", e);
        }
        return "";
    }

    @Override
    public String verifyPaojiao(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.PAOJIAO, params.toString());

        String uid = request.getParameter("uid");
        String orderNo = request.getParameter("orderNo");
        String price = request.getParameter("price");
        String status = request.getParameter("status");
        String remark = request.getParameter("remark");
        String subject = request.getParameter("subject");
        String gameId = request.getParameter("gameId");
        String payTime = request.getParameter("payTime");
        String ext = request.getParameter("ext");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(ext);
        if (null == order) {
            return "fail";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "fail";
        }
        try {
            String appKey = channelGame.getConfigParamsList().get(1);
            String content = "uid=" + uid
                    + "price=" + price
                    + "orderNo=" + orderNo
                    + "remark=" + remark
                    + "status=" + status
                    + "subject=" + subject
                    + "gameId=" + gameId
                    + "payTime=" + payTime
                    + "ext=" + ext;
            String authSign = MD5.encode(content + appKey);
            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                logger.debug("paojiao payback valid sign success");
                if (order.getAmount() > Double.valueOf(price) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.PAOJIAO, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "success";
                }
                orderService.paySuccess(order.getOrderId());
                return "success";
            } else {
                logger.debug("paojiao payback valid sign failed");
                return "fail";
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.PAOJIAO, params.toString(), "verifyPaojiao error :" + e);
        }
        return "fail";
    }

    @Override
    public String verifyQixiazi(HttpServletRequest request) {
        String transdata = request.getParameter("transdata");
        String sign = request.getParameter("sign");
        ChannelStatsLogger.info(ChannelStatsLogger.QIXIAZI, transdata + sign);

        try {
            QixiaziPayCallback back = JsonMapper.toObject(transdata, QixiaziPayCallback.class);
            Order order = basicRepository.getOrderByOrderId(back.getCpprivate());
            if (null == order) {
                return "FAILURE";
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "FAILURE";
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("exorderno", back.getExorderno());
            params.put("transid", back.getTransid());
            params.put("appid", back.getAppid());
            params.put("waresid", back.getWaresid());
            params.put("feetype", back.getFeetype());
            params.put("money", back.getMoney());
            params.put("count", back.getCount());
            params.put("result", back.getResult());
            params.put("transtype", back.getTranstype());
            params.put("transtime", back.getTranstime());
            params.put("cpprivate", back.getCpprivate());
            params.put("paytype", back.getPaytype());
            params.put("uid", back.getUid());

            String secretKey = channelGame.getConfigParamsList().get(0);
            String authSign = MD5.encode(Sign.signByMD5(params, "") + secretKey);
            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                logger.debug("verifyQixiazi valid sign success");
                if (order.getAmount() > back.getMoney()) {
                    ChannelStatsLogger.info(ChannelStatsLogger.QIXIAZI, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "SUCCESS";
                }
                orderService.paySuccess(order.getOrderId());
                return "SUCCESS";
            } else {
                logger.debug("verifyQixiazi valid sign fail");
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.QIXIAZI, transdata + sign, "verifyQixiazi error :" + e);
        }
        return "FAILURE";
    }

    @Override
    public String verifyKuaiyongSession(KuaiyongSession session) {
        logger.debug("verifyKuaiyongSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getTokenKey())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String appKey = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tokenKey", session.getTokenKey());
        params.put("sign", MD5.encode(appKey + session.getTokenKey()));
        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyKuaiyongSession error", e);
        }
        return "";
    }


    @Override
    @SuppressWarnings("unused")
    public String verifyKuaiyong(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.KUAIYONG, params.toString());

        String notify_data = request.getParameter("notify_data");
        String orderid = request.getParameter("orderid");
        String dealseq = request.getParameter("dealseq");
        String uid = request.getParameter("uid");
        String subject = request.getParameter("subject");
        String v = request.getParameter("v");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(dealseq);
        if (null == order) {
            return "failed";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "failed";
        }
        try {
            String publicKey = channelGame.getConfigParamsList().get(2);
            String data = new String(RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(publicKey), Base64.decodeToByteArray(notify_data)));
            String fee = data.split("&")[1].split("=")[1];
            String payresult = data.split("&")[2].split("=")[1];

            if (!StringUtils.equals("0", payresult)) {
                return "success";
            }
            String content = "dealseq=" + dealseq
                    + "&notify_data=" + notify_data
                    + "&orderid=" + orderid
                    + "&subject=" + subject
                    + "&uid=" + uid
                    + "&v=" + v;

        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.KUAIYONG, params.toString(), "verifyKuaiyong error :" + e);
        }
        return "failed";
    }

    @Override
    public String verifyHuaweiSession(HuaweiSession session) {
        logger.debug("verifyHuaweiSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getAccessToken())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nsp_svc", "OpenUP.User.getInfo");
        params.put("nsp_ts", System.currentTimeMillis() / 1000);
        try {
            params.put("access_token", URLEncoder.encode(session.getAccessToken(), "utf-8").replace("+", "%2B"));
            return HttpUtils.postToHttps(url, params);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("verifyHuaweiSession error", e);
        }
        return "";
    }

    @Override
    public String verifyHuawei(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        Map<String, String> params = new HashMap<String, String>();
        try {
            request.setCharacterEncoding("UTF-8");
            String line = null;
            StringBuffer sb = new StringBuffer();
            request.setCharacterEncoding("UTF-8");
            InputStream stream = request.getInputStream();
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            br.close();
            String str = sb.toString();
            if (StringUtils.isBlank(str)) {
                result.put("result", "98");
                return JsonMapper.toJson(result);
            }

            String[] valueKey = str.split("&");
            for (String temp : valueKey) {
                String[] single = temp.split("=");
                params.put(single[0], single[1]);
            }

            String sign = (String) params.get("sign");
            String extReserved = (String) params.get("extReserved");
            String sysReserved = (String) params.get("sysReserved");
            if (null != sign) {
                sign = URLDecoder.decode(sign, "utf-8");
                params.put("sign", sign);
            }

            if (null != extReserved) {
                extReserved = URLDecoder.decode(extReserved, "utf-8");
                params.put("extReserved", extReserved);
            }

            if (null != sysReserved) {
                sysReserved = URLDecoder.decode(sysReserved, "utf-8");
                params.put("sysReserved", sysReserved);
            }

            ChannelStatsLogger.info(ChannelStatsLogger.HUAWEI, params.toString());

            String orderId = (String) params.get("requestId");
            Order order = basicRepository.getOrderByOrderId(orderId);
            if (null == order) {
                result.put("result", "3");
                return JsonMapper.toJson(result);
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                result.put("result", "3");
                return JsonMapper.toJson(result);
            }

            String publicKey = channelGame.getConfigParamsList().get(1);

            sign = (String) params.get("sign");

            if (channelUtilsService.verifyHuawei(params, publicKey)) {
                logger.debug("verify huawei valid sign success");
                if (order.getAmount() > Double.valueOf(params.get("amount")) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.HUAWEI, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    result.put("result", "0");
                    return JsonMapper.toJson(result);
                }
                orderService.paySuccess(order.getOrderId());
                result.put("result", "0");
                return JsonMapper.toJson(result);
            } else {
                result.put("result", "1");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.HUAWEI, params.toString(), "verifyHuawei error :" + e);
        }
        result.put("result", "1");
        return JsonMapper.toJson(result);
    }

    @Override
    public String huaweiPaySign(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        String requestId = request.getParameter("requestId");
        String productName = request.getParameter("productName");
        String productDesc = request.getParameter("productDesc");
        if (StringUtils.isBlank(requestId) || StringUtils.isBlank(productName) || StringUtils.isBlank(productDesc)) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }
        Order order = basicRepository.getOrderByOrderId(requestId);
        if (order == null) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("userID", channelGame.getConfigParamsList().get(2));
        params.put("applicationID", channelGame.getConfigParamsList().get(3));
        params.put("amount", new DecimalFormat("0.00").format((double) order.getAmount() / 100));
        params.put("productName", productName);
        params.put("requestId", requestId);
        params.put("productDesc", productDesc);
        ChannelStatsLogger.info(ChannelStatsLogger.HUAWEI, "pay sign: " + params.toString());

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if (!"sign".equals(key)) {
                String value = (String) params.get(key);
                if (value != null) {
                    content.append((i == 0 ? "" : "&") + key + "=" + value);
                }
            }
        }
        try {
            result.put("code", "0");
            result.put("sign", channelUtilsService.huaweiPaySign(content.toString(), channelGame.getConfigParamsList().get(4)));
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.HUAWEI, params.toString(), "huaweiPaySign error :" + e);
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyFtnnSession(FtnnSession session) {
        logger.debug("verifyFtnnSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getUid())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }
        String url = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("state", session.getToken());
        params.put("uid", session.getUid());
        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyFtnnSession error", e);
        }
        return "";
    }


    @Override
    @SuppressWarnings("unused")
    public String verifyFtnn(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.FTNN, HttpUtils.getRequestParams(request).toString());
        Map<String, String> result = new HashMap<String, String>();

        String orderid = request.getParameter("orderid");
        String p_type = request.getParameter("p_type");
        String uid = request.getParameter("uid");
        String money = request.getParameter("money");
        String gamemoney = request.getParameter("gamemoney");
        String serverid = request.getParameter("serverid");
        String mark = request.getParameter("mark");
        String roleid = request.getParameter("roleid");
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(mark);
        if (order == null) {
            result.put("status", "1");
            result.put("code", "other_error");
            result.put("msg", "不存在该订单");
            result.put("money", money);
            result.put("gamemoney", gamemoney);
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("status", "1");
            result.put("code", "other_error");
            result.put("msg", "订单信息有误");
            result.put("money", money);
            result.put("gamemoney", gamemoney);
            return JsonMapper.toJson(result);
        }
        String secrect = channelGame.getConfigParamsList().get(1);

        StringBuffer sb = new StringBuffer();
        sb.append(orderid).append(uid).append(money).append(gamemoney)
                .append(StringUtils.isBlank(serverid) ? "" : serverid)
                .append(secrect)
                .append(StringUtils.isBlank(mark) ? "" : mark)
                .append(StringUtils.isBlank(roleid) ? "" : roleid)
                .append(time);

        try {
            String validSign = MD5.encode(sb.toString());
            if (StringUtils.equals(sign, validSign)) {
                logger.debug("verify 4399 valid sign success");

                if (order.getAmount() > Double.valueOf(money) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.FTNN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    result.put("status", "2");
                    result.put("code", "money_error");
                    result.put("msg", "订单金额有误");
                    result.put("money", money);
                    result.put("gamemoney", gamemoney);
                    return JsonMapper.toJson(result);
                }

                orderService.paySuccess(order.getOrderId());
                result.put("status", "2");
                result.put("code", "");
                result.put("msg", "成功");
                result.put("money", money);
                result.put("gamemoney", gamemoney);
                return JsonMapper.toJson(result);
            } else {
                logger.debug("verify 4399 valid sign failed");

                result.put("status", "1");
                result.put("code", "sign_error");
                result.put("money", money);
                result.put("gamemoney", gamemoney);
                result.put("msg", "请求串的md5验证码错误");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.FTNN, HttpUtils.getRequestParams(request).toString(), "verifyFtnn error :" + e);

            result.put("status", "1");
            result.put("code", "other_error");
            result.put("msg", "服务器异常");
            result.put("money", money);
            result.put("gamemoney", gamemoney);
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyTsSession(TsSession session) {
        logger.debug("verifyTsSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getGid()) || StringUtils.isBlank(session.getPid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList("\\|").get(0);
        String key = channelGame.getConfigParamsList("\\|").get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pid", session.getPid());
        params.put("gid", session.getGid());
        params.put("time", System.currentTimeMillis() / 1000);
        params.put("token", session.getToken());
        params.put("sign", MD5.encode(session.getGid() + params.get("time").toString() + key));
        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyTsSession error", e);
            return "";
        }
    }

    @Override
    public String verifyTs(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.TS, HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        String oid = request.getParameter("oid");
        String doid = request.getParameter("doid");
        String dsid = request.getParameter("dsid");
        String uid = request.getParameter("uid");
        String money = request.getParameter("money");
        String coin = request.getParameter("coin");

        Order order = basicRepository.getOrderByOrderId(doid);
        if (order == null) {
            result.put("state", "0");
            result.put("data", "");
            result.put("msg", "不存在该订单");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("state", "0");
            result.put("data", "");
            result.put("msg", "订单信息有误");
            return JsonMapper.toJson(result);
        }

        String key = channelGame.getConfigParamsList("\\|").get(2);

        String content = time + key + oid + doid + dsid + uid + money + coin;
        String validSign = MD5.encode(content).toLowerCase();
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify 37 valid sign success");

            if (order.getAmount() > Double.valueOf(money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.TS, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                result.put("state", "1");
                result.put("data", "");
                result.put("msg", "订单金额有误");
                return JsonMapper.toJson(result);
            }
            orderService.paySuccess(order.getOrderId());
            result.put("state", "1");
            result.put("data", "");
            result.put("msg", "success");
            return JsonMapper.toJson(result);
        } else {
            logger.debug("verify 37 valid sign failed");
            result.put("state", "0");
            result.put("data", "");
            result.put("msg", "签名校验错误");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyMuzhiSession(MuzhiSession session) {
        Map<String, String> result = new HashMap<String, String>();

        logger.debug("verifyMuzhiSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsename()) || StringUtils.isBlank(session.getSign())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String key = channelGame.getConfigParamsList().get(0);
        try {
            String validSign = channelUtilsService.muzhiMd5(session.getUsename() + key);

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyMuzhiSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String verifyMuzhi(HttpServletRequest request) {
        String content = request.getParameter("content");
        content = new String(Base64.decode(content));
        String sign = request.getParameter("sign");

        ChannelStatsLogger.info(ChannelStatsLogger.MUZHI, content + " sign: " + sign);

        Map<String, Object> params = JsonMapper.toObject(content, Map.class);
        String cp_order_id = params.get("cp_order_id").toString();
        int amount = Integer.valueOf(params.get("amount").toString());
        int payStatus = Integer.valueOf(params.get("payStatus").toString());

        Order order = basicRepository.getOrderByOrderId(cp_order_id);
        if (order == null) {
            return "failed";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "failed";
        }
        try {
            String key = channelGame.getConfigParamsList().get(0);
            String validSign = channelUtilsService.muzhiMd5(content + "&key=" + key);
            if (StringUtils.equals(sign, validSign)) {
                logger.debug("verify muzhi valid sign success");

                if (payStatus != 0) {
                    orderService.payFail(order.getOrderId(), "平台通知订单支付失败");
                    return "success";
                }

                if (order.getAmount() > amount) {
                    ChannelStatsLogger.info(ChannelStatsLogger.MUZHI, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "success";
                }
                orderService.paySuccess(order.getOrderId());
                return "success";
            } else {
                logger.debug("verify muzhi valid sign failed");
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.MUZHI, content + " sign: " + sign, "verifyMuzhi error :" + e);
        }
        return "failed";
    }

    @Override
    public String verifyMuzhiwanSession(MuzhiwanSession session) {
        logger.debug("verifyMuzhiwanSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getToken())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String appkey = channelGame.getConfigParamsList().get(1);

        try {
            url = url + "?token=" + session.getToken() + "&appkey=" + appkey;
            return HttpUtils.get(url);
        } catch (Exception e) {
            logger.error("verifyMuzhiwanSession error", e);
            return "";
        }
    }

    @Override
    public String verifyMuzhiwan(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.MUZHIWAN, HttpUtils.getRequestParams(request).toString());

        String appkey = request.getParameter("appkey");
        String orderID = request.getParameter("orderID");
        String productName = request.getParameter("productName");
        String productDesc = request.getParameter("productDesc");
        String productID = request.getParameter("productID");
        String money = request.getParameter("money");
        String uid = request.getParameter("uid");
        String extern = request.getParameter("extern");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(extern);
        if (order == null) {
            return "can not find order";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "order info error";
        }

        String payKey = channelGame.getConfigParamsList().get(2);
        String content = appkey + orderID + productName + productDesc + productID + money + uid + extern + payKey;

        String validSign = MD5.encode(content);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify muzhiwan valid sign success");
            if (order.getAmount() > Integer.valueOf(money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.MUZHIWAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "SUCCESS";
            }

            orderService.paySuccess(order.getOrderId());
            return "SUCCESS";
        } else {
            logger.debug("verify muzhiwan valid sign failed");
            return "valid sign failed";
        }
    }

    @Override
    public String verifyKaopuSession(KaopuSession session) {
        logger.debug("verifyKaopuSession params : " + session.toString());

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("devicetype", session.getDevicetype());
        params.put("imei", session.getImei());
        params.put("sign", session.getSign());
        params.put("r", session.getR());
        params.put("tag", session.getTag());
        params.put("tagid", session.getTagid());
        params.put("appid", session.getAppid());
        params.put("channelkey", session.getChannelkey());
        params.put("openid", session.getOpenid());
        params.put("token", session.getToken());

        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Entry<String, Object> e : params.entrySet()) {
                sb.append(e.getKey() + "=" + e.getValue().toString().trim()
                        + "&");
            }
            sb.substring(0, sb.length() - 1);
        }

        url = url + "?" + sb.toString();

        logger.debug("url: " + url);

        try {
            return HttpUtils.get(url);
        } catch (Exception e) {
            logger.error("verifyKaopuSession error", e);
            return "";
        }
    }

    @Override
    public String verifyKaopu(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        String username = request.getParameter("username");
        String kpordernum = request.getParameter("kpordernum");
        String ywordernum = request.getParameter("ywordernum");
        String status = request.getParameter("status");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String gameserver = request.getParameter("gameserver");
        String errdesc = request.getParameter("errdesc");
        String paytime = request.getParameter("paytime");
        String gamename = request.getParameter("gamename");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(ywordernum);
        if (order == null) {
            result.put("code", "1003");
            result.put("msg", "找不到订单");
            result.put("sign", "");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("code", "1004");
            result.put("msg", "订单信息有误");
            result.put("sign", "");
            return JsonMapper.toJson(result);
        }

        String payKey = channelGame.getConfigParamsList().get(1);

        StringBuffer content = new StringBuffer();
        content.append(username).append("|").append(kpordernum).append("|")
                .append(ywordernum).append("|").append(status).append("|")
                .append(paytype).append("|").append(amount).append("|")
                .append(gameserver).append("|").append(errdesc).append("|")
                .append(paytime).append("|").append(gamename).append("|")
                .append(payKey);

        ChannelStatsLogger.info(ChannelStatsLogger.KAOPU, content.toString() + " sign: " + sign);

        String validSign = MD5.encode(content.toString());
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify kaopu valid sign success");

            if (!"1".equals(status)) {
                orderService.payFail(ywordernum, "回调返回支付失败");

                result.put("code", "1000");
                result.put("msg", "success");
                result.put("sign", MD5.encode("1000|" + payKey));
                return JsonMapper.toJson(result);
            }

            if (order.getAmount() > Integer.valueOf(amount)) {
                ChannelStatsLogger.info(ChannelStatsLogger.KAOPU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");

                result.put("code", "1009");
                result.put("msg", "金额不正确");
                result.put("sign", MD5.encode("1009|" + payKey));
                return JsonMapper.toJson(result);
            }
            orderService.paySuccess(order.getOrderId());
            result.put("code", "1000");
            result.put("msg", "success");
            result.put("sign", MD5.encode("1000|" + payKey));
            return JsonMapper.toJson(result);
        } else {
            logger.debug("verify kaopu valid sign failed: " + content + " sign: " + sign);

            result.put("code", "1002");
            result.put("msg", "验证签名失败");
            result.put("sign", MD5.encode("1002|" + payKey));
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyGametanziSession(GametanziSession session) {
        Map<String, String> result = new HashMap<String, String>();

        logger.debug("verifyGametanziSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyGametanziSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyGametanzi(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.GAMETANZI, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");

        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify gametanzi valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.GAMETANZI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify gametanzi valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyWeidongSession(WeidongSession session) {
        logger.debug("verifyWeidongSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getAppid()) || StringUtils.isBlank(session.getUid()) || StringUtils.isBlank(session.getState())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String login_key = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", session.getAppid());
        params.put("uid", session.getUid());
        params.put("state", session.getState());
        params.put("flag", MD5.encode(session.getAppid() + session.getUid() + session.getState() + login_key));
        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyWeidongSession error", e);
            return "";
        }
    }

    @Override
    public String verifyWeidong(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.WEIDONG, HttpUtils.getRequestParams(request).toString());

        String uid = request.getParameter("uid");
        String money = request.getParameter("money");
        String time = request.getParameter("time");
        String sid = request.getParameter("sid");
        String orderid = request.getParameter("orderid");
        String ext = request.getParameter("ext");
        String flag = request.getParameter("flag");

        Order order = basicRepository.getOrderByOrderId(ext);
        if (order == null) {
            return "-1";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "-1";
        }

        String pay_key = channelGame.getConfigParamsList().get(2);
        String content = uid + money + time + sid + orderid + ext + pay_key;

        String validSign = MD5.encode(content);
        if (StringUtils.equals(validSign, flag)) {
            logger.debug("verify weidong valid sign success");

            if (order.getAmount() > Double.valueOf(money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.WEIDONG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");

                return "5";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "1";
            }
        } else {
            logger.debug("verify weidong valid sign failed: " + HttpUtils.getRequestParams(request).toString());
            return "3";
        }
    }

    @Override
    public String verifyEdgSession(EdgSession session) {
        logger.debug("verifyEdgSession params : " + session.toString());

        Map<String, String> result = new HashMap<String, String>();

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUserId()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getTimestamp())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            String content = session.getUserId() + "&" + session.getTimestamp() + "&" + appkey;
            String validSign = MD5.encode(content);

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyEdgSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyEdg(HttpServletRequest request) {
        String account = request.getParameter("account");
        String amount = request.getParameter("amount");
        String orderid = request.getParameter("orderid");
        String result = request.getParameter("result");
        String channel = request.getParameter("channel");
        String msg = request.getParameter("msg");
        String extrainfo = request.getParameter("extrainfo");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(extrainfo);
        if (order == null) {
            return "-1";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "-1";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("account", account);
        params.put("amount", amount);
        params.put("orderid", orderid);
        params.put("result", result);
        params.put("channel", channel);
        params.put("msg", msg);
        params.put("extrainfo", extrainfo);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.WEIDONG, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify edg valid sign success");

            if (!"0".equals(result)) {
                orderService.payFail(order.getOrderId(), "平台返回订单支付失败");
                return extrainfo;
            }

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.EDG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");

                return extrainfo;
            } else {
                orderService.paySuccess(order.getOrderId());
                return extrainfo;
            }
        } else {
            logger.debug("verify edg valid sign failed");
        }
        return "-3";
    }

    @Override
    @SuppressWarnings({"deprecation", "rawtypes"})
    public String verifyTencent(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.TENCENT, HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String appid = request.getParameter("appid");
        String openid = request.getParameter("openid");
        String openkey = request.getParameter("openkey");
        String pay_token = request.getParameter("pay_token");
        String pf = request.getParameter("pf");
        String pfkey = request.getParameter("pfkey");
        String zoneid = request.getParameter("zoneid");
        String session_id = request.getParameter("session_id");
        String session_type = request.getParameter("session_type");
        String orderId = request.getParameter("orderId");                //指点订单号

        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String scriptName = "/mpay/pay_m";
        String protocol = "https";

        Order order = basicRepository.getOrderByOrderId(orderId);

        if (null == order) {
            result.put("code", "1");
            result.put("msg", "找不到订单");
            return JsonMapper.toJson(result);
        }

        if (order.getStatus() == Order.ORDER_STATUS_PAYMENT_SUCCESS) {
            result.put("code", "1");
            result.put("msg", "订单已经支付成功");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            result.put("code", "2");
            result.put("msg", "订单信息有误");
            return JsonMapper.toJson(result);
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        String serverName = channelGame.getConfigParamsList().get(1);
        int gameCoinRatio = Integer.valueOf(channelGame.getConfigParamsList().get(2));
        logger.debug(String.format("-------------------------------appid=%s,channel=%d,gameCoinRation=%d,amount=%d", channelGame.getGameId(), channelGame.getChannelId(), gameCoinRatio, order.getAmount()));
        OpenApiV3 sdk = new OpenApiV3(appid, appkey);
        sdk.setServerName(serverName);

        HashMap<String, String> cookie = new HashMap<String, String>();
        cookie.put("session_id", session_id);
        cookie.put("session_type", session_type);
        cookie.put("org_loc", URLEncoder.encode(scriptName));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("openid", openid);
        params.put("openkey", openkey);
        params.put("pay_token", StringUtils.equals("openid", session_id) ? pay_token : "");
        params.put("ts", ts);
        params.put("pf", pf);
        params.put("pfkey", pfkey);
        params.put("zoneid", zoneid);
        params.put("amt", String.valueOf(order.getAmount() / gameCoinRatio));
        params.put("billno", orderId);

        HashMap<String, String> queryParam = new HashMap<String, String>();
        queryParam.put("appid", appid);
        queryParam.put("openid", openid);
        queryParam.put("openkey", openkey);
        //params.put("pay_token", StringUtils.equals("openid", session_id) ? pay_token : "");
        queryParam.put("ts", ts);
        queryParam.put("pf", pf);
        queryParam.put("pfkey", pfkey);
        queryParam.put("zoneid", zoneid);
        //params.put("amt", String.valueOf(order.getAmount() / gameCoinRatio));
        //params.put("billno", orderId);
//        checkTencent(sdk,cookie,queryParam,protocol);
        try {
            String resp = sdk.api_pay(scriptName, cookie, params, protocol);
            if (StringUtils.isBlank(resp)) {
                result.put("code", "5");
                result.put("msg", "sdk.api_pay返回空");
                return JsonMapper.toJson(result);
            }

            Map respMap = JsonMapper.toObject(resp, Map.class);
            String ret = respMap.get("ret").toString();
            if (!"0".equals(ret)) {
                //如果余额不足，可能服务端延迟，放入轮询
                if ("1004".equals(ret)) {
                    TencentNotifyParams notifyParams = new TencentNotifyParams();
                    notifyParams.setAppid(appid);
                    notifyParams.setOpenid(openid);
                    notifyParams.setOpenkey(openkey);
                    notifyParams.setPay_token(pay_token);
                    notifyParams.setPf(pf);
                    notifyParams.setPfkey(pfkey);
                    notifyParams.setZoneid(zoneid);
                    notifyParams.setSession_id(session_id);
                    notifyParams.setSession_type(session_type);
                    notifyParams.setOrderId(orderId);
                    notifyParams.setAmount(order.getAmount());
                    notifyParams.setAppkey(appkey);
                    notifyParams.setServerName(serverName);
                    notifyParams.setGameCoinRatio(gameCoinRatio);
                    notifyParams.setPollTimes(1);

                    redisUtil.setLpush("tencent_poll_order", JsonMapper.toJson(notifyParams));
                }
                String msg = respMap.get("msg").toString();
                result.put("code", "4");
//                result.put("msg", msg);
                result.put("msg", JsonMapper.toJson(respMap));
                return JsonMapper.toJson(result);
            }

            orderService.paySuccess(order.getOrderId());
            result.put("code", "0");
            result.put("msg", "success");
            return JsonMapper.toJson(result);
        } catch (OpensnsException e) {
            System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
            result.put("code", "6");
            result.put("msg", "服务器异常" + e.getMessage());
            return JsonMapper.toJson(result);
        } catch (InterruptedException e) {
            System.out.printf("set tencent poll notify redis error %s\n", e.getMessage());
            e.printStackTrace();
            result.put("code", "6");
            result.put("msg", "服务器异常" + e.getMessage());
            return JsonMapper.toJson(result);
        }
    }

    private String checkTencent(OpenApiV3 sdk, HashMap<String, String> cookie, HashMap<String, String> param, String protol) {
        String scriptName = "/mpay/get_balance_m";
        cookie.put("org_loc", URLEncoder.encode(scriptName));
        logger.info("!!!!!!" + JsonMapper.toJson(param));
        logger.info("======" + JsonMapper.toJson(cookie));
        try {
            String res = sdk.api_msdk(scriptName, cookie, param, protol);
            logger.info("Query Result " + res);
            return res;
        } catch (OpensnsException e) {
            logger.info("Query exception " + e.getMessage());
            e.printStackTrace();
        }
        return "";

    }

    @Override
    public String verifyTencent2(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.TENCENT, HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String appid = request.getParameter("appid");
        String openid = request.getParameter("openid");
        String openkey = request.getParameter("openkey");
        String pay_token = request.getParameter("pay_token");
        String pf = request.getParameter("pf");
        String pfkey = request.getParameter("pfkey");
        String zoneid = request.getParameter("zoneid");
        String session_id = request.getParameter("session_id");
        String session_type = request.getParameter("session_type");
        String orderId = request.getParameter("orderId");                //指点订单号

        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String scriptName = "/mpay/pay_m";
        String protocol = "http";

        Order order = basicRepository.getOrderByOrderId(orderId);

        if (null == order) {
            result.put("code", "1");
            result.put("msg", "找不到订单");
            return JsonMapper.toJson(result);
        }

        if (order.getStatus() == Order.ORDER_STATUS_PAYMENT_SUCCESS) {
            result.put("code", "1");
            result.put("msg", "订单已经支付成功");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            result.put("code", "2");
            result.put("msg", "订单信息有误");
            return JsonMapper.toJson(result);
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        String serverName = channelGame.getConfigParamsList().get(1);
        int gameCoinRatio = Integer.valueOf(channelGame.getConfigParamsList().get(2));
        logger.debug(String.format("-------------------------------appid=%s,channel=%d,gameCoinRation=%d,amount=%d", channelGame.getGameId(), channelGame.getChannelId(), gameCoinRatio, order.getAmount()));
        MsOpenApiV3 sdk = new MsOpenApiV3(appid, appkey);
        sdk.setServerName(serverName);

        HashMap<String, String> cookie = new HashMap<String, String>();
        cookie.put("session_id", session_id);
        cookie.put("session_type", session_type);
        cookie.put("org_loc", URLEncoder.encode(scriptName));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("openid", openid);
        params.put("openkey", openkey);
        params.put("pay_token", StringUtils.equals("openid", session_id) ? pay_token : "");
        params.put("ts", ts);
        params.put("pf", pf);
        params.put("pfkey", pfkey);
        params.put("zoneid", zoneid);
        params.put("amt", String.valueOf(order.getAmount() / gameCoinRatio));
        params.put("billno", orderId);
        try {
            String resp = sdk.api_pay(scriptName, cookie, params, protocol);
            if (StringUtils.isBlank(resp)) {
                result.put("code", "5");
                result.put("msg", "sdk.api_pay返回空");
                return JsonMapper.toJson(result);
            }

            Map respMap = JsonMapper.toObject(resp, Map.class);
            String ret = respMap.get("ret").toString();
            if (!"0".equals(ret)) {
                //如果余额不足，可能服务端延迟，放入轮询
                if ("1004".equals(ret)) {
                    TencentNotifyParams notifyParams = new TencentNotifyParams();
                    notifyParams.setAppid(appid);
                    notifyParams.setOpenid(openid);
                    notifyParams.setOpenkey(openkey);
                    notifyParams.setPay_token(pay_token);
                    notifyParams.setPf(pf);
                    notifyParams.setPfkey(pfkey);
                    notifyParams.setZoneid(zoneid);
                    notifyParams.setSession_id(session_id);
                    notifyParams.setSession_type(session_type);
                    notifyParams.setOrderId(orderId);
                    notifyParams.setAmount(order.getAmount());
                    notifyParams.setAppkey(appkey);
                    notifyParams.setServerName(serverName);
                    notifyParams.setGameCoinRatio(gameCoinRatio);
                    notifyParams.setPollTimes(1);

                    redisUtil.setLpush("tencent_poll_order_msdk", JsonMapper.toJson(notifyParams));
                }
                String msg = respMap.get("msg").toString();
                result.put("code", "4");
//                result.put("msg", msg);
                result.put("msg", JsonMapper.toJson(respMap));
                return JsonMapper.toJson(result);
            }

            orderService.paySuccess(order.getOrderId());
            result.put("code", "0");
            result.put("msg", "success");
            return JsonMapper.toJson(result);
        } catch (OpensnsException e) {
            System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
            result.put("code", "6");
            result.put("msg", "服务器异常" + e.getMessage());
            return JsonMapper.toJson(result);
        } catch (InterruptedException e) {
            System.out.printf("set tencent poll notify redis error %s\n", e.getMessage());
            e.printStackTrace();
            result.put("code", "6");
            result.put("msg", "服务器异常" + e.getMessage());
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyUucunSession(UucunSession session) {
        logger.debug("verifyUucunSession params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getToken())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", session.getToken());
        try {
            String json = URLEncoder.encode(JsonMapper.toJson(params), "utf-8");
            url = url + "?jsonString=" + json;
            return HttpUtils.get(url);
        } catch (UnsupportedEncodingException e) {
            logger.error("verifyUucunSession error", e);
        }
        return "";
    }

    @Override
    public String verifyUucun(HttpServletRequest request) {
        String callback_rsp = request.getParameter("callback_rsp");
        String callback_appkey = request.getParameter("callback_appkey");
        String decrypt_rsp = "";
        try {
            DesUtils desUtils = new DesUtils();
            desUtils.setDesKEY("2SoXIhFB");
            String decrypt_appKey = desUtils.decrypt(callback_appkey);

            desUtils.setDesKEY(Constants.UUCUN_APPKEY_DESKEY.get(decrypt_appKey));
            decrypt_rsp = desUtils.decrypt(callback_rsp);

            ChannelStatsLogger.info(ChannelStatsLogger.UUCUN, decrypt_rsp);

            String decrypt_rsp_args[] = decrypt_rsp.split("&");
            String orderId = decrypt_rsp_args[2].split("=")[1];
            String rspCode = decrypt_rsp_args[3].split("=")[1];
            String actualTxnAmt = decrypt_rsp_args[5].split("=")[1];
            String sign = decrypt_rsp_args[7].split("=")[1];

            Order order = basicRepository.getOrderByOrderId(orderId);
            if (order == null) {
                return "-1";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "-1";
            }

            String key = channelGame.getConfigParamsList().get(1);

            String validSign = MD5.encode(decrypt_rsp.substring(0, decrypt_rsp.lastIndexOf("&")) + "&key=" + key);
            if (StringUtils.equalsIgnoreCase(validSign, sign)) {
                if (!"000000".equals(rspCode)) {
                    orderService.payFail(order.getOrderId(), "平台返回订单支付失败");
                    return "1";
                }

                logger.debug("verify uucun valid sign success");
                if (order.getAmount() > Integer.valueOf(actualTxnAmt)) {
                    ChannelStatsLogger.info(ChannelStatsLogger.UUCUN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");

                    return "1";
                } else {
                    orderService.paySuccess(order.getOrderId());
                    return "1";
                }
            } else {
                logger.debug("verify uucun valid sign failed");
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.UUCUN, decrypt_rsp, "verifyUucun error :" + e);
        }
        return "-1";
    }

    @Override
    public String verifyKaiuc(HttpServletRequest request) {
        Map<String, String> params = HttpUtils.getRequestParams(request);
        ChannelStatsLogger.info(ChannelStatsLogger.KAIUC, params.toString());

        String areaId = request.getParameter("areaId");
        String callbackInfo = request.getParameter("callbackInfo");
        String uid = request.getParameter("uid");
        String orderId = request.getParameter("orderId");
        String status = request.getParameter("status");
        String payType = request.getParameter("payType");
        String fee = request.getParameter("fee");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(callbackInfo);
        if (null == order) {
            return "fail";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "fail";
        }
        String clientSecret = channelGame.getConfigParamsList().get(0);
        String validSign = MD5.encode(areaId + callbackInfo + uid + orderId + status + payType + fee + clientSecret);

        if (StringUtils.equalsIgnoreCase(sign, validSign)) {
            logger.debug("verify kaiuc valid sign success");
            if (!"1".equals(status)) {
                orderService.payFail(order.getOrderId(), "平台返回订单支付失败");
                return "success";
            }

            if (order.getAmount() > Double.valueOf(fee) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.KAIUC, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "success";
        } else {
            logger.debug("verify kaiuc valid sign failed");
        }
        return "fail";
    }

    @Override
    public String verifyLiebaoSession(LiebaoSession session) {
        ChannelStatsLogger.info(ChannelStatsLogger.LIEBAO, JsonMapper.toJson(session));
        Map<String, String> result = new HashMap<String, String>();

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
        if (channelGame == null) {
            result.put("status", "1");
            result.put("msg", "platorm not unition");
            return JsonMapper.toJson(result);
        }

        String url = channelGame.getConfigParamsList().get(0);
        String appkey = channelGame.getConfigParamsList().get(1);
        try {
            Map<String, Object> signParams = new LinkedHashMap<String, Object>();
            signParams.put("gameid", session.getGameid());
            signParams.put("username", session.getUsername());
            signParams.put("logintime", session.getLogintime());
            signParams.put("appkey", appkey);

            String validSign = Sign.signByMD5Unsort(signParams, "");

            Map<String, Object> params = new LinkedHashMap<String, Object>(16);
            params.put("gameid", session.getGameid());
            params.put("username", session.getUsername());
            params.put("logintime", session.getLogintime());
            params.put("sign", validSign);
            String returnMsg = HttpUtils.doPost(url, params);
            JSONObject returnJson = new JSONObject(returnMsg);
            if (returnJson.get("status").equals(true)) {
                result.put("code", "0");
                result.put("msg", "校验成功！");
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "1");
                result.put("msg", returnJson.get("msg").toString());
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("verifyLiebaoSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyLiebao(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.LIEBAO, HttpUtils.getRequestParams(request).toString());
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.LIEBAO, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");

        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify liebao valid sign success");
            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.LIEBAO, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "success";
        } else {
            logger.debug("verify liebao valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyLeshanSession(LeshanSession session) {
        logger.debug("verify 07073 session params : " + session.toString());
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getPid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String key = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", session.getUsername());
        params.put("token", session.getToken());
        params.put("pid", session.getPid());
        params.put("sign", Sign.signByMD5(params, key));

        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verify 07073 session error", e);
            return "";
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public String verifyLeshan(HttpServletRequest request) {
        String data = request.getParameter("data");
        ChannelStatsLogger.info(ChannelStatsLogger.LESHAN, data);

        Map<String, Object> params = JsonMapper.toObject(data, Map.class);
        String orderId = DES.decryptBase64((String) params.get("extendsInfo"), Constants.BASE64_ORDERID_KEY);
        String sign = (String) params.get("sign");
        float amount = Float.valueOf((String) params.get("amount"));

        Order order = basicRepository.getOrderByOrderId(orderId);
        if (null == order) {
            return "fail";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "fail";
        }

        String key = channelGame.getConfigParamsList().get(1);
        params.remove("sign");
        params.remove("extendsInfo");
        String validSign = Sign.signByMD5(params, key);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify 07073 valid sign success");
            if (order.getAmount() > (int) (amount * 100)) {
                ChannelStatsLogger.info(ChannelStatsLogger.LESHAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "succ";
        } else {
            logger.debug("verify 07073 valid sign fail");
        }
        return "fail";
    }

    @Override
    public String atetPaypoing(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        String appId = request.getParameter("appId");
        String platformId = request.getParameter("platformId");
        String amount = request.getParameter("amount");

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(platformId) || StringUtils.isBlank(amount)) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(platformId), Long.valueOf(appId));
        String paypointInfo = channelGame.getConfigParamsList("\\|").get(1);

        logger.debug("paypointInfo: " + paypointInfo);

        List<AtetPaypoint> points = JsonMapper.stringToList(paypointInfo, AtetPaypoint.class);
        if (!CollectionUtils.isEmpty(points)) {
            for (AtetPaypoint item : points) {
                if (StringUtils.equals(item.getAmount(), amount)) {
                    result.put("code", item.getCode());
                    result.put("name", item.getName());

                    return JsonMapper.toJson(result);
                }
            }
        }
        return "";
    }

    @Override
    public String verifyAtet(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.ATET, HttpUtils.getRequestParams(request).toString());

        String transdata = request.getParameter("transdata");
        String sign = request.getParameter("sign");
        if (StringUtils.isBlank(transdata) || StringUtils.isBlank(sign)) {
            return "fail";
        }

        AtetPayCallback back = JsonMapper.toObject(transdata, AtetPayCallback.class);
        String orderId = back.getExOrderNo();
        Order order = orderService.getOrderByOrderId(orderId);
        if (null == order) {
            return "fail";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "fail";
        }

        String publicKey = channelGame.getConfigParamsList("\\|").get(0);

        if (channelUtilsService.verifyAtet(transdata, sign.replace(" ", "+"), publicKey)) {
            logger.debug("verify atet valid sign success");
            if (back.getResult() == 1) {
                orderService.payFail(order.getOrderId(), "callback notify order payfail");
                return "success";
            }

            if (back.getAmount() >= order.getAmount()) {
                orderService.paySuccess(order.getOrderId());
            } else {
                orderService.payFail(order.getOrderId(), "order amount error");
                ChannelStatsLogger.error(ChannelStatsLogger.ATET, order.getOrderId(), "order amount error");
            }
            return "success";
        } else {
            logger.debug("verify atet valid sign failed");
        }
        return "fail";
    }

    @Override
    public String verifyShenqi(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.SHENQI, HttpUtils.getRequestParams(request).toString());

        String orderNo = request.getParameter("OrderNo");
        String outPayNo = request.getParameter("OutPayNo");
        String userID = request.getParameter("UserID");
        String serverNo = request.getParameter("ServerNo");
        String payType = request.getParameter("PayType");
        String money = request.getParameter("Money");
        String pMoney = request.getParameter("PMoney");
        String payTime = request.getParameter("PayTime");
        String sign = request.getParameter("Sign");

        Order order = orderService.getOrderByOrderId(outPayNo);
        if (null == order) {
            return "0|order unfind";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "0|wrong order";
        }

        String serverKey = channelGame.getConfigParamsList().get(0);
        String validSign = MD5.encode(orderNo + outPayNo + userID + serverNo + payType + money + pMoney + payTime + serverKey);

        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify 2yl valid sign success");

            if (Float.valueOf(money) * 100 >= order.getAmount()) {
                orderService.paySuccess(order.getOrderId());
            } else {
                orderService.payFail(order.getOrderId(), "order amount error");
                ChannelStatsLogger.error(ChannelStatsLogger.SHENQI, order.getOrderId(), "order amount error");
            }

            return "1";
        } else {
            logger.debug("verify 2yl valid sign failed");
            return "0|valid sign fail";
        }
    }

    @Override
    public String verifyHaimaSession(HaimaSession session) {
        Map<String, String> result = new HashMap<String, String>();

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getAppId()) || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getUid())) {
            return "";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", session.getAppId());
        params.put("t", session.getToken());
        params.put("uid", session.getUid());

        try {
            String retStr = HttpUtils.post(url, params);
            result.put("code", retStr.indexOf("success") > -1 ? "0" : "1");
        } catch (Exception e) {
            logger.error("verify haima session error", e);
            result.put("code", "2");
        }
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyHaima(HttpServletRequest request) {
        Map<String, String> params = HttpUtils.getRequestParams(request);
        ChannelStatsLogger.info(ChannelStatsLogger.HAIMA, params.toString());

        String out_trade_no = request.getParameter("out_trade_no");
        String sign = request.getParameter("sign");
        String trade_status = request.getParameter("trade_status");
        String total_fee = request.getParameter("total_fee");

        Order order = basicRepository.getOrderByOrderId(out_trade_no);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(1);

        Map<String, Object> signMap = new LinkedHashMap<String, Object>();
        try {
            signMap.put("notify_time", URLEncoder.encode(request.getParameter("notify_time"), "utf-8"));
            signMap.put("appid", URLEncoder.encode(request.getParameter("appid"), "utf-8"));
            signMap.put("out_trade_no", URLEncoder.encode(out_trade_no, "utf-8"));
            signMap.put("total_fee", URLEncoder.encode(total_fee, "utf-8"));
            signMap.put("subject", URLEncoder.encode(request.getParameter("subject"), "utf-8"));
            signMap.put("body", URLEncoder.encode(request.getParameter("body"), "utf-8"));
            signMap.put("trade_status", URLEncoder.encode(trade_status, "utf-8"));
        } catch (Exception e) {
            logger.error("verifyHaima URLEncoder encode error", e);
            return "serverError";
        }

        String validSign = Sign.signByMD5Unsort(signMap, appkey);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify haima valid sign success");
            if ("1".equals(trade_status)) {
                if (order.getAmount() > Float.valueOf(total_fee) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.HAIMA, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
            } else {
                orderService.payFail(order.getOrderId(), trade_status);
            }

            return "success";
        } else {
            logger.debug("verify haima valid sign fail");
            return "validSignFail";
        }
    }

    @Override
    public String pengyouwanPaypoing(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        String appId = request.getParameter("appId");
        String platformId = request.getParameter("platformId");
        String amount = request.getParameter("amount");

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(platformId) || StringUtils.isBlank(amount)) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(platformId), Long.valueOf(appId));
        String paypointInfo = channelGame.getConfigParamsList("\\|").get(1);

        logger.debug("paypointInfo: " + paypointInfo);

        List<PengyouwanPaypoint> points = JsonMapper.stringToList(paypointInfo, PengyouwanPaypoint.class);
        if (!CollectionUtils.isEmpty(points)) {
            for (PengyouwanPaypoint item : points) {
                if (StringUtils.equals(item.getAmount(), amount)) {
                    result.put("code", item.getCode());
                    result.put("name", item.getName());

                    return JsonMapper.toJson(result);
                }
            }
        }
        return "";
    }

    @Override
    public String verifyPengyouwan(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
            String ln;
            StringBuffer sb = new StringBuffer();
            while ((ln = in.readLine()) != null) {
                sb.append(ln);
                sb.append("\r\n");
            }

            PengyouwanPayCallback back = JsonMapper.toObject(sb.toString(), PengyouwanPayCallback.class);
            if (null == back) {
                result.put("ack", "101");
                result.put("msg", "params error");
                return JsonMapper.toJson(result);
            }

            ChannelStatsLogger.info(ChannelStatsLogger.PENGYOUWAN, back.toString());

            Order order = basicRepository.getOrderByOrderId(back.getCp_orderid());
            if (null == order) {
                result.put("ack", "102");
                result.put("msg", "order unexist");
                return JsonMapper.toJson(result);
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                result.put("ack", "103");
                result.put("msg", "a wrong order");
                return JsonMapper.toJson(result);
            }

            String secretKey = channelGame.getConfigParamsList("\\|").get(0);
            String validSign = MD5.encode(secretKey + back.getCp_orderid() + back.getCh_orderid() + back.getAmount());
            if (StringUtils.equals(back.getSign(), validSign)) {
                logger.debug("verify verifyPengyouwan valid sign success");

                if (order.getAmount() > Float.valueOf(back.getAmount()) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.PENGYOUWAN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }

                result.put("ack", "200");
                result.put("msg", "success");
                return JsonMapper.toJson(result);
            } else {
                logger.debug("verify verifyPengyouwan valid sign fail");

                result.put("ack", "104");
                result.put("msg", "valid sign fail");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("verifyPengyouwan error", e);
            result.put("ack", "105");
            result.put("msg", "server error");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verify3899Session(TennSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getAppId()) || StringUtils.isBlank(session.getSessionId())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String loginkey = channelGame.getConfigParamsList().get(1);

        try {
            String decodeSid = URLDecoder.decode(session.getSessionId(), "utf-8");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ac", "check");
            params.put("appid", session.getAppId());
            params.put("sessionid", decodeSid);
            params.put("time", System.currentTimeMillis());

            Map<String, Object> signParams = new LinkedHashMap<String, Object>();
            signParams.put("ac", "check");
            signParams.put("appid", session.getAppId());
            signParams.put("sessionid", URLEncoder.encode(decodeSid, "utf-8"));
            signParams.put("time", System.currentTimeMillis());
            params.put("sign", Sign.signByMD5(signParams, loginkey));

            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verify3899Session error", e);
            return "";
        }

    }

    @Override
    public String verify3899(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.TENN, params.toString());

        String cporderid = request.getParameter("cporderid");
        String sign = request.getParameter("sign");
        String amount = request.getParameter("amount");

        Order order = basicRepository.getOrderByOrderId(cporderid);
        if (null == order) {
            return "can not find order";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "order info error";
        }
        try {
            String paykey = channelGame.getConfigParamsList().get(2);
            params.put("extinfo", URLEncoder.encode(params.get("extinfo").toString(), "utf-8"));
            params.remove("sign");                                                        //sign参数不参与签名
            String validSign = Sign.signByMD5(params, paykey);

            if (StringUtils.equals(sign, validSign)) {
                logger.debug("verify verify3899 valid sign success");
                if (order.getAmount() > Float.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.TENN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }

                return "SUCCESS";
            } else {
                logger.debug("verify verify3899 valid sign fail");
                return "valid sign fail";
            }
        } catch (Exception e) {
            logger.error("verify3899 error", e);
            return "server error";
        }
    }

    @Override
    public String verifyLiulianSession(LiulianSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getAppId()) || StringUtils.isBlank(session.getSessionId())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String appKey = channelGame.getConfigParamsList().get(1);
        String appsecret = channelGame.getConfigParamsList().get(2);
        String privateKey = MD5.encode(appKey + "#" + appsecret);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", session.getAppId());
        params.put("appkey", appKey);
        params.put("sid", session.getSessionId());
        params.put("sign", MD5.encode(session.getAppId() + appKey + privateKey + session.getSessionId()));

        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verify3899Session error", e);
            return "";
        }
    }

    @Override
    public String verifyLiulian(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.LIULIAN, params.toString());

        String appid = request.getParameter("appid");
        String orderId = request.getParameter("orderId");
        String userId = request.getParameter("userId");
        String serverId = request.getParameter("serverId");
        String roleId = request.getParameter("roleId");
        String roleName = request.getParameter("roleName");
        String money = request.getParameter("money");
        String extInfo = request.getParameter("cpExtInfo");
        String status = request.getParameter("status");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(extInfo);
        if (null == order) {
            return "\"FAILURE\"";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "\"FAILURE\"";
        }

        String appKey = channelGame.getConfigParamsList().get(1);
        String appsecret = channelGame.getConfigParamsList().get(2);
        String privateKey = MD5.encode(appKey + "#" + appsecret);

        String validSign = MD5.encode(appid + privateKey + orderId + userId + serverId + roleId + roleName + money + extInfo + status);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify verifyLiulian valid sign success");
            if ("1".equals(status)) {
                if (order.getAmount() > Float.valueOf(money) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.LIULIAN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
            } else {
                orderService.payFail(order.getOrderId(), "liulian notify pay fail");
            }
            return "\"SUCCESS\"";
        } else {
            logger.debug("verify verifyLiulian valid sign fail");
            return "\"FAILURE\"";
        }
    }

    @Override
    public String verifyXunlei(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.XUNLEI, params.toString());

        String orderid = request.getParameter("orderid");
        String user = request.getParameter("user");
        String gold = request.getParameter("gold");
        String money = request.getParameter("money");
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        String ext = request.getParameter("ext");

        Order order = basicRepository.getOrderByOrderId(ext);
        if (null == order) {
            return "-1";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "-1";
        }

        String requestIp = IpUtils.getIpAddr(request);
        logger.debug("xunlei paycallback requestIp: " + requestIp);
        if (StringUtils.isBlank(requestIp)) {
            return "-1";
        }

        String legalIp = channelGame.getConfigParamsList().get(1);
        if (!Arrays.asList(legalIp.split("\\|")).contains(requestIp)) {
            return "-6";
        }

        String payKey = channelGame.getConfigParamsList().get(0);
        String validSign = MD5.encode(orderid + user + gold + money + time + payKey);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify verifyXunlei valid sign success");
            if (order.getAmount() > Float.valueOf(money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.XUNLEI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "1";
        } else {
            logger.debug("verify verifyXunlei valid sign fail");
            return "-2";
        }
    }

    @Override
    public String verifyGuopanSession(GuopanSession session) {
        Map<String, String> result = new HashMap<String, String>();

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getAppId()) || StringUtils.isBlank(session.getGameUin()) || StringUtils.isBlank(session.getToken())) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            result.put("code", "2");
            return JsonMapper.toJson(result);
        }

        String url = channelGame.getConfigParamsList().get(0);
        String secretKey = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("game_uin", session.getGameUin());
        params.put("appid", session.getAppId());
        params.put("token", session.getToken());
        params.put("t", System.currentTimeMillis());
        params.put("sign", MD5.encode(session.getGameUin() + session.getAppId() + params.get("t").toString() + secretKey));

        try {
            String retStr = HttpUtils.post(url, params);
            logger.debug("verifyGuopanSession retStr{} ", retStr);
            if ("true".equals(retStr)) {
                result.put("code", "0");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("verifyGuopanSession error", e);
        }
        result.put("code", "3");
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyGuopan(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.GUOPAN, params.toString());

        String serialNumber = request.getParameter("serialNumber");
        String money = request.getParameter("money");
        String status = request.getParameter("status");
        String t = request.getParameter("t");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(serialNumber);
        if (null == order) {
            return "fail";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "fail";
        }

        String secretKey = channelGame.getConfigParamsList().get(1);
        String validSign = MD5.encode(serialNumber + money + status + t + secretKey);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify verifyGuopan valid sign success");
            if ("1".equals(status)) {
                if (order.getAmount() > Float.valueOf(money) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.GUOPAN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
            } else {
                orderService.payFail(order.getOrderId(), "guopan notify pay fail");
            }
            return "success";
        } else {
            logger.debug("verify verifyGuopan valid sign fail");
            return "fail";
        }
    }

    @Override
    public String verifyQxfySession(QxfySession session) {
        Map<String, String> result = new HashMap<String, String>();

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getSdk())
                || StringUtils.isBlank(session.getApp()) || StringUtils.isBlank(session.getUin()) || StringUtils.isBlank(session.getSess())) {
            result.put("code", "1");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            result.put("code", "2");
            return JsonMapper.toJson(result);
        }

        String url = channelGame.getConfigParamsList().get(0);

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("sdk", session.getSdk());
            params.put("app", session.getApp());
            params.put("uin", URLEncoder.encode(session.getUin(), "utf-8"));
            params.put("sess", URLEncoder.encode(session.getSess(), "utf-8"));

            String retStr = HttpUtils.getZmData(url, params);
            logger.debug("verifyQxfySession retStr: " + retStr);

            if (StringUtils.indexOf(retStr, "0") > -1) {
                result.put("code", "0");
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "3");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("verifyQxfySession error ", e);
            result.put("code", "4");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyQxfy(HttpServletRequest request) {
        Map<String, Object> params = HttpUtils.getRequestParamsObject(request);
        ChannelStatsLogger.info(ChannelStatsLogger.QXFY, params.toString());

        String sign = request.getParameter("sign");
        String cbi = request.getParameter("cbi");

        Order order = basicRepository.getOrderByOrderId(cbi);
        if (null == order) {
            return "FAIL";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "FAIL";
        }

        String secretKey = channelGame.getConfigParamsList().get(1);

        params.remove("sign");        //除sign以外参数参与签名
        String validSign = Sign.signByMD5(params, secretKey);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify verifyQxfy valid sign success");
            if (StringUtils.equals("1", request.getParameter("st"))) {
                if (order.getAmount() > Integer.valueOf(request.getParameter("fee"))) {
                    ChannelStatsLogger.info(ChannelStatsLogger.QXFY, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
            } else {
                orderService.payFail(order.getOrderId(), "qxfy back payFail");
            }
            return "SUCCESS";
        } else {
            logger.debug("verify verifyQxfy valid sign fail");
            return "FAIL";
        }
    }

    @Override
    public String verify19game(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.ONGAME, HttpUtils.getRequestParamsObject(request).toString());

        String orderId = request.getParameter("orderId");
        String user_id = request.getParameter("user_id");
        String system_account = request.getParameter("system_account");
        String char_id = request.getParameter("char_id");
        String amount = request.getParameter("amount");
        String game_coin = request.getParameter("game_coin");
        String offer = request.getParameter("offer");
        String cpOrderId = request.getParameter("cpOrderId");
        String success = request.getParameter("success");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(cpOrderId);
        if (null == order) {
            return "FAIL";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "FAIL";
        }

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("user_id", user_id);
        params.put("system_account", system_account);
        params.put("char_id", char_id);
        params.put("amount", amount);
        params.put("game_coin", game_coin);
        params.put("offer", offer);
        params.put("cpOrderId", cpOrderId);
        params.put("success", success);
        String secretKey = channelGame.getConfigParamsList().get(0);
        params.put("secret", secretKey);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify verify19game valid sign success");
            if (StringUtils.equals("1", success)) {
                if (order.getAmount() > Integer.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.ONGAME, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
            } else {
                orderService.payFail(order.getOrderId(), "19game back payFail");
            }
            return "SUCCESS";
        } else {
            logger.debug("verify verify19game valid sign fail");
            return "FAIL";
        }

    }

    @Override
    public String verifyLongxiangSession(LongxiangSession session) {
        Map<String, String> result = new HashMap<String, String>();

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyGametanziSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyLongxiang(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.LONGXIANG, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");

        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify longxiang valid sign success");
            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.LONGXIANG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "success";
        } else {
            logger.debug("verify longxiang valid sign failed");
            return "errorSign";
        }
    }

    @Override
    public String verifyLehihiSession(LehihiSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getPid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String key = channelGame.getConfigParamsList().get(1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", session.getUsername());
        params.put("token", session.getToken());
        params.put("pid", session.getPid());
        params.put("sign", Sign.signByMD5(params, key));

        try {
            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verify lehihi session error", e);
            return "";
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String verifyLehihi(HttpServletRequest request) {
        String data = request.getParameter("data");
        ChannelStatsLogger.info(ChannelStatsLogger.LEHIHI, data);

        Map<String, Object> params = JsonMapper.toObject(data, Map.class);
        String orderId = DES.decryptBase64((String) params.get("extendsInfo"), Constants.BASE64_ORDERID_KEY);
        String encodeExtendsInfo = "";
        try {
            encodeExtendsInfo = URLEncoder.encode((String) params.get("extendsInfo"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.info("extendsInfo encode error");
            return "fail";
        }
        params.put("extendsInfo", encodeExtendsInfo);
        String sign = (String) params.get("sign");
        float amount = Float.valueOf((String) params.get("amount"));

        Order order = basicRepository.getOrderByOrderId(orderId);
        if (null == order) {
            logger.info("order errror");
            return "fail";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
            return "fail";
        }

        String key = channelGame.getConfigParamsList().get(1);
        params.remove("sign");
        String validSign = Sign.signByMD5(params, key);
        logger.info("validSign: {} sign: {}", validSign, sign);
        if (StringUtils.equals(sign, validSign)) {
            logger.debug("verify lehihi valid sign success");
            logger.info("orderAmount: {} amount: {}", order.getAmount(), amount);
            if (order.getAmount() > (int) (amount * 100)) {
                ChannelStatsLogger.info(ChannelStatsLogger.LEHIHI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "succ";
        } else {
            logger.debug("verify lehihi valid sign fail");
        }
        return "fail";
    }

    @Override
    public String verifyKoudai(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String appid = request.getParameter("appid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("orderid", URLEncoder.encode(orderid, "utf-8"));
            params.put("username", URLEncoder.encode(username, "utf-8"));
            params.put("appid", appid);
            params.put("roleid", URLEncoder.encode(roleid, "utf-8"));
            params.put("serverid", serverid);
            params.put("paytype", URLEncoder.encode(paytype, "utf-8"));
            params.put("amount", amount);
            params.put("paytime", paytime);
            params.put("attach", URLEncoder.encode(attach, "utf-8"));
            params.put("appkey", appkey);

            ChannelStatsLogger.info(ChannelStatsLogger.KOUDAI, params.toString() + " sign: " + sign);

            String validSign = Sign.signByMD5Unsort(params, "");

            if (StringUtils.equalsIgnoreCase(validSign, sign)) {
                logger.debug("verify koudai valid sign success");
                if (order.getAmount() > Integer.valueOf(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.KOUDAI, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
                return "success";
            } else {
                logger.debug("verify koudai valid sign failed");
                return "errorSign";
            }
        } catch (Exception e) {
            logger.error("verifyKoudai error", e);
            return "error";
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String verifyYouleSession(YouleSession session) {
        Map<String, String> result = new HashMap<String, String>();

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getMerchantid()) || StringUtils.isBlank(session.getAppid()) || StringUtils.isBlank(session.getUid()) || StringUtils.isBlank(session.getSessionid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        String key = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("merchantid", session.getMerchantid());
        params.put("appid", session.getAppid());
        params.put("uid", session.getUid());
        params.put("sessionid", session.getSessionid());
        params.put("act", "4");
        params.put("sign", Sign.encode(session.getMerchantid(), session.getAppid(), session.getUid(), session.getSessionid(), "4", key));

        try {
            String retStr = HttpUtils.post(url, params);
            if (StringUtils.isBlank(retStr)) {
                result.put("code", "1");
                return JsonMapper.toJson(result);
            }
            Map map = JsonMapper.toObject(retStr, Map.class);
            result.put("code", (String) map.get("code"));
            return JsonMapper.toJson(result);

        } catch (Exception e) {
            logger.error("verify youle session error", e);
            return "";
        }
    }

    @Override
    public String verifyYoule(YouleCallback callback) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (StringUtils.isEmpty(callback.getMerchantId()) || StringUtils.isEmpty(callback.getAppId()) || StringUtils.isEmpty(callback.getUid())
                || StringUtils.isEmpty(callback.getTradeNo()) || StringUtils.isEmpty(callback.getChannelCode()) || StringUtils.isEmpty(callback.getAmount())
                || StringUtils.isEmpty(callback.getCreateTime()) || StringUtils.isEmpty(callback.getSign())) {
            map.put("statusCode", 2);
            map.put("errorMsg", "参数无效");
            map.put("tradeNo", callback.getTradeNo());
            return JsonMapper.toJson(map);
        }

        ChannelStatsLogger.info(ChannelStatsLogger.YOULE, callback.toString());

        try {
            Order order = orderService.getOrderByOrderId(callback.getNote());
            if (order == null) {
                map.put("statusCode", 2);
                map.put("errorMsg", "orderId无效");
                map.put("tradeNo", callback.getTradeNo());
                return JsonMapper.toJson(map);
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                map.put("statusCode", 2);
                map.put("errorMsg", "orderId无效");
                map.put("tradeNo", callback.getTradeNo());
                return JsonMapper.toJson(map);
            }

            String validSign = Sign.encode(callback.getMerchantId(), callback.getAppId(), callback.getUid(),
                    callback.getTradeNo(), callback.getChannelCode(), callback.getAmount(),
                    callback.getCreateTime(), channelGame.getConfigParamsList().get(0));

            if (StringUtils.equals(validSign, callback.getSign())) {
                if (Double.valueOf(callback.getAmount()) * 100 >= order.getAmount()) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    ChannelStatsLogger.error(ChannelStatsLogger.YOULE, order.getOrderId(), "order amount error");
                }
                map.put("statusCode", 0);
                map.put("errorMsg", "接收成功");
                map.put("tradeNo", callback.getTradeNo());
                return JsonMapper.toJson(map);
            } else {
                map.put("statusCode", 3);
                map.put("errorMsg", "签名无效");
                map.put("tradeNo", callback.getTradeNo());
                return JsonMapper.toJson(map);
            }
        } catch (Exception e) {
            map.put("statusCode", 4);
            map.put("errorMsg", "接收数据异常");
            map.put("tradeNo", callback.getTradeNo());
            ChannelStatsLogger.error(ChannelStatsLogger.YOULE, callback.getNote(), "verifyYoule error: " + e);
            return JsonMapper.toJson(map);
        }
    }

    @Override
    public String verifyQiutuSession(QiutuSession session) {
        Map<String, String> result = new HashMap<String, String>();

        logger.debug("verifyQiutuSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyQiutuSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyQiutu(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.QIUTU, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify qiutu valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.QIUTU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify qiutu valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyYuewanSession(YuewanSession session) {

        Map<String, String> result = new HashMap<String, String>();

        logger.debug("verifyYuewanSession params : " + session.toString());

        if (StringUtils.isBlank(session.getYgAppId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyYuewanSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyYuewan(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.YUEWAN, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify yuewan valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.YUEWAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify yuewan valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyWsx(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            logger.info(attach);
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            logger.info("--------");
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.Wsx, params.toString() + " sign: " + sign);

        String validSign = null;
        try {
            validSign = Sign.signByMD5UnsortURLEncode(params, "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify wsx valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.Wsx, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify yuewan valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyIveryoneSession(IveryoneSession session) {

        Map<String, String> result = new HashMap<String, String>();

        logger.debug("verifyIveryoneSession params : " + session.toString());

        if (StringUtils.isBlank(session.getYgAppId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyIveryoneSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyIveryone(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.IVERYONE, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify iveryone valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.IVERYONE, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify iveryone valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }


    @Override
    public String verifyDyhdSession(DyhdSession session) {

        Map<String, String> result = new HashMap<String, String>();

        logger.debug("verifyDyhdSession params : " + session.toString());

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyDyhdSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyDyhd(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.DYHD, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify dyhd valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.DYHD, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify dyhd valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verify7723Session(SsttSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verify7723Session error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verify7723(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.SSTT, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify 7723 valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.SSTT, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify 7723 valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyQiqileSession(QiqileSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getToken()) || StringUtils.isBlank(session.getUgid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String url = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("token", session.getToken());
            params.put("ugid", session.getUgid());

            return HttpUtils.post(url, params);
        } catch (Exception e) {
            logger.error("verifyQiqileSession error", e);
            return "";
        }
    }

    @Override
    public String verifyQiqile(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.QIQILE, HttpUtils.getRequestParams(request).toString());

        String orderId = request.getParameter("orderId");
        String gameOrderId = request.getParameter("gameOrderId");
        String productName = request.getParameter("productName");
        String money = request.getParameter("money");
        String ext = request.getParameter("ext");
        String ugid = request.getParameter("ugid");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(ext);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appKey = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("gameOrderId", gameOrderId);
        params.put("productName", productName);
        params.put("money", money);
        params.put("ext", ext);
        params.put("ugid", ugid);

        String validSign = Sign.signByMD5(params, "#" + appKey);
        if (StringUtils.equalsIgnoreCase(sign, validSign)) {
            logger.debug("verify qiqile valid sign success");
            if (order.getAmount() > Integer.valueOf(money)) {
                ChannelStatsLogger.info(ChannelStatsLogger.QIQILE, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "ok";
        } else {
            logger.debug("verify qiqile valid sign fail: " + validSign);
            return "errorSign";
        }
    }

    @Override
    public String verifyMogeSession(MogeSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyMogeSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyMoge(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.MOGE, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify moge valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.MOGE, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify moge valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String miguLoginNotify(HttpServletRequest request) {
        logger.debug(HttpUtils.getRequestParams(request).toString());
        return "0";
    }

    @Override
    public String verifyMigu(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.MIGU, HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String hRet = request.getParameter("hRet");
        String status = request.getParameter("status");
        String cpparam = request.getParameter("cpparam");

        Order order = basicRepository.getOrderByOrderId(cpparam);
        if (order == null) {
            result.put("hRet", "1");
            result.put("message", "order can not find");
            return JsonMapper.toJson(result);
        }

        if (StringUtils.equals(hRet, "0")) {
            orderService.paySuccess(order.getOrderId());
        } else {
            orderService.payFail(order.getOrderId(), "migu notify order status: " + status);
        }
        result.put("hRet", "0");
        result.put("message", "success");
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyTuuSession(TuuSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyTuuSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyTuu(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.TUU, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify tuu valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.TUU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify tuu valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyMoyoyo(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.MOYOYO, HttpUtils.getRequestParams(request).toString());

        String custominfo = request.getParameter("custominfo");
        String status = request.getParameter("status");
        String amount = request.getParameter("amount");
        String errdesc = request.getParameter("errdesc");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(custominfo);
        if (order == null) {
            return "0";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "0";
        }
        String notifyKey = channelGame.getConfigParamsList().get(0);

        String signParams = new StringBuilder()
                .append(request.getParameter("serverid"))
                .append("|").append(custominfo)
                .append("|").append(request.getParameter("openid"))
                .append("|").append(request.getParameter("ordernum"))
                .append("|").append(status)
                .append("|").append(request.getParameter("paytype"))
                .append("|").append(amount)
                .append("|").append(errdesc)
                .append("|").append(request.getParameter("paytime"))
                .append("|").append(notifyKey).toString();

        if (StringUtils.equalsIgnoreCase(sign, MD5.encode(signParams))) {
            logger.debug("verify moyoyo valid sign success");

            if (StringUtils.equals("1", status)) {
                if (order.getAmount() > Integer.valueOf(amount)) {
                    ChannelStatsLogger.info(ChannelStatsLogger.MOYOYO, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                } else {
                    orderService.paySuccess(order.getOrderId());
                }
            } else {
                orderService.payFail(order.getOrderId(), errdesc);
            }
            return "1";
        } else {
            logger.debug("verify moyoyo valid sign fail");
            return "0";
        }
    }

    @Override
    public String verifyDamai(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.DAMAI, HttpUtils.getRequestParams(request).toString());

        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String appid = request.getParameter("appid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String productname = request.getParameter("productname");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("appid", appid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        try {
            params.put("productname", productname == null ? "" : URLEncoder.encode(productname, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("verifyDamai exception", e);
            return "error";
        }
        params.put("appkey", appkey);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify damai valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.DAMAI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify damai valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyShuowanSession(ShuowanSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyShuowanSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyShuowan(HttpServletRequest request) {
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        ChannelStatsLogger.info(ChannelStatsLogger.SHUOWAN, params.toString() + " sign: " + sign);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify shuowan valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.SHUOWAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify shuowan valid sign failed: " + params.toString() + " sign: " + sign);
            return "errorSign";
        }
    }

    @Override
    public String verifyFirstappSession(FirstappSession session) {
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getAppid())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }
        String url = channelGame.getConfigParamsList().get(0);
        String appkey = channelGame.getConfigParamsList().get(1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", System.currentTimeMillis());
        params.put("appid", session.getAppid());
        params.put("username", session.getUsername());
        params.put("sign", MD5.encode(session.getUsername() + appkey));

        try {
            return HttpUtils.doPostToJson(url, JsonMapper.toJson(params), 5000);
        } catch (Exception e) {
            logger.error("verifyFirstappSession error", e);
            return "";
        }
    }

    @Override
    public String verifyFirstapp(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.FIRSTAPP, HttpUtils.getRequestParams(request).toString());
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String appid = request.getParameter("appid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("appid", appid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify firstapp valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.FIRSTAPP, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify firstapp valid sign failed");
            return "errorSign";
        }
    }

    @Override
    public String qbaoLogin(HttpServletRequest request) {
        logger.debug(HttpUtils.getRequestParams(request).toString());
        return "success";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String qbaoPaySign(HttpServletRequest request) {
        logger.debug("qbaoPaySign params:" + HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String zdappId = request.getParameter("zdappId");
        String platformId = request.getParameter("platformId");
        String appCode = request.getParameter("appCode");
        String orderNo = request.getParameter("orderNo");
        String money = request.getParameter("money");
        String payNotifyUrl = request.getParameter("payNotifyUrl");

        if (StringUtils.isBlank(zdappId) || StringUtils.isBlank(platformId) || StringUtils.isBlank(appCode)
                || StringUtils.isBlank(orderNo) || StringUtils.isBlank(money) || StringUtils.isBlank(payNotifyUrl)) {
            result.put("code", "1");
            result.put("msg", "params empty");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(platformId), Long.valueOf(zdappId));
        if (channelGame == null) {
            result.put("code", "1");
            result.put("msg", "params error");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }
        String pointInfo = channelGame.getConfigParamsList("\\|").get(2);
        Map<String, String> pointMap = JsonMapper.toObject(pointInfo, Map.class);
        String payCode = pointMap.get(money);

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("appCode=");
        sBuilder.append(appCode);
        sBuilder.append("&orderNo=");
        sBuilder.append(orderNo);
        sBuilder.append("&payCode=");
        sBuilder.append(payCode);
        sBuilder.append("&payNotifyUrl=");
        try {
            sBuilder.append(URLEncoder.encode(payNotifyUrl, "utf-8"));
            result.put("code", "0");
            result.put("msg", channelUtilsService.qbaoPaySign(sBuilder.toString(), channelGame.getConfigParamsList("\\|").get(0)));
            result.put("payCode", payCode);
            return JsonMapper.toJson(result);

        } catch (Exception e) {
            logger.error("qbaoPaySign error", e);
            result.put("code", "1");
            result.put("msg", "server error");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String rechargeCallBackQbao(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.QBAO, HttpUtils.getRequestParams(request).toString());

        Map<String, Object> result = new HashMap<String, Object>();
        String responseCode = request.getParameter("responseCode");
        String errorCode = request.getParameter("errorCode");
        String errorMsg = request.getParameter("errorMsg");
        String data = request.getParameter("data");
        String signCode = request.getParameter("signCode");
        if (StringUtils.isBlank(responseCode) || StringUtils.isBlank(data) || StringUtils.isBlank(signCode)) {
            result.put("isSuccess", false);
            return JsonMapper.toJson(result);
        }
        Map<String, Object> dataMap = JsonMapper.toObject(data, Map.class);
        String sdkflowId = dataMap.get("sdkflowId").toString();
        String bizCode = dataMap.get("bizCode").toString();

        logger.debug("sdkflowId: {}, bizCode: {}", sdkflowId, bizCode);

        Order order = basicRepository.getOrderByOrderId(bizCode);
        if (order == null) {
            result.put("isSuccess", false);
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("isSuccess", false);
            return JsonMapper.toJson(result);
        }

        String format = "responseCode=%s,errorCode=%s,sdkflowId=%s,bizCode=%s";
        String content = String.format(format, responseCode, StringUtils.isBlank(errorCode) ? "null" : errorCode, sdkflowId, bizCode);
        logger.debug("content: {}, signCode: {}", content, signCode);
        try {
            boolean valid = channelUtilsService.verifyQbao(content, signCode, channelGame.getConfigParamsList("\\|").get(1));
            if (valid) {
                logger.debug("verify qbao valid sign success");
                if (StringUtils.equals("1000", responseCode)) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), errorMsg);
                }

                result.put("isSuccess", true);
                return JsonMapper.toJson(result);
            } else {
                logger.debug("verify qbao valid sign failed");
            }
        } catch (Exception e) {
            logger.error("verify qbao pay error: ", e);
        }
        result.put("isSuccess", false);
        return JsonMapper.toJson(result);
    }


    @Override
    public String bingquBaowanLogin(HttpServletRequest request) {
        return "success";
    }

    @Override
    public String bingquBaowanPaySign(HttpServletRequest request) {
        logger.debug("bingquBaowanPaySign params:" + HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String zdappId = request.getParameter("zdappId");
        String platformId = request.getParameter("platformId");
        String appCode = request.getParameter("appCode");
        String orderNo = request.getParameter("orderNo");
        String money = request.getParameter("money");
        String payNotifyUrl = request.getParameter("payNotifyUrl");

        if (StringUtils.isBlank(zdappId) || StringUtils.isBlank(platformId) || StringUtils.isBlank(appCode)
                || StringUtils.isBlank(orderNo) || StringUtils.isBlank(money) || StringUtils.isBlank(payNotifyUrl)) {
            result.put("code", "1");
            result.put("msg", "params empty");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(platformId), Long.valueOf(zdappId));
        if (channelGame == null) {
            result.put("code", "1");
            result.put("msg", "params error");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }
        String pointInfo = channelGame.getConfigParamsList("\\|").get(2);
        Map<String, String> pointMap = JsonMapper.toObject(pointInfo, Map.class);
        String payCode = pointMap.get(money);

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("appCode=");
        sBuilder.append(appCode);
        sBuilder.append("&orderNo=");
        sBuilder.append(orderNo);
        sBuilder.append("&payCode=");
        sBuilder.append(payCode);
        sBuilder.append("&payNotifyUrl=");
        try {
            sBuilder.append(URLEncoder.encode(payNotifyUrl, "utf-8"));
            result.put("code", "0");
            result.put("msg", channelUtilsService.qbaoPaySign(sBuilder.toString(), channelGame.getConfigParamsList("\\|").get(0)));
            result.put("payCode", payCode);
            return JsonMapper.toJson(result);

        } catch (Exception e) {
            logger.error("qbaoPaySign error", e);
            result.put("code", "1");
            result.put("msg", "server error");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String rechargeCallBackbingquBaowan(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.BINGQUBAOWAN, HttpUtils.getRequestParams(request).toString());

        Map<String, Object> result = new HashMap<String, Object>();
        String responseCode = request.getParameter("responseCode");
        String errorCode = request.getParameter("errorCode");
        String errorMsg = request.getParameter("errorMsg");
        String data = request.getParameter("data");
        String signCode = request.getParameter("signCode");
        if (StringUtils.isBlank(responseCode) || StringUtils.isBlank(data) || StringUtils.isBlank(signCode)) {
            result.put("isSuccess", false);
            return JsonMapper.toJson(result);
        }
        Map<String, Object> dataMap = JsonMapper.toObject(data, Map.class);
        String sdkflowId = dataMap.get("sdkflowId").toString();
        String orderNo = dataMap.get("orderNo").toString();

        logger.debug("sdkflowId: {}, orderNo: {}", sdkflowId, orderNo);

        Order order = basicRepository.getOrderByOrderId(orderNo);
        if (order == null) {
            result.put("isSuccess", false);
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("isSuccess", false);
            return JsonMapper.toJson(result);
        }

        String format = "responseCode=%s,errorCode=%s,sdkflowId=%s,orderNo=%s";
        String content = String.format(format, responseCode, StringUtils.isBlank(errorCode) ? "\"\"" : errorCode, sdkflowId, orderNo);
        logger.debug("content: {}, signCode: {}", content, signCode);
        try {
            boolean valid = channelUtilsService.verifyQbao(content, signCode, channelGame.getConfigParamsList("\\|").get(1));
            if (valid) {
                logger.debug("verify bingquBaowan valid sign success");
                if (StringUtils.equals("1000", responseCode)) {
                    orderService.paySuccess(order.getOrderId());
                } else {
                    orderService.payFail(order.getOrderId(), errorMsg);
                }

                result.put("isSuccess", true);
                return JsonMapper.toJson(result);
            } else {
                logger.debug("verify bingquBaowan valid sign failed");
            }
        } catch (Exception e) {
            logger.error("verify bingquBaowan pay error: ", e);
        }
        result.put("isSuccess", false);
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyTtSession(TtSession ttSession) {
        ChannelStatsLogger.info(ChannelStatsLogger.TT, ttSession.toString());

        Map<String, Object> head = new HashMap<String, Object>();
        Map<String, Object> mapResult = new HashMap<String, Object>();
        try {
            String sid = ttSession.getSid();
            ChannelGameEntity channelGame = basicRepository.
                    getByChannelAndGameId(Integer.valueOf(ttSession.getPlatformId()), Long.valueOf(ttSession.getYgAppId()));
            if (channelGame == null) {
                head.put("result", "1");
                head.put("message", "not find channelGame");
                mapResult.put("head", head);
                mapResult.put("body", "错误");
                return JsonUtil.toJson(mapResult);
            }

            String gameId = channelGame.getConfigParamsList().get(0);
            String appkey = channelGame.getConfigParamsList().get(1);
            String url = channelGame.getConfigParamsList().get(2);

            /** 组合报文*/
            Map<String, Object> urldata = new LinkedHashMap<String, Object>();
            urldata.put("gameId", Integer.parseInt(gameId));
            urldata.put("uid", ttSession.getUid());
            String jsonBody = JsonUtil.toJson(urldata);

            /** 使用密钥进行签名*/
            String sign = Sign.signByMD5AndBASE64(jsonBody, appkey);

            ChannelStatsLogger.info(ChannelStatsLogger.TT, "jsonBody：" + jsonBody + " sign: " + sign);

            /** 组合headers*/
            Map<String, Object> header = new LinkedHashMap<String, Object>();
            header.put("sign", sign);
            header.put("sid", sid);
            /** doPost*/
            String result = HttpUtils.doPost(url, jsonBody, header);

            ChannelStatsLogger.info(ChannelStatsLogger.TT, "verifyTtSession Result：" + result);
            return result;
        } catch (Exception e) {
            head.put("result", "1");
            head.put("message", "error");
            mapResult.put("head", head);
            mapResult.put("body", "系统出现异常");
            return JsonUtil.toJson(mapResult);
        }
    }

    @Override
    public String rechargeTt(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.TT, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> head = new HashMap<String, Object>();
        Map<String, Object> mapResult = new HashMap<String, Object>();

        try {
            /** 获取订单号*/
            String cpTradeNo = request.getParameter("cpTradeNo");
            /** 查询订单*/
            Order order = basicRepository.getOrderByOrderId(cpTradeNo);
            logger.debug("order= {}", order);
            if (order == null) {
                return "error not find order";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "error not find channelGame";
            }

            Integer gameId = Integer.parseInt(channelGame.getConfigParamsList().get(0));
            String appkey = channelGame.getConfigParamsList().get(3);
            String url = channelGame.getConfigParamsList().get(4);

            BigDecimal bd = new BigDecimal(request.getParameter("cpFee"));
            BigDecimal cpFee = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("body", request.getParameter("body"));
            map.put("cpFee", cpFee);
            map.put("cpTradeNo", cpTradeNo);
            map.put("gameId", gameId);
            map.put("roleId", request.getParameter("roleId"));
            map.put("roleName", request.getParameter("roleName"));
            map.put("serverId", request.getParameter("serverId"));
            map.put("serverName", request.getParameter("serverName"));
            map.put("subject", request.getParameter("subject"));
            map.put("userId", Long.parseLong(request.getParameter("userId")));
            String jsonBody = JsonUtil.toJson(map);

            /** 使用密钥进行签名*/
            String sign = Sign.signByMD5AndBASE64(URLDecoder.decode(jsonBody, "utf-8"), appkey);

            ChannelStatsLogger.info(ChannelStatsLogger.TT, "jsonBody：" + jsonBody + " sign: " + sign);

            /** 组合headers*/
            Map<String, Object> header = new HashMap<String, Object>();
            header.put("sign", sign);
            /** 获取返回结果*/
            String result = HttpUtils.doPost(url, jsonBody, header);

            ChannelStatsLogger.info(ChannelStatsLogger.TT, "rechargeTt Result：" + result);
            return result;
        } catch (Exception e) {
            head.put("message", "系统出现异常");
            head.put("result", "1");
            mapResult.put("head", head);
            return JsonUtil.toJson(mapResult);
        }
    }

    @Override
    public String rechargeCallBackQipa(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.QIPA, HttpUtils.getRequestParams(request).toString());

        Map<String, String> result = new HashMap<String, String>();

        String order_no = request.getParameter("order_no");
        String game_order_no = request.getParameter("game_order_no");
        String uid = request.getParameter("uid");
        String pay_money = request.getParameter("pay_money");
        String pid = request.getParameter("pid");
        String service_id = request.getParameter("service_id");
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        String paystatus = request.getParameter("paystatus");

        Order order = basicRepository.getOrderByOrderId(game_order_no);
        if (order == null) {
            result.put("code", "-3");
            result.put("msg", "非法订单");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            result.put("code", "-3");
            result.put("msg", "非法订单");
            return JsonMapper.toJson(result);
        }

        String key = channelGame.getConfigParamsList().get(0);

        String validSign = MD5.encode(order_no + game_order_no + uid + pay_money + pid + service_id + time + paystatus + key);

        logger.debug("sign: " + sign);
        logger.debug("validSign: " + validSign);
        logger.debug("content: " + order_no + game_order_no + uid + pay_money + pid + service_id + time + paystatus + key);

        if (StringUtils.equalsIgnoreCase(sign, validSign)) {
            logger.debug("verify qipa valid sign success");
            if (StringUtils.equals(paystatus, "1")) {
                if (order.getAmount() > Double.valueOf(pay_money) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.QIPA, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    result.put("code", "-2");
                    result.put("msg", "金额有误");
                    return JsonMapper.toJson(result);
                } else {
                    orderService.paySuccess(order.getOrderId());
                    result.put("code", "1");
                    result.put("msg", "充值成功");
                    return JsonMapper.toJson(result);
                }
            } else {
                orderService.payFail(order.getOrderId(), paystatus);
                result.put("code", "0");
                result.put("msg", "充值失败");
                return JsonMapper.toJson(result);
            }

        } else {
            logger.debug("verify qipa valid sign failed");
            result.put("code", "-1");
            result.put("msg", "sign错误");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyAipuSession(AipuSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId())
                || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(session.getSign()) || StringUtils.isBlank(session.getLogintime())) {
            return "";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
            return "";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        try {
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("username", session.getUsername());
            params.put("appkey", appkey);
            params.put("logintime", session.getLogintime());

            String validSign = Sign.signByMD5Unsort(params, "");

            result.put("code", StringUtils.equals(session.getSign(), validSign) ? "0" : "1");
            result.put("msg", StringUtils.equals(session.getSign(), validSign) ? "success" : "签名校验失败");
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyAipuSession error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyAipu(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.AIPU, HttpUtils.getRequestParams(request).toString());

        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderid", orderid);
        params.put("username", username);
        params.put("gameid", gameid);
        params.put("roleid", roleid);
        params.put("serverid", serverid);
        params.put("paytype", paytype);
        params.put("amount", amount);
        params.put("paytime", paytime);
        params.put("attach", attach);
        params.put("appkey", appkey);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify aipu valid sign success");

            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.AIPU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }

            return "success";
        } else {
            logger.debug("verify aipu valid sign failed validSign{}: ", validSign);
            return "errorSign";
        }
    }

    @Override
    public String verifyShunwangSession(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(request.getParameter("platformId")), Long.valueOf(request.getParameter("zdappId")));
            if (channelGame == null) {
                return "";
            }
            //memberId=213673665&zdappId=151110191986&siteId=10170&ticket=32c193a7-99fa-40ce-b1d7-c33b267b0328&platformId=1092&

            String md5KeyString = channelGame.getConfigParamsList().get(1);
            String url = channelGame.getConfigParamsList().get(2);
            //当前时间,精确到秒，格式如：20090202080403
            String time = DateUtils.format(new Date(), "yyyyMMddHHmmss");
            //sign = upper(md5(upper(urlencode(siteId|time|memberId|ticket|md5Key))))
            String sign = (MD5.encode((URLEncoder.encode(request.getParameter("siteId") + "|" + time + "|" + request.getParameter("memberId") + "|" + request.getParameter("ticket") + "|" + md5KeyString, "UTF-8")).toUpperCase())).toUpperCase();
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("siteId", request.getParameter("siteId"));
            params.put("time", time);
            params.put("gameId", request.getParameter("gameId"));
            params.put("memberId", request.getParameter("memberId"));
            params.put("flagGuid", "1");
            params.put("ticket", request.getParameter("ticket"));
            params.put("sign", sign);

            String string = HttpUtils.post(url, params);
            string = string.replace("\r\n", "");
            result.put("msg", getKeyShunwang(string, "msg=\"", "\""));
            result.put("msgId", getKeyShunwang(string, "msgId=\"", "\""));
            result.put("returnSign", getKeyShunwang(string, "returnSign=\"", "\""));
            result.put("memberName", getKeyShunwang(string, "<memberName>", "</memberName>"));
            result.put("accessTokenTimeOutInterval", getKeyShunwang(string, "<accessTokenTimeOutInterval>", "</accessTokenTimeOutInterval>"));
            result.put("accessToken", getKeyShunwang(string, "<accessToken>", "</accessToken>"));
            result.put("refreshToken", getKeyShunwang(string, "<refreshToken>", "</refreshToken>"));
            result.put("guid", getKeyShunwang(string, "<guid>", "</guid>"));
            result.put("loginStatus", getKeyShunwang(string, "<loginStatus>", "</loginStatus>"));
//			Map<String, String> re = new HashMap<String,String>();
//			re.put("1000", "失败,系统异常.");
//			re.put("1001", "参数不能为空");
//			re.put("1002", "签名错误");
//			re.put("1003", "请求超时");
//			re.put("1004", "站点ID不存在");
//			re.put("1005", "IP地址不合法");
//			result.put("code", re.get(result.get("msgId")));
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verify Shun wang session error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    private static String getKeyShunwang(String content, String k1, String k2) {
        int s1 = content.indexOf(k1) + k1.length();
        if (s1 > 0) {
            int s2 = content.indexOf(k2, s1);
            if (s2 > 0) {
                return content.substring(s1, s2);
            }
        }
        return "";
    }

    @Override
    public String verifyShunwang(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.SHUNWANG, HttpUtils.getRequestParams(request).toString());

        String orderNo = request.getParameter("orderNo");
        String gameId = request.getParameter("gameId");
        String guid = request.getParameter("guid");
        String money = request.getParameter("money");
        String coins = request.getParameter("coins");
        String coinMes = request.getParameter("coinMes");
        String time = request.getParameter("time");
        String orderId = request.getParameter("orderId");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String signKey = channelGame.getConfigParamsList().get(0);

        //Sign=MD5(1001|5a60cc3718ba43a994caa2507d673dac|s1|接入方的sign_key).toUpperCase
        //String validSign = MD5.encode(orderNo + "|" + gameId + "|" + guid + "|" + money + "|" + coins + "|" + coinMes + "|" + time + "|" + orderId + "|" + signKey).toUpperCase();
        String md5Str = coinMes + "|" + coins + "|" + gameId + "|" + guid + "|" + money + "|" + orderId + "|" + orderNo + "|" + time + "|" + signKey;
        String validSign = MD5.encode(coinMes + "|" + coins + "|" + gameId + "|" + guid + "|" + money + "|" + orderId + "|" + orderNo + "|" + time + "|" + signKey).toUpperCase();
        logger.info("MD5String = " + md5Str);
        logger.info("validSign = " + validSign);
        logger.info("sign = " + sign);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify aipu valid sign success");

            if (order.getAmount() > Integer.valueOf(money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.SHUNWANG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "order amount errot";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "0";
            }
        } else {
            logger.debug("verify aipu valid sign failed validSign{}: ", validSign);
            return "errorSign";
        }
    }


    @Override
    public String verifyZhuoyi(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.ZHOUYI, HttpUtils.getRequestParams(request).toString());
        String Recharge_Id = request.getParameter("Recharge_Id");
        String App_Id = request.getParameter("App_Id");
        String Uin = request.getParameter("Uin");
        String Urecharge_Id = request.getParameter("Urecharge_Id");
        String Extra = request.getParameter("Extra");
        String Recharge_Money = request.getParameter("Recharge_Money");
        String Recharge_Gold_Count = request.getParameter("Recharge_Gold_Count");
        String Pay_Status = request.getParameter("Pay_Status");
        String Create_Time = request.getParameter("Create_Time");
        String sign = request.getParameter("Sign");

        Order order = basicRepository.getOrderByOrderId(Urecharge_Id);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);
        //encrypt($queryArray,app_server_key,’MD5)，生成32个0-f的字符串后，将 &sign=32个签名 添加到数据段最后。
        //Recharge_Id=xxx&App_Id=xxx&Uin=xxx&Urecharge_Id=xxx&Extra=xxx&Recharge_Money=xxx&Recharge_Gold_Count=xxx&Pay_Status=xxx&Create_Time=xxx&Sign=xxxxxxxxxxxx

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("Recharge_Id", Recharge_Id);
        params.put("App_Id", App_Id);
        params.put("Uin", Uin);
        params.put("Urecharge_Id", Urecharge_Id);
        params.put("Extra", Extra);
        params.put("Recharge_Money", Recharge_Money);
        params.put("Recharge_Gold_Count", Recharge_Gold_Count);
        params.put("Pay_Status", Pay_Status);
        params.put("Create_Time", Create_Time);

        String validSign = Sign.signByMD5(params, appkey);
        if (StringUtils.equals(validSign, sign)) {
            ChannelStatsLogger.info(ChannelStatsLogger.ZHOUYI, "verify zhuoyi success:");
            logger.debug("verify zhuoyi valid sign success");

            if (order.getAmount() > Float.valueOf(Recharge_Money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.ZHOUYI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "success";
        } else {
            ChannelStatsLogger.info(ChannelStatsLogger.ZHOUYI, "verify zuoyi valid sign failed validSign:" + appkey);
            return "errorSign";
        }
    }

    @Override
    public String verifyYunxiaotanSession(YunxiaotanSession session) {

        Map<String, String> result = new HashMap<String, String>();

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }
            //memberId=213673665&zdappId=151110191986&siteId=10170&ticket=32c193a7-99fa-40ce-b1d7-c33b267b0328&platformId=1092&

            String md5KeyString = channelGame.getConfigParamsList().get(0);
            String url = channelGame.getConfigParamsList().get(1);
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("ac", "check");
            params.put("appid", session.getAppId());
            params.put("sdkversion", "3.0");
            params.put("time", System.currentTimeMillis() / 1000);
            params.put("sessionid", session.getSid());
            List<String> keys = new ArrayList<String>(params.keySet());
            Collections.sort(keys);
            Map<String, Object> sigleMap = new LinkedHashMap<String, Object>();
            for (String key : keys) {
                sigleMap.put(key, params.get(key));
            }
            String sigle = Sign.signByMD5(sigleMap, md5KeyString);
            System.out.println("=====" + sigle + "=====");
            params.put("sign", sigle);
            params.put("sessionid", URLDecoder.decode(session.getSid(), "utf-8"));
            String string = HttpUtils.post(url, params);
            if (TextUtils.isEmpty(string)) {
                result.put("code", "3");
                result.put("msg", "渠道服务器返回空");
                return JsonMapper.toJson(result);
            }
            return string;
        } catch (Exception e) {
            logger.error("verify shun wang Session error", e);
            result.put("code", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }

    }

    @Override
    public String verifyYunxiaotan(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.YUNXIAOTAN, HttpUtils.getRequestParams(request).toString());
        String cporderid = request.getParameter("cporderid");
        String orderid = request.getParameter("orderid");
        String appid = request.getParameter("appid");
        String time = request.getParameter("time");
        String extinfo = request.getParameter("extinfo");
        String amount = request.getParameter("amount");
        String serverid = request.getParameter("serverid");
        String charid = request.getParameter("charid");
        String gold = request.getParameter("gold");
        String uid = request.getParameter("uid");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(cporderid);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(2);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("cporderid", cporderid);
        params.put("appid", appid);
        params.put("gold", gold);
        try {
            params.put("extinfo", URLEncoder.encode(extinfo, "utf-8"));
            params.put("charid", URLEncoder.encode(charid, "utf-8"));
            params.put("serverid", URLEncoder.encode(serverid, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "FAIL";
        }
        params.put("amount", amount);
        params.put("orderid", orderid);
        params.put("time", time);
        params.put("uid", uid);

        String validSign = Sign.signByMD5(params, appkey);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify aipu valid sign success");

            if (order.getAmount() > Float.parseFloat(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.YUNXIAOTAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "SUCCESS";
        } else {
            logger.debug("verify aipu valid sign failed validSign{}: ", validSign);
            return "FAIL";
        }
    }

    @Override
    public String verifyGuangzhoupeidui(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.GUANGZHOUPEIDUI, HttpUtils.getRequestParams(request).toString());
        String c_id = request.getParameter("c_id");
        String cp_order_no = request.getParameter("cp_order_no");
        String g_id = request.getParameter("g_id");
        String u_id = request.getParameter("u_id");
        String goods_id = request.getParameter("goods_id");
        String goods_name = request.getParameter("goods_name");
        String goods_body = request.getParameter("goods_body");
        String goods_num = request.getParameter("goods_num");
        String goods_price = request.getParameter("goods_price");
        String goods_amount = request.getParameter("goods_amount");
        String a_id = request.getParameter("a_id");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(cp_order_no);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("g_id", g_id);
        params.put("u_id", u_id);
        params.put("c_id", c_id);
        params.put("goods_name", goods_name);
        params.put("goods_body", goods_body);
        params.put("goods_id", goods_id);
        params.put("goods_body", goods_body);
        params.put("goods_num", goods_num);
        params.put("goods_price", goods_price);
        params.put("goods_amount", goods_amount);
        params.put("cp_order_no", cp_order_no);
        params.put("a_id", a_id);
        String validSign = Sign.signByMD5KeyPre(params, appkey);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify gzpd valid sign success");

            if (order.getAmount() > Float.parseFloat(goods_amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.GUANGZHOUPEIDUI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "success";
        } else {
            logger.debug("verify gzpd valid sign failed validSign{}: ", validSign);
            return "FAIL";
        }
    }

    @Override
    public String verifyDianyoo(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.DIANYOO, HttpUtils.getRequestParams(request).toString());
        String OrderNo = request.getParameter("OrderNo");
        String Money = request.getParameter("Money");
        String ResultCode = request.getParameter("ResultCode");
        String ExtensionField = request.getParameter("ExtensionField");
        String TimeStamp = request.getParameter("TimeStamp");
        String SKey = request.getParameter("SKey");

        if (!ResultCode.equals("0")) {
            return "fail:充值失败 ResultCode=" + ResultCode;
        }

        Order order = basicRepository.getOrderByOrderId(OrderNo);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(0);

        StringBuilder sb = new StringBuilder();
        sb.append(OrderNo).append(Money).append(ResultCode).append(TimeStamp).append(appkey);

        String validSign = MD5.encode(sb.toString());

        if (StringUtils.endsWithIgnoreCase(validSign, SKey)) {
            logger.debug("verify dianyoo valid sign success");

            if (order.getAmount() > Float.parseFloat(Money) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.YUNXIAOTAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "success";
        } else {
            logger.debug("verify aipu valid sign failed validSign{}: ", validSign);
            return "fail:签名不对";
        }
    }

    @Override
    public String verifyhongchongSession(ChongchongSession session) {
        Map<String, String> result = new HashMap<String, String>();

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }
            //memberId=213673665&zdappId=151110191986&siteId=10170&ticket=32c193a7-99fa-40ce-b1d7-c33b267b0328&platformId=1092&

            String url = channelGame.getConfigParamsList().get(1);
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("token", session.getToken());
            String string = HttpUtils.post(url, params);
            if (TextUtils.isEmpty(string)) {
                result.put("code", "3");
                result.put("msg", "渠道服务器返回空");
                return JsonMapper.toJson(result);
            } else if (string.equals("success")) {
                result.put("code", "0");
                result.put("msg", string);
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "4");
                result.put("msg", string);
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("verifyAipuSession error", e);
            result.put("code", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyChongchong(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.CHONGCHONG, HttpUtils.getRequestParams(request).toString());
        String transactionNo = request.getParameter("transactionNo");
        String partnerTransactionNo = request.getParameter("partnerTransactionNo");
        String statusCode = request.getParameter("statusCode");
        String productId = request.getParameter("productId");
        String orderPrice = request.getParameter("orderPrice");
        String packageId = request.getParameter("packageId");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(partnerTransactionNo);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }

        String appkey = channelGame.getConfigParamsList().get(2);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("transactionNo", transactionNo);
        params.put("partnerTransactionNo", partnerTransactionNo);
        params.put("statusCode", statusCode);
        params.put("productId", productId);
        params.put("orderPrice", orderPrice);
        params.put("packageId", packageId);
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        String validSign = MD5.encode(sb.toString() + appkey);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify chongchong valid sign success");

            if (order.getAmount() > Float.parseFloat(orderPrice) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.CHONGCHONG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "SUCCESS";
        } else {
            logger.debug("verify chongchong valid sign failed validSign{}: ", validSign);
            return "FAIL";
        }
    }

    @Override
    public String verifyQishi(HttpServletRequest request) {

        ChannelStatsLogger.info(ChannelStatsLogger.QISHI, HttpUtils.getRequestParams(request).toString());
        String paystatus = request.getParameter("paystatus");
        String paymoney = request.getParameter("paymoney");//单位分
        String payorder = request.getParameter("payorder");
        String paygameorder = request.getParameter("paygameorder");
        String paygameid = request.getParameter("paygameid");
        String gamestring = request.getParameter("gamestring");
        String sign = request.getParameter("appkey");

        Order order = basicRepository.getOrderByOrderId(paygameorder);
        if (order == null) {
            return "error";
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }
        String appkey = channelGame.getConfigParamsList().get(0);
        long md5str = Long.parseLong(payorder) - Integer.parseInt(paymoney);
        String validSign = MD5.encode(MD5.encode(md5str + "") + appkey);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify qishi valid sign success");

            if (order.getAmount() > Integer.parseInt(paymoney)) {
                ChannelStatsLogger.info(ChannelStatsLogger.QISHI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
            } else {
                orderService.paySuccess(order.getOrderId());
            }
            return "1";
        } else {
            logger.debug("verify qishi valid sign failed validSign{}: ", validSign);
            return "2";
        }
    }

    @Override
    public String verifyTt(HttpServletRequest request) {
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        Map<String, Object> headMap = new LinkedHashMap<String, Object>();
        try {
            BufferedReader br = request.getReader();
            String inputLine;
            String str = "";
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
            logger.debug("verifyTt str:" + str);
            /** 获取请求有个sign*/
            String sign = request.getHeader("sign");
            ChannelStatsLogger.info(ChannelStatsLogger.TT, URLDecoder.decode(str, "utf-8"));
            ChannelStatsLogger.info(ChannelStatsLogger.TT, sign);
            /** 将报文体内容解码,并获取*/
            JSONObject jsonObject = new JSONObject(URLDecoder.decode(str, "utf-8"));
            String cpOrderId = jsonObject.optString("cpOrderId");
            String payFee = jsonObject.optString("payFee");
            String payResult = jsonObject.optString("payResult");
            Order order = basicRepository.getOrderByOrderId(cpOrderId);
            if (order == null) {
                return "error";
            }

            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "error";
            }
            /** 生成签名*/
            String appkey = channelGame.getConfigParamsList().get(3);
            String validSign = Sign.signByMD5AndBASE64(URLDecoder.decode(str, "utf-8"), appkey);
            logger.debug("sign:" + sign + "validSign:" + validSign);
            if (validSign.trim().equals(sign.trim()) && payResult.equals("1")) {
                logger.debug("verify qishi valid sign success");

                if (order.getAmount() > Float.parseFloat(payFee) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.TT, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    resultMap.put("result", "-2");
                    resultMap.put("message", "回调金额不一致，期望=" + order.getAmount() / 100 + "，实际=" + Float.parseFloat(payFee));
                    headMap.put("head", resultMap);
                } else {
                    orderService.paySuccess(order.getOrderId());
                    resultMap.put("result", "0");
                    resultMap.put("message", "成功");
                    headMap.put("head", resultMap);
                    logger.debug("verifyTt Success");
                }
                return JsonMapper.toJson(headMap);
            } else {
                logger.debug("verify tt valid sign failed validSign{}: ", validSign);
                return JsonMapper.toJson(headMap);
            }
        } catch (IOException e) {
            resultMap.put("result", "1");
            resultMap.put("message", "失败");
            e.printStackTrace();
            headMap.put("head", resultMap);
            return JsonMapper.toJson(headMap);
        } catch (JSONException e) {
            resultMap.put("result", "1");
            resultMap.put("message", "失败");
            e.printStackTrace();
            headMap.put("head", resultMap);
            return JsonMapper.toJson(headMap);
        }
    }

    @Override
    public String verifyYeshen(HttpServletRequest request) {
        String transdata = request.getParameter("transdata");
        return null;
    }

    @Override
    public String verifyLewanSession(LewanSession session) {
        Map<String, String> result = new HashMap<String, String>();

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }
            //memberId=213673665&zdappId=151110191986&siteId=10170&ticket=32c193a7-99fa-40ce-b1d7-c33b267b0328&platformId=1092&

            String url = channelGame.getConfigParamsList().get(2);
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("app_id", session.getApp_id());
            params.put("code", session.getCode());
            params.put("password", session.getPassword());
            params.put("token", session.getToken());
            params.put("channelId", session.getChannelId());
            Map<String, Object> tparams = new LinkedHashMap<String, Object>();
            tparams.put("notifyData", JsonMapper.toJson(params));
            url = url + "?notifyData=" + JsonMapper.toJson(params);
            logger.debug("URL=" + url);
            String string = HttpUtils.post(url, JsonMapper.toJson(params));
            return string;
        } catch (Exception e) {
            logger.error("verifyAipuSession error", e);
            result.put("code", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }


    @Override
    public String verifyLewan(HttpServletRequest request) {
        String data = request.getParameter("data");
        String encryptkey = request.getParameter("encryptkey");
        String appid = request.getParameter("appid");
        ChannelStatsLogger.info(ChannelStatsLogger.LEWAN, HttpUtils.getRequestParams(request).toString());
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(1104, Long.parseLong(appid));

        if (channelGame == null) {
            return "error";
        }
        String gameprivateKey = channelGame.getConfigParamsList().get(0).trim();
        String lewanpublickKey = channelGame.getConfigParamsList().get(1).trim();
        logger.debug("gameprivateKey:" + gameprivateKey);
        logger.debug("lewanpublickKey:" + lewanpublickKey);
        if (channelGame == null) {
            return "error";
        }
        boolean isRight = true;
        try {

            isRight = EncryUtil.checkDecryptAndSign(data, encryptkey, lewanpublickKey, gameprivateKey);
        } catch (Exception e1) {
            logger.debug("sign is not pass--:" + e1.getMessage());
            e1.printStackTrace();
        }
        if (!isRight) {//验证签名没通过
//            out.println("sign is not pass");
            logger.debug("sign is not pass");
            return "error";
        }

        /** 1.使用游戏的的私钥解开aesEncrypt。 */
        String AESKey = "";
        try {
            AESKey = RSA.decrypt(encryptkey, gameprivateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail " + e.getMessage();
        }
        /** 2.用aeskey解开data。取得data明文 */
        String realData = AES.decryptFromBase64(data, AESKey);
        ChannelStatsLogger.info(ChannelStatsLogger.LEWAN, realData);
        TreeMap<String, String> map = JSON.parseObject(realData, new TypeReference<TreeMap<String, String>>() {
        });
        String orderid = map.get("gameOrderId");
        Order order = basicRepository.getOrderByOrderId(orderid);
        String paySuccessMoney = map.get("paySuccessMoney");
        if (order == null) {
            return "error";
        }
        if (Integer.parseInt(map.get("payState")) != 2) {
            return "payfail paystate:" + map.get("payState");
        }
        if (order.getAmount() > Float.parseFloat(paySuccessMoney)) {
            ChannelStatsLogger.info(ChannelStatsLogger.TT, "order amount error");
            orderService.payFail(order.getOrderId(), "order amount error");
            return "fail";
        } else {

            orderService.paySuccess(order.getOrderId());
            return "success";
        }
    }

    @Override
    public String verifyWanke(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.WANKE, HttpUtils.getRequestParams(request).toString());
        String orderid = request.getParameter("orderid");
        String username = request.getParameter("username");
        String gameid = request.getParameter("gameid");
        String roleid = request.getParameter("roleid");
        String serverid = request.getParameter("serverid");
        String paytype = request.getParameter("paytype");
        String amount = request.getParameter("amount");
        String paytime = request.getParameter("paytime");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(attach);
        if (order == null) {
            return "error";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }
        String appKey = channelGame.getConfigParamsList().get(0).trim();
        Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        paramMap.put("orderid", orderid);
        paramMap.put("username", username);
        paramMap.put("gameid", gameid);
        paramMap.put("roleid", roleid);
        paramMap.put("serverid", serverid);
        paramMap.put("paytype", paytype);
        paramMap.put("amount", amount);
        paramMap.put("paytime", paytime);
        paramMap.put("attach", attach);
        paramMap.put("appkey", appKey);
        String validSign = Sign.signByMD5Unsort(paramMap, "");
        if (validSign.equals(sign)) {
            if (order.getAmount() > Integer.parseInt(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.WANKE, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "error";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "success";
            }

        } else {
            return "errorSign";
        }
    }

    @Override
    public String verifyDaomen(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.DAOMEN, HttpUtils.getRequestParams(request).toString());
        String out_trade_no = request.getParameter("out_trade_no");
        String price = request.getParameter("price");
        String pay_status = request.getParameter("pay_status");
        String extend = request.getParameter("extend");
        String signType = request.getParameter("signType");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(extend);
        if (order == null) {
            return "error";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }
        String key = channelGame.getConfigParamsList().get(0);
        String str = out_trade_no + pay_status + extend + key;
        String md5 = MD5.encode(str);
        if (sign.equals(md5)) {
            if (pay_status.equals("1")) {
                if (order.getAmount() > Integer.parseInt(price)) {
                    ChannelStatsLogger.info(ChannelStatsLogger.DAOMEN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "error";
                } else {
                    orderService.paySuccess(order.getOrderId());
                    return "success";
                }
            } else {
                return "pay failed";
            }
        } else {

            return "签名错误";
        }
    }

    @Override
    public String verifyWuxiandongli(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.WXDL, HttpUtils.getRequestParams(request).toString());
        String orderId = request.getParameter("orderId");
        String cpOrderId = request.getParameter("cpOrderId");
        String payPointId = request.getParameter("payPointId");
        String payRealMoney = request.getParameter("payRealMoney");
        String payVirMoney = request.getParameter("payVirMoney");
        String status = request.getParameter("status");
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        if (Integer.parseInt(status) != 1) {
            return "fail status =" + status;
        }
        Order order = basicRepository.getOrderByOrderId(cpOrderId);
        if (order == null) {
            return "fail";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        String appKey = channelGame.getConfigParamsList("\\|").get(0).trim();
        if (channelGame == null) {
            return "fail";
        }
        StringBuilder md5srcsb = new StringBuilder(orderId);
        md5srcsb.append(payPointId).append(payRealMoney).append(time).append(cpOrderId).append(appKey);
        String validString = MD5.encode(md5srcsb.toString());
        if (validString.equals(sign)) {
            if (order.getAmount() > Integer.parseInt(payRealMoney)) {
                ChannelStatsLogger.info(ChannelStatsLogger.WXDL, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "fail";
            } else {

                orderService.paySuccess(order.getOrderId());
                return "ok";
            }
        } else {
            return "sigle error";
        }
    }

    @Override
    public String getWuxiandongliPaycode(HttpServletRequest request) {
        logger.debug("wxdl :", HttpUtils.getRequestParams(request).toString());
        String appId = request.getParameter("zdAppId");
        String platformId = request.getParameter("platformId");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (appId == null || platformId == null) {
            resultMap.put("code", 1);
            resultMap.put("desc", "appid is null or  channel id is null");
            return JsonMapper.toJson(resultMap);
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.parseInt(platformId), Long.parseLong(appId));
        if (channelGame == null) {
            resultMap.put("code", 1);
            resultMap.put("desc", "appid is error or  channel id is error");
            return JsonMapper.toJson(resultMap);
        }
        return channelGame.getConfigParamsList("\\|").get(1);
    }

    @Override
    public String verifyXiao7Session(Xiao7Session session) {
        logger.debug("xiao7:", session);
        Map<String, String> result = new HashMap<String, String>();

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            String sign = MD5.encode(session.getAppkey() + session.getTokenkey());
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("tokenkey", session.getTokenkey());
            params.put("sign", sign);
            String string = HttpUtils.post(url, params);
            return string;
        } catch (Exception e) {
            logger.error("verifyXiao7Session error", e);
            result.put("errorno", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyXiao7(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.XIAO7, HttpUtils.getRequestParams(request).toString());
        String encryp_data = request.getParameter("encryp_data");
        String xiao7_goid = request.getParameter("xiao7_goid");
        String game_orderid = request.getParameter("game_orderid");
        String guid = request.getParameter("guid");
        String subject = request.getParameter("subject");
        String sign_data = request.getParameter("sign_data");
        Order order = basicRepository.getOrderByOrderId(game_orderid);
        if (order == null) {
            return "error";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "error";
        }
        String appKey = channelGame.getConfigParamsList().get(1).trim();
        Map<String, String> paramMap = new TreeMap<String, String>();
        paramMap.put("encryp_data", encryp_data);
        paramMap.put("xiao7_goid", xiao7_goid);
        paramMap.put("game_orderid", game_orderid);
        paramMap.put("guid", guid);
        paramMap.put("subject", subject);
        VerifyXiao7.PUBLIC_KEY = appKey;
        try {
            String httpstring = VerifyXiao7.buildHttpQuery(paramMap);
            if (VerifyXiao7.doCheck(httpstring, sign_data, VerifyXiao7.loadPublicKeyByStr())) {

                String decryptData = new String(VerifyXiao7.decrypt(VerifyXiao7.loadPublicKeyByStr(), VerifyXiao7.decode(paramMap.get("encryp_data"))));
                if (decryptData == null) {
                    logger.info("decryptData is null");
                    return "decryptData null";
                }
                Map<String, String> decryptMap = VerifyXiao7.decodeHttpQuery(decryptData);

                if (decryptMap != null && decryptMap.containsKey("game_orderid") && decryptMap.get("game_orderid").equals(paramMap.get("game_orderid")) && decryptMap.get("payflag").equals("1")) {
                    if (order.getAmount() > (Float.parseFloat(decryptMap.get("pay")) * 100)) {
                        ChannelStatsLogger.info(ChannelStatsLogger.XIAO7, "order amount error: [order:" + order.getAmount() + "][pay:" + (Float.parseFloat(decryptMap.get("pay")) * 100) + "]");
                        orderService.payFail(order.getOrderId(), "order amount error");
                        return "failed amount error";
                    } else {
                        orderService.paySuccess(order.getOrderId());
                        return "success";
                    }
                } else {
                    logger.info("解密数据出错" + appKey);
                    return "failed 解密数据错误";
                }


            } else {
                logger.info("Sign error");
                return "verify_failed";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.info("系统异常:" + e.getMessage());
            return "failed 系统发生异常";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("系统异常xxxx" + e.getMessage());
            return "failed 系统发生异常";
        }
    }

    @Override
    public String verifyQuickSession(QuickSdkSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("errorno", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("errorno", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("token", session.getToken());
            params.put("product_code", session.getProduct_code());
            params.put("uid", session.getUid());
            String string = HttpUtils.post(url, params);
            return string;
        } catch (Exception e) {
            logger.error("Verify QuickSDK Session error", e);
            result.put("errorno", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyQuick(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.QUICKSDK, HttpUtils.getRequestParams(request).toString());
        String nt_data = request.getParameter("nt_data");
        String sign = request.getParameter("sign");
        String md5Sign = request.getParameter("md5Sign");
        String appid = request.getParameter("appid");
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(19, Long.parseLong(appid));
        if (channelGame == null) {
            return "error";
        }
        String localKey = channelGame.getConfigParamsList().get(1).trim();
        String md5SignLocal = MD5.encode(nt_data + sign + localKey);
        if (md5Sign.equals(md5SignLocal)) {
            String xmlData = IOSDesUtil.decode(nt_data, localKey);
            QuickXmlBean bean = XmlUtils.parserXML(xmlData);
            if (bean != null && bean.getStatus().equals("0")) {

                Order order = basicRepository.getOrderByOrderId(bean.getGame_order());
                if (order == null) {
                    return "error";
                }
                if (order.getAmount() > Float.parseFloat(bean.getAmount()) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.QUICKSDK, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "failed";
                } else {
                    orderService.paySuccess(order.getOrderId());
                    return "SUCCESS";
                }
            } else {
                ChannelStatsLogger.info(ChannelStatsLogger.QUICKSDK, "XML 解析出错:" + xmlData);
            }
        } else {

            //logger.info("QuickSdk 签名验证失败");
            ChannelStatsLogger.info(ChannelStatsLogger.QUICKSDK, "签名验证失败");
            return "verify_failed";
        }
        return null;
    }

    @Override
    public String verifyYijieSession(YijieSdkSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("errorno", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("errorno", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("sdk", session.getSdk());
            params.put("app", session.getApp());
            params.put("uin", session.getUin());
            params.put("sess", session.getSess());
            StringBuilder getUrl = new StringBuilder();
            getUrl.append(url);
            getUrl.append("?app=");
            getUrl.append(URLEncoder.encode(session.getApp(), "UTF-8"));
            getUrl.append("&sdk=");
            getUrl.append(URLEncoder.encode(session.getSdk(), "UTF-8"));
            getUrl.append("&uin=");
            getUrl.append(URLEncoder.encode(session.getUin(), "UTF-8"));
            getUrl.append("&sess=");
            getUrl.append(URLEncoder.encode(session.getSess(), "UTF-8"));
            String string = HttpUtils.get(getUrl.toString());
            if (string.equals("0")) {
                result.put("code", "0");
                result.put("msg", string);
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "2");
                result.put("msg", "服务器异常:" + string);
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("Verify QuickSDK Session error", e);
            result.put("errorno", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyYijie(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.YIJIESDK, HttpUtils.getRequestParams(request).toString());
        String appid = request.getParameter("appid");
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(17, Long.parseLong(appid));
        if (channelGame == null) {
            return "error";
        }
        String localKey = channelGame.getConfigParamsList().get(1).trim();
        StringBuffer sbEnc = new StringBuffer();
        sbEnc.append("app=");
        sbEnc.append(request.getParameter("app"));
        sbEnc.append("&cbi=");
        sbEnc.append(request.getParameter("cbi"));
        sbEnc.append("&ct=");
        sbEnc.append(request.getParameter("ct"));
        sbEnc.append("&fee=");
        sbEnc.append(request.getParameter("fee"));
        sbEnc.append("&pt=");
        sbEnc.append(request.getParameter("pt"));
        sbEnc.append("&sdk=");
        sbEnc.append(request.getParameter("sdk"));
        sbEnc.append("&ssid=");
        sbEnc.append(request.getParameter("ssid"));
        sbEnc.append("&st=");
        sbEnc.append(request.getParameter("st"));
        sbEnc.append("&tcd=");
        sbEnc.append(request.getParameter("tcd"));
        sbEnc.append("&uid=");
        sbEnc.append(request.getParameter("uid"));
        sbEnc.append("&ver=");
        sbEnc.append(request.getParameter("ver"));

        String sign = request.getParameter("sign");
        String cbi = request.getParameter("cbi");
        String fee = request.getParameter("fee");
        String st = request.getParameter("st");
        boolean result = MD5.encode(sbEnc + localKey).equalsIgnoreCase(sign);

        if (result) {

            Order order = basicRepository.getOrderByOrderId(cbi);
            if (order == null) {
                return "error";
            }
            if (!st.equals("1")) {
                orderService.payFail(order.getOrderId(), "order amount error");
                return "failed st =" + st;
            }
            if (order.getAmount() > Integer.parseInt(fee)) {
                ChannelStatsLogger.info(ChannelStatsLogger.YIJIESDK, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "failed";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "SUCCESS";
            }
        } else {

            //logger.info("QuickSdk 签名验证失败");
            ChannelStatsLogger.info(ChannelStatsLogger.YIJIESDK, "签名验证失败");
            return "verify_failed";
        }
    }

    @Override
    public String verifyKuaifaSession(KuaifaSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("result", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("result", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("token", session.getToken());
            params.put("openid", session.getOpenid());
            params.put("gamekey", session.getGamekey());
            params.put("timestamp", System.currentTimeMillis());
            String sign1 = Sign.signByMD5NoKey(params);
            String security_key = channelGame.getConfigParamsList().get(1);
            String sign = MD5.encode(sign1 + security_key);
            params.put("_sign", sign);
            String string = HttpUtils.post(url, params);
            return string;
        } catch (Exception e) {
            logger.error("Verify 快发 Session error", e);
            result.put("result", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyKuaifa(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.KUAIFA, HttpUtils.getRequestParams(request).toString());
        Map<String, String> resultMap = new HashMap<String, String>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("serial_number", request.getParameter("serial_number"));
        paramMap.put("cp", request.getParameter("cp"));
        paramMap.put("timestamp", request.getParameter("timestamp"));
        paramMap.put("result", request.getParameter("result"));
        paramMap.put("extend", request.getParameter("extend"));
        paramMap.put("server", request.getParameter("server"));
        paramMap.put("product_id", request.getParameter("product_id"));
        paramMap.put("product_num", request.getParameter("product_num"));
        paramMap.put("game_orderno", request.getParameter("game_orderno"));
        paramMap.put("amount", request.getParameter("amount"));
        String result = request.getParameter("result");
        String game_orderno = request.getParameter("game_orderno");
        String amount = request.getParameter("amount");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(game_orderno);
        if (order == null) {
            return "error order not Fund";
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            resultMap.put("result", "1");
            resultMap.put("result_desc", "游戏平台没有关联");
            return JsonMapper.toJson(resultMap);
        }
        String localKey = channelGame.getConfigParamsList().get(1).trim();
        try {
            String sign1 = Sign.signByMD5NoKey(paramMap);
            String validsign = MD5.encode(sign1 + localKey);
            if (sign.equals(validsign) && "0".equals(result)) {
                if (order.getAmount() > Float.parseFloat(amount) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.KUAIFA, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    resultMap.put("result", "1");
                    resultMap.put("result_desc", "金额不对");
                    return JsonMapper.toJson(resultMap);
                } else {
                    orderService.paySuccess(order.getOrderId());
                    resultMap.put("result", "0");
                    resultMap.put("result_desc", "ok");
                    return JsonMapper.toJson(resultMap);
                }
            } else {

                ChannelStatsLogger.info(ChannelStatsLogger.KUAIFA, "签名验证失败");
                resultMap.put("result", "2");
                resultMap.put("result_desc", "签名失败");
                return JsonMapper.toJson(resultMap);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultMap.put("result", "2");
            resultMap.put("result_desc", "system error");
            return JsonMapper.toJson(resultMap);
        }
    }

    @Override
    public String verifyFtxSession(FtxSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("result", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("result", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            String key = channelGame.getConfigParamsList().get(1);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("token", session.getToken());
            params.put("packageId", session.getPackageId());
            params.put("appId", session.getAppId());
            params.put("userId", session.getUserId());
            params.put("exInfo", session.getExInfo());
            String sign = Sign.signByMD5(params, key);
            params.put("sign", sign);
            String json = JsonMapper.toJson(params);
            Map<String, Object> p = new HashMap<String, Object>();
            p.put("data", json);
            String string = HttpUtils.post(url, p);
            string = URLDecoder.decode(string, "utf-8");
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Verify Ftx Session error", e);
            result.put("result", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyFtx(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<String, String>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            String paramString = HttpUtils.inputStream2String(request.getInputStream());
            ChannelStatsLogger.info(ChannelStatsLogger.FTX, paramString);
            JSONObject jsonObject = new JSONObject(paramString);
            String appBillNo = jsonObject.optString("appBillNo");
            paramMap.put("packageId", jsonObject.optString("packageId"));
            paramMap.put("platformBillNo", jsonObject.optString("platformBillNo"));
            String appExInfo = jsonObject.optString("appExInfo");
            if (!StringUtils.isEmpty(appExInfo)) {
                paramMap.put("appExInfo", jsonObject.optString("appExInfo"));
            }
            paramMap.put("amount", jsonObject.optString("amount"));
            paramMap.put("appBillNo", jsonObject.optString("appBillNo"));
            String channelBillNo = jsonObject.optString("channelBillNo");
            if (!StringUtils.isEmpty(channelBillNo)) {
                paramMap.put("channelBillNo", jsonObject.optString("channelBillNo"));
            }
            String amount = jsonObject.optString("amount");
            String sign = jsonObject.optString("sign");
            Order order = basicRepository.getOrderByOrderId(appBillNo);
            if (order == null) {
                return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                resultMap.put("result", "1");
                resultMap.put("result_desc", "游戏平台没有关联");
                return JsonMapper.toJson(resultMap);
            }
            String localKey = channelGame.getConfigParamsList().get(1).trim();
            String sign1 = Sign.signByMD5(paramMap, localKey);
            String validsign = MD5.encode(sign1 + localKey);
            if (sign.equals(validsign)) {
                if (order.getAmount() > Float.parseFloat(amount)) {
                    ChannelStatsLogger.info(ChannelStatsLogger.FTX, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    resultMap.put("result", "1");
                    resultMap.put("result_desc", "金额不对");
                    return JsonMapper.toJson(resultMap);
                } else {
                    orderService.paySuccess(order.getOrderId());
                    resultMap.put("result", "0");
                    resultMap.put("result_desc", "ok");
                    return JsonMapper.toJson(resultMap);
                }
            } else {

                ChannelStatsLogger.info(ChannelStatsLogger.FTX, "签名验证失败");
                resultMap.put("result", "2");
                resultMap.put("result_desc", "签名失败");
                return JsonMapper.toJson(resultMap);
            }
        } catch (IOException e) {
//            e.printStackTrace();
            resultMap.put("result", "3");
            resultMap.put("result_desc", "读取网络流错误");
            return JsonMapper.toJson(resultMap);

        } catch (JSONException e) {
//            e.printStackTrace();
            resultMap.put("result", "4");
            resultMap.put("result_desc", "JSON转化异常");
            return JsonMapper.toJson(resultMap);
        }
    }

    @Override
    public String verifyYihuanSession(YihuanSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("result", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("result", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String key = channelGame.getConfigParamsList("\\|").get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("USERID", session.getUserId());
            params.put("TIMESTAMP", session.getTimesTamp());
            params.put("APPKEY", key);
            String sign = MD5.encode(key + session.getUserId() + session.getTimesTamp()).toUpperCase();
            params.put("SIGNATURE", sign);
            String json = JsonMapper.toJson(params);
            Map<String, Object> p = new HashMap<String, Object>();
            p.put("data", json);
//            String string = HttpUtils.post(url, p);
//            string = URLDecoder.decode(string,"utf-8");
//            logger.info(string);
            result.put("code", "0");
            result.put("sigin", sign);
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify Yihuan Session error", e);
            result.put("result", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyYihuan(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.YIHUAN, HttpUtils.getRequestParams(request).toString());
        Map<String, String> resultMap = new HashMap<String, String>();
        String remark = request.getParameter("remark");
        String amount = request.getParameter("amount");
        String md5Str = request.getParameter("md5Str");
        String pOrderId = request.getParameter("pOrderId");
        String serverCode = request.getParameter("serverCode");
        String creditId = request.getParameter("creditId");
        String userId = request.getParameter("userId");
        String stone = request.getParameter("stone");
        String time = request.getParameter("time");
        Order order = basicRepository.getOrderByOrderId(remark);
        if (order == null) {
            return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            resultMap.put("code", "0100");
            resultMap.put("desc", "游戏平台没有关联");
            return JsonMapper.toJson(resultMap);
        }
        String localKey = channelGame.getConfigParamsList("\\|").get(0);
        String st = pOrderId + serverCode + creditId + userId + amount + stone + time + localKey;
        String validsign = MD5.encode(st).toUpperCase();
        if (md5Str.equals(validsign)) {
            if (order.getAmount() > Float.parseFloat(amount) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.YIHUAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                resultMap.put("code", "0010");
                resultMap.put("desc", "金额不对");
                return JsonMapper.toJson(resultMap);
            } else {
                orderService.paySuccess(order.getOrderId());
                resultMap.put("code", "0000");
                resultMap.put("result_desc", "ok");
                return JsonMapper.toJson(resultMap);
            }
        } else {

            ChannelStatsLogger.info(ChannelStatsLogger.YIHUAN, "签名验证失败 key:" + localKey + " st=" + st);
            resultMap.put("code", "0011");
            resultMap.put("result_desc", "签名失败");
            return JsonMapper.toJson(resultMap);
        }
    }

    @Override
    public String getYihuanPayCode(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        String zdappId = request.getParameter("zdappId");
        String platformId = request.getParameter("platformId");

        if (StringUtils.isBlank(zdappId) || StringUtils.isBlank(platformId)) {
            result.put("code", "1");
            result.put("msg", "params empty");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(platformId), Long.valueOf(zdappId));
        if (channelGame == null) {
            result.put("code", "1");
            result.put("msg", "params error");
            result.put("payCode", "");
            return JsonMapper.toJson(result);
        }
        String pointInfo = channelGame.getConfigParamsList("\\|").get(1);
        result.put("code", "0");
        result.put("data", pointInfo);
        return JsonMapper.toJson(result);
    }

    @Override
    public String verifyHongshouzhiSession(HongshouzhiSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("status", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("status", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (StringUtils.isEmpty(session.getUser_token())) {
            result.put("status", "2");
            result.put("msg", "[user_token] 为空请核对");
            return JsonMapper.toJson(result);
        }

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            String key = channelGame.getConfigParamsList().get(1);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("app_id", session.getApp_id());
            params.put("mem_id", session.getMem_id());
            params.put("user_token", session.getUser_token());
            params.put("app_key", key);
            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(session.getApp_id()).append("&");
            sb.append("mem_id=").append(session.getMem_id()).append("&");
            sb.append("user_token=").append(session.getUser_token()).append("&");
            sb.append("app_key=").append(key);
            String sign = MD5.encode(sb.toString());
            params.put("sign", sign);
            params.remove("app_key");
            String json = JsonMapper.toJson(params);
            logger.info(json);
            String string = HongShouZhiUtil.doRequest(url, json);
//            string = URLDecoder.decode(string, "utf-8");
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Verify hongshouzhi Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyHongshouzhi(HttpServletRequest request) {
        try {
            String paramString = HttpUtils.inputStream2String(request.getInputStream());
            ChannelStatsLogger.info(ChannelStatsLogger.HONGSHOUZHI, paramString);
            JSONObject jsonObject = new JSONObject(paramString);
            String order_id = jsonObject.optString("order_id");
            String mem_id = jsonObject.optString("mem_id");
            String app_id = jsonObject.optString("app_id");
            String money = jsonObject.optString("money");
            String order_status = jsonObject.optString("order_status");
            String paytime = jsonObject.optString("paytime");
            String attach = jsonObject.optString("attach");
            String sign = jsonObject.optString("sign");
            Order order = basicRepository.getOrderByOrderId(attach);
            if (order == null) {
                return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "Not Fund Game";
            }
            String appKey = channelGame.getConfigParamsList().get(1);
            if (order_status.equals("2")) {
                StringBuilder sb = new StringBuilder();
                sb.append("order_id=").append(order_id).append("&");
                sb.append("mem_id=").append(mem_id).append("&");
                sb.append("app_id=").append(app_id).append("&");
                sb.append("money=").append(money).append("&");
                sb.append("order_status=").append(order_status).append("&");
                sb.append("paytime=").append(paytime).append("&");
                sb.append("attach=").append(attach).append("&");
                sb.append("app_key=").append(appKey);
                String validString = MD5.encode(sb.toString());
                if (validString.equals(sign)) {
                    if (order.getAmount() > Float.parseFloat(money) * 100) {
                        ChannelStatsLogger.info(ChannelStatsLogger.HONGSHOUZHI, "order amount error");
                        orderService.payFail(order.getOrderId(), "order amount error");
                        return "FAILURE";
                    } else {
                        orderService.paySuccess(order.getOrderId());
                        return "SUCCESS";
                    }
                } else {

                    ChannelStatsLogger.info(ChannelStatsLogger.HONGSHOUZHI, "签名验证失败 key:" + appKey + " st=" + sb.toString());
                    return "FAILURE";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "FAILURE";
    }

    @Override
    public String submitHongshouzhiRole(HongShouZhiRole session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.getZdAppId() == null) {
            result.put("status", "2");
            result.put("msg", "[zdAppId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (session.getPlatformId() == null) {
            result.put("status", "2");
            result.put("msg", "[platformId] 为空请核对");
            return JsonMapper.toJson(result);
        }
        if (StringUtils.isEmpty(session.getUser_token())) {
            result.put("status", "2");
            result.put("msg", "[user_token] 为空请核对");
            return JsonMapper.toJson(result);
        }

        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(2);
            String key = channelGame.getConfigParamsList().get(1);
            StringBuilder sb = new StringBuilder();
            sb.append("mem_id=").append(session.getMem_id()).append("&");
            sb.append("app_id=").append(session.getApp_id()).append("&");
            sb.append("server=").append(session.getServer()).append("&");
            sb.append("role=").append(session.getRole()).append("&");
            sb.append("money=").append(session.getMoney()).append("&");
            sb.append("level=").append(session.getLevel()).append("&");
            sb.append("experience=").append(session.getExperience()).append("&");
            sb.append("user_token=").append(session.getUser_token()).append("&");
            sb.append("appkey=").append(key);
            logger.info("############:" + sb.toString());
            String sign = MD5.encode(sb.toString());
            String jsonStr = JsonMapper.toJson(session);
            JSONObject jsonObject = new JSONObject(jsonStr);
            jsonObject.put("sign", sign);
            logger.info("url=" + url + "==========================:" + jsonObject.toString());
            String string = HongShouZhiUtil.doRequest(url, jsonObject.toString());
//            string = URLDecoder.decode(string, "utf-8");
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Upload hongshouzhi Role error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyFansdkSession(FansdkSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (channelGame == null) {
                return "";
            }

            String url = channelGame.getConfigParamsList().get(0);
            String key = channelGame.getConfigParamsList().get(1);
            String sign = MD5.encode("userID=" + session.getUserID() + "token=" + session.getToken() + key);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userID", session.getUserID());
            params.put("token", session.getToken());
            params.put("sign", sign);
            String string = HttpUtils.post(url, params);
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Verify hongshouzhi Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyFansdk(HttpServletRequest request) {
        try {
            String paramString = HttpUtils.inputStream2String(request.getInputStream());
            ChannelStatsLogger.info(ChannelStatsLogger.FANSDK, paramString);
            JSONObject jsonObject = new JSONObject(paramString);
            if (!"1".equals(jsonObject.optString("status"))) {
                return "FAIL";
            }
            JSONObject dataObject = jsonObject.getJSONObject("data");
            UOrder uOrder = new UOrder();
            uOrder.setProductID(dataObject.optString("productID"));
            uOrder.setOrderID(Long.parseLong(dataObject.optString("OrderID")));
            uOrder.setUserID(Integer.parseInt(dataObject.optString("userID")));
            uOrder.setChannelID(Integer.parseInt(dataObject.optString("channelID")));
            uOrder.setAppID(Integer.parseInt(dataObject.optString("gameID")));
            uOrder.setServerID(dataObject.optString("serverID"));
            uOrder.setMoney(Integer.parseInt(dataObject.optString("money")));
            uOrder.setCurrency(dataObject.optString("currency"));
            uOrder.setExtension(dataObject.optString("extension"));
            String signType = dataObject.optString("signType");
            String sign = dataObject.optString("sign");
            Order order = basicRepository.getOrderByOrderId(uOrder.getExtension());
            if (order == null) {
                return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
            }
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (channelGame == null) {
                return "Not Fund Game";
            }
            String appScripte = channelGame.getConfigParamsList().get(1);
            String privateKey = channelGame.getConfigParamsList().get(2);
            String validString = FansdkSigleUtils.generateSign(uOrder, signType, appScripte, privateKey);
            if (validString.equals(sign)) {
                if (order.getAmount() > uOrder.getMoney()) {
                    ChannelStatsLogger.info(ChannelStatsLogger.FANSDK, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "FAILURE";
                } else {
                    orderService.paySuccess(order.getOrderId());
                    return "SUCCESS";
                }
            } else {

                ChannelStatsLogger.info(ChannelStatsLogger.FANSDK, "签名验证失败 ");
                return "FAILURE";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "FAILURE";
    }

    @Override
    public String verifyNiguangSession(NiguangSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (channelGame == null) {
                return "";
            }
            String url = channelGame.getConfigParamsList().get(0);
            String key = channelGame.getConfigParamsList().get(1);
            long time = System.currentTimeMillis();
            String sessionid = URLDecoder.decode(session.getSessionid(), "utf-8");
            StringBuilder sb = new StringBuilder();
            sb.append("ac=check").append("&");
            sb.append("appid=").append(session.getAppid()).append("&");
            sb.append("sdkversion=3.2").append("&");
            sb.append("sessionid=").append(URLEncoder.encode(sessionid, "utf-8")).append("&");
            sb.append("time=").append(time);
            sb.append(key);
            String sign = MD5.encode(sb.toString());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ac", "check");
            params.put("appid", session.getAppid());
            params.put("sdkversion", "3.2");
            params.put("sessionid", sessionid);
            params.put("time", time);
            params.put("sign", sign);
            String string = HttpUtils.post(url, params);
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Verify Niguang Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyNiguangsdk(HttpServletRequest request) {
        ChannelStatsLogger.info(ChannelStatsLogger.NIGUANG, HttpUtils.getRequestParams(request).toString());
        Map<String, String> resultMap = new HashMap<String, String>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cporderid", request.getParameter("cporderid"));
        paramMap.put("orderid", request.getParameter("orderid"));
        paramMap.put("appid", request.getParameter("appid"));
        paramMap.put("uid", request.getParameter("uid"));
        paramMap.put("time", request.getParameter("time"));
        paramMap.put("extinfo", URLEncoder.encode(request.getParameter("extinfo")));
        paramMap.put("amount", request.getParameter("amount"));
        paramMap.put("serverid", URLEncoder.encode(request.getParameter("serverid")));
        paramMap.put("charid", URLEncoder.encode(request.getParameter("charid")));
        paramMap.put("gold", request.getParameter("gold"));
        Order order = basicRepository.getOrderByOrderId(request.getParameter("cporderid"));
        if (order == null) {
            return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (channelGame == null) {
            return "Not Fund Game";
        }
        String privateKey = channelGame.getConfigParamsList().get(2);
        String validString = Sign.signByMD5(paramMap, privateKey);
        String sign = request.getParameter("sign");
        logger.info("validString  =  " + validString);
        logger.info("sign  =  " + sign);
        if (validString.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(request.getParameter("amount")) * 100) {
                ChannelStatsLogger.info(ChannelStatsLogger.NIGUANG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "FAILURE";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "SUCCESS";
            }
        } else {

            ChannelStatsLogger.info(ChannelStatsLogger.FANSDK, "签名验证失败 ");
            return "FAILURE";
        }
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
