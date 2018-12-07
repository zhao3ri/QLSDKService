package com.qinglan.sdk.server.application.platform.impl;

import com.lenovo.pay.sign.JsonUtil;
import com.qinglan.sdk.server.application.platform.ChannelUtilsService;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.platform.qq.JSONException;
import com.qinglan.sdk.server.presentation.channel.IChannel;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.impl.HmsChannel;
import com.qinglan.sdk.server.presentation.platform.dto.dtotwo.*;
import com.qinglan.sdk.server.platform.ibei.SignHelper;
import com.qinglan.sdk.server.platform.lewan.util.MD5Util;
import com.qinglan.sdk.server.platform.qq.JSONObject;
import com.qinglan.sdk.server.platform.six7.Six7payUtils;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.application.basic.redis.RedisUtil;
import com.qinglan.sdk.server.application.platform.ChannelServicePartTwo;
import com.qinglan.sdk.server.application.platform.log.PlatformStatsLogger;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.presentation.platform.dto.AoChuangSession;
import com.qinglan.sdk.server.presentation.platform.dto.MangGuoWanSession;
import com.qinglan.sdk.server.presentation.platform.dto.Six7Session;
import com.qinglan.sdk.server.presentation.platform.dto.TaoShouYouSession;
import egame.openapi.common.RequestParasUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN;

/**
 * Created by engine on 2016/10/21.
 */
@Service
public class ChannelServicePartTwoImpl implements ChannelServicePartTwo {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServicePartTwoImpl.class);
    @Resource
    private BasicRepository basicRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private ChannelUtilsService channelUtilsService;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public String signOrderHuawei(HMSPaySignRequest request) {
        IChannel channel = new HmsChannel();
        channel.init(basicRepository, request.getGameId(), request.getPlatformId());
        return channel.signOrder(request);
    }

    @Override
    public String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        IChannel channel = new HmsChannel();
        channel.init(basicRepository);
        String result = channel.returnPayResult(request, orderService);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("HMS Response string: " + result);
        PrintWriter out = response.getWriter();

        out.print(result);
        out.close();
        return null;
    }

    @Override
    public String verifyHuawei(HMSVerifyRequest request) {
        IChannel channel = new HmsChannel();
        channel.init(basicRepository, request.getGameId(), request.getPlatformId());
        //顺序需相同
        String result = channel.verifySession(request.getAppID(), request.getCpID(), request.getTs()
                , request.getPlayerId(), request.getPlayerLevel(), request.getPlayerSSign());
        return result;
    }

    @Override
    public String verifyAoChuangsdk(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.AOCHUANG, HttpUtils.getRequestParams(request).toString());
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
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            return "Not Fund Game";
        }
        String privateKey = platformGame.getConfigParamsList().get(2);
        String validString = Sign.signByMD5(paramMap, privateKey);
        String sign = request.getParameter("sign");
        if (validString.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(request.getParameter("amount")) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.AOCHUANG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "FAILURE";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "SUCCESS";
            }
        } else {

            PlatformStatsLogger.info(PlatformStatsLogger.FANSDK, "签名验证失败 ");
            return "FAILURE";
        }
    }

    @Override
    public String verifyAoChuangSession(AoChuangSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "";
            }
            String url = platformGame.getConfigParamsList().get(0);
            String key = platformGame.getConfigParamsList().get(1);
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
            logger.error("Verify Aochuang Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyPaPaYou(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.PAPAYOU, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("app_key", request.getParameter("app_key"));
        paramMap.put("app_order_id", request.getParameter("app_order_id"));
        paramMap.put("app_district", request.getParameter("app_district"));
        paramMap.put("app_server", request.getParameter("app_server"));
        paramMap.put("app_user_id", request.getParameter("app_user_id"));
        paramMap.put("app_user_name", request.getParameter("app_user_name"));
        paramMap.put("product_id", request.getParameter("product_id"));
        paramMap.put("product_name", request.getParameter("product_name"));
        paramMap.put("money_amount", request.getParameter("money_amount"));
        paramMap.put("pa_open_uid", request.getParameter("pa_open_uid"));
        paramMap.put("app_extra1", request.getParameter("app_extra1"));
        paramMap.put("app_extra2", request.getParameter("app_extra2"));
        paramMap.put("pa_open_order_id", request.getParameter("pa_open_order_id"));
        Order order = basicRepository.getOrderByOrderId(request.getParameter("app_order_id"));
        if (order == null) {
            return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            return "Not Fund Game";
        }
        String appKey = platformGame.getConfigParamsList().get(0);
        String appSecretKey = platformGame.getConfigParamsList().get(1);
        String key = appKey.concat(appSecretKey);
        String validString = Sign.signByMD5KeyPre(paramMap, key);
        String sign = request.getParameter("sign");
        if (validString.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(request.getParameter("money_amount")) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.PAPAYOU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "FAILURE";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "ok";
            }
        } else {

            PlatformStatsLogger.info(PlatformStatsLogger.FANSDK, "签名验证失败 ");
            return "FAILURE";
        }
    }

    @Override
    public String verifyTaoShouYouSession(TaoShouYouSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "";
            }
            String url = platformGame.getConfigParamsList().get(0);
            String appid = platformGame.getConfigParamsList().get(1);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userid", session.getUserid());
            params.put("token", session.getToken());
            params.put("appid", appid);
            String string = HttpUtils.post(url, params);
            logger.info(string);
            if ("success".equals(string)) {
                result.put("code", "1");
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "0");
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("Verify TaoShouYou Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyTaoShouYousdk(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.TAOSHOUYOU, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appid", request.getParameter("appid"));
        paramMap.put("bizno", request.getParameter("bizno"));
        paramMap.put("goods_data", request.getParameter("goods_data"));
        paramMap.put("total_fee", request.getParameter("total_fee"));
        String orderId = "";
        try {
            JSONObject goodsData = new JSONObject(request.getParameter("goods_data"));
            orderId = goodsData.optString("orderId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null) {
            return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            return "Not Fund Game";
        }
        String appSecretKey = platformGame.getConfigParamsList().get(2);
        String validString = Sign.signByMD5(paramMap, appSecretKey);
        String sign = request.getParameter("signature");
        if (validString.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(request.getParameter("total_fee")) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.TAOSHOUYOU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                return "fail";
            } else {
                orderService.paySuccess(order.getOrderId());
                return "success";
            }
        } else {

            PlatformStatsLogger.info(PlatformStatsLogger.TAOSHOUYOU, "签名验证失败 ");
            return "fail";
        }
    }

    @Override
    public String verifyMangGuoWanSession(MangGuoWanSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "";
            }
            String url = platformGame.getConfigParamsList().get(0);
            String appid = platformGame.getConfigParamsList().get(1);
            String appkey = platformGame.getConfigParamsList().get(2);
            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(appid).append("&");
            sb.append("mem_id=").append(session.getMemId()).append("&");
            sb.append("user_token=").append(session.getUserToken()).append("&");
            sb.append("app_key=").append(appkey);
            String sign = MD5.encode(sb.toString());
            logger.info("sb.toString() = " + sb.toString());
            logger.info("sign = " + sign);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("app_id", appid);
            params.put("mem_id", session.getMemId());
            params.put("user_token", session.getUserToken());
            params.put("sign", sign);
            String string = HttpUtils.post(url, JsonMapper.toJson(params));
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Verify MangGuoWan Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyMangGuoWansdk(HttpServletRequest request) {
        try {
            String paramString = HttpUtils.inputStream2String(request.getInputStream());
            PlatformStatsLogger.info(PlatformStatsLogger.MANGGUOWAN, paramString);
            JSONObject dataObject = new JSONObject(paramString);
            String orderId = dataObject.optString("attach");
            if (!"2".equals(dataObject.optString("order_status"))) {
                return "FAIL";
            }
            String sign = dataObject.optString("sign");
            Order order = basicRepository.getOrderByOrderId(orderId);
            if (order == null) {
                return "error order not Fund param:" + HttpUtils.getRequestParams(request).toString();
            }
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
            if (platformGame == null) {
                return "Not Fund Game";
            }
            String appkey = platformGame.getConfigParamsList().get(2);
            StringBuilder sb = new StringBuilder();
            sb.append("order_id=").append(dataObject.optString("order_id")).append("&");
            sb.append("mem_id=").append(dataObject.optString("mem_id")).append("&");
            sb.append("app_id=").append(dataObject.optString("app_id")).append("&");
            sb.append("money=").append(dataObject.optString("money")).append("&");
            sb.append("order_status=").append(dataObject.optString("order_status")).append("&");
            sb.append("paytime=").append(dataObject.optString("paytime")).append("&");
            sb.append("attach=").append(orderId).append("&");
            sb.append("app_key=").append(appkey);
            String validString = MD5.encode(sb.toString());
            logger.info("validString ============= " + validString);
            logger.info("sign ============= " + sign);
            if (validString.equals(sign)) {
                if (order.getAmount() > Float.parseFloat(dataObject.optString("money")) * 100) {
                    PlatformStatsLogger.info(PlatformStatsLogger.MANGGUOWAN, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "FAILURE";
                } else {
                    orderService.paySuccess(order.getOrderId());
                    return "SUCCESS";
                }
            } else {

                PlatformStatsLogger.info(PlatformStatsLogger.MANGGUOWAN, "签名验证失败 ");
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
    public String verifyQinmuSession(QinmuSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "";
            }
            String url = platformGame.getConfigParamsList().get(1);
            String app_secret = platformGame.getConfigParamsList().get(0);
            long time = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer("authorize_code=");
            sb.append(session.getAuthorize_code());
            sb.append("&").append("app_key=").append(session.getApp_key());
            sb.append("&").append("jh_sign=").append(app_secret);
            sb.append("&").append("time=").append(time);
            String sigin = MD5.encode(sb.toString());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("app_key", session.getApp_key());
            params.put("authorize_code", session.getAuthorize_code());
            params.put("sign", sigin);
            params.put("time", time);

            String string = HttpUtils.doPost(url, params);
            logger.info(string);
            return string;
        } catch (Exception e) {
            logger.error("Verify 青木 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyQimu(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.QINGMU, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String app_key = request.getParameter("app_key");
        String product_id = request.getParameter("product_id");
        String total_fee = request.getParameter("total_fee");
        String app_role_id = request.getParameter("app_role_id");
        String user_id = request.getParameter("user_id");
        String order_id = request.getParameter("order_id");
        String app_order_id = request.getParameter("app_order_id");
        String server_id = request.getParameter("server_id");
        String sign = request.getParameter("sign");
        String time = request.getParameter("time");
        String pay_result = request.getParameter("pay_result");
        Order order = basicRepository.getOrderByOrderId(app_order_id);
        if (order == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return JsonMapper.toJson(returnMap);
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return JsonMapper.toJson(returnMap);
        }
        if (!pay_result.equals("1")) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Pay Fail " + pay_result);
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return JsonMapper.toJson(returnMap);
        }
        String app_secret = platformGame.getConfigParamsList().get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("app_key=").append(app_key).append("&");
        sb.append("app_order_id=").append(app_order_id).append("&");
        sb.append("app_role_id=").append(app_role_id).append("&");
        sb.append("order_id=").append(order_id).append("&");
        sb.append("pay_result=").append(pay_result).append("&");
        sb.append("product_id=").append(product_id).append("&");
        sb.append("server_id=").append(server_id).append("&");
        sb.append("total_fee=").append(total_fee).append("&");
        sb.append("user_id=").append(user_id).append("&");
        sb.append("jh_sign=").append(app_secret).append("&");
        sb.append("time=").append(time);
        String validSigin = MD5.encode(sb.toString());
        if (validSigin.equals(sign)) {
            if (order.getAmount() > Integer.parseInt(total_fee)) {
                PlatformStatsLogger.info(PlatformStatsLogger.QINGMU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 0);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return JsonMapper.toJson(returnMap);
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 1);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return JsonMapper.toJson(returnMap);
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.QINGMU, "签名验证失败 ");
            returnMap.put("ret", 0);
            returnMap.put("msg", "Sigin error");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return JsonMapper.toJson(returnMap);
        }
    }

    @Override
    public String verifyChangqu(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.CHANGQU, HttpUtils.getRequestParams(request).toString());
        String transdata = request.getParameter("transdata");
        String sign = (String) request.getParameter(RESPONSE_KEY_SIGN);
        String signtype = request.getParameter("signtype");

        String orderId = null;
        float money = 0f;
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(transdata);
            orderId = jsonObject.optString("cporderid");
            money = Float.parseFloat(jsonObject.optString("money"));
            result = jsonObject.optString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null) {
            return "Order not fund";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());

        String platp_key = platformGame.getConfigParamsList().get(2);
        if (platformGame == null) {
            return "platform not unition";
        }
        if (signtype == null) {
            return "signType null";
        } else {
            if (SignHelper.verify(transdata, sign, platp_key)) {
                if (order.getAmount() > money * 100) {
                    PlatformStatsLogger.info(PlatformStatsLogger.CHANGQU, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "money not match";
                } else {
                    if (result.equals("0")) {
                        orderService.paySuccess(order.getOrderId());
                        return "SUCCESS";
                    } else {
                        return "result:" + result;
                    }
                }
            } else {
                return "verify fail";
            }
        }
    }

    @Override
    public String verifyQitianlediSession(QitianlediSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                result.put("code", "1");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", session.getUid());
            params.put("session_id", session.getSession_id());
            params.put("pid", "1");
            String string = HttpUtils.doPost(url, params);
            logger.info(string);
            result.put("code", "0");
            result.put("msg", string);
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify 奇天乐地 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyQitianledi(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.QITIANLEDI, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String sid = request.getParameter("sid");
        String orderid = request.getParameter("orderid");
        String cash = request.getParameter("cash");
        String money = request.getParameter("money");
        String amount = request.getParameter("amount");
        String way_name = request.getParameter("way_name");
        String exchange = request.getParameter("exchange");
        String pay_time = request.getParameter("pay_time");
        String uid = request.getParameter("uid");
        String extra = request.getParameter("extra");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(extra);
        if (order == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }

        String app_secret = platformGame.getConfigParamsList().get(1);

        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append(amount);
        sb.append("cash=").append(cash);
        sb.append("exchange=").append(exchange);
        sb.append("extra=").append(extra);
        sb.append("money=").append(money);
        sb.append("orderid=").append(orderid);
        sb.append("pay_time=").append(pay_time);
        sb.append("sid=").append(sid);
        sb.append("uid=").append(uid);
        sb.append("way_name=").append(way_name);
        sb.append(app_secret);
        String validSigin = MD5.encode(sb.toString());
        logger.info(validSigin);
        if (validSigin.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(money) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.QITIANLEDI, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 0);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "0";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 1);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "1";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.QITIANLEDI, "签名验证失败 ");
            returnMap.put("ret", 0);
            returnMap.put("msg", "Sigin error");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }
    }

    @Override
    public String verifyCangluanSession(CangluanSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            int platformId = Integer.valueOf(session.getPlatformId());
            long ygAppId = Long.valueOf(session.getYgAppId());
            //logger.info(session.toString());
            logger.info("platformId:" + platformId + ";ygAppId:" + ygAppId);
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(platformId, ygAppId);
            logger.info(JsonMapper.toJson(platformGame));
            if (platformGame == null) {
                result.put("code", "0");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            long time = System.currentTimeMillis();
            List<String> msgLst = platformGame.getConfigParamsList();
            logger.info("msgLst:" + msgLst.toString());
            String url = platformGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("game_id", session.getGame_id());
            params.put("user_id", session.getUser_id());
            params.put("login_time", time);
            params.put("token", session.getToken());
            params.put("channel_id", session.getChannel_id());
            //logger.info(url);
            logger.info(params.toString());
            String string = HttpUtils.doPost(url, params);
            logger.info(string);
            result.put("code", "1");
            result.put("msg", string);
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify 苍鸾 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyCangluan(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.CANGLUAN, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String player_id = request.getParameter("player_id");
        String goods_id = request.getParameter("goods_id");
        String order_number = request.getParameter("order_number");
        String money = request.getParameter("money");
        String currency = request.getParameter("currency");
        String pay_way = request.getParameter("pay_way");
        String game_order_code = request.getParameter("game_order_code");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(game_order_code);
        if (order == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "1";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "1";
        }

        String game_key = platformGame.getConfigParamsList().get(1);
        logger.info(game_key);
        StringBuilder sb = new StringBuilder();
        sb.append(currency);
        sb.append(game_order_code);
        sb.append(goods_id);
        sb.append(money);
        sb.append(order_number);
        sb.append(pay_way);
        sb.append(player_id);
        sb.append(game_key);
        String validSigin = MD5.encode(sb.toString());
        validSigin = validSigin.toUpperCase();//转大写
        logger.info(validSigin);
        if (validSigin.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(money) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.CANGLUAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "1";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "0";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.CANGLUAN, "签名验证失败 ");
            returnMap.put("ret", 1);
            returnMap.put("msg", "Sigin error");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "1";
        }
    }

    @Override
    public String verifyLingdongSession(LingdongSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            int platformId = Integer.valueOf(session.getPlatformId());
            long ygAppId = Long.valueOf(session.getYgAppId());
            logger.info("platformId:" + platformId + ";ygAppId:" + ygAppId);
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(platformId, ygAppId);
            logger.info(JsonMapper.toJson(platformGame));
            if (platformGame == null) {
                result.put("code", "0");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }

            String ldurl = platformGame.getConfigParamsList().get(0);
            String appkey = platformGame.getConfigParamsList().get(1);
            String userid = session.getUser_id();
            String ldToken = session.getToken();
            String ldAppId = session.getLdAppId();
            int user_id = Integer.valueOf(userid);

            Map<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("user_id", user_id);
            body.put("token", ldToken);
            String bodyJsonStr = JsonMapper.toJson(body);
            String sign = MD5.encode(bodyJsonStr + appkey);
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("appid", ldAppId);
            params.put("sign", sign);
            String data = "appid=" + ldAppId + "&" + "sign=" + sign;
            String url = ldurl + "?" + data;

            //String msg = HttpUtils.doPostToJson(ldurl, data, 10000);

            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod(url);
            httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(1000 * 30);//链接超时30秒
            httpclient.getHttpConnectionManager().getParams().setSoTimeout(1000 * 30); //读取超时30秒
            post.addRequestHeader("Content-Type", "application/json; charset=UTF-8");
            RequestEntity entity = new StringRequestEntity(bodyJsonStr, "application/json", "UTF-8");
            post.setRequestEntity(entity);
            httpclient.executeMethod(post);
            String info = new String(post.getResponseBody(), "utf-8");

            logger.info("加密的appkey：" + appkey);
            logger.info("请求的URL：" + ldurl);
            logger.info("发送的键值：" + data);
            logger.info("传入的body：" + bodyJsonStr);
            logger.info("返回：" + info);

            JSONObject infoObj = new JSONObject(info);
            int code = infoObj.optInt("retcode");
            String msg = infoObj.optString("retmsg");
            JSONObject reBody = infoObj.optJSONObject("body");
            if (code == 0) {
                result.put("code", "1");
                result.put("msg", msg);
                result.put("body", reBody.toString());
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "0");
                result.put("msg", msg);
                result.put("body", reBody.toString());
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("Verify 灵动 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyLingdong(HttpServletRequest request) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try {
            String keyValue = HttpUtils.getRequestParams(request).toString();
            String bodyJson = HttpUtils.inputStream2String(request.getInputStream());
            PlatformStatsLogger.info(PlatformStatsLogger.LINGDONG, "返回键值：" + keyValue + "；body：" + bodyJson);

            JSONObject bodyObj = new JSONObject(bodyJson);
            //Double amount = bodyObj.optDouble("amount");
            //Double amount_usd = bodyObj.optDouble("amount_usd");
            //int coin = bodyObj.optInt("coin");
            String cp_ext = bodyObj.optString("cp_ext");
            //String cp_role_id = bodyObj.optString("cp_role_id");
            //String currency = bodyObj.optString("currency");
            //String pay_channel = bodyObj.optString("pay_channel");
            //String pay_no = bodyObj.optString("pay_no");
            //String product_id = bodyObj.optString("product_id");
            //int role_id = bodyObj.optInt("role_id");
            //int user_id = bodyObj.optInt("user_id");

            Order order = basicRepository.getOrderByOrderId(cp_ext);
            if (order == null) {
                returnMap.put("ret", 1);
                returnMap.put("msg", "Can find Order");
                returnMap.put("content", "");
                logger.info(JsonMapper.toJson(returnMap));
                return "1";
            }
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
            if (platformGame == null) {
                returnMap.put("ret", 1);
                returnMap.put("msg", "Can find Game");
                returnMap.put("content", "");
                logger.info(JsonMapper.toJson(returnMap));
                return "1";
            }

            String appkey = platformGame.getConfigParamsList().get(1);
            //String url = platformGame.getConfigParamsList().get(2);

            //Map<String, Object> body = new LinkedHashMap<String, Object>();
            //body.put("amount", amount);
            //body.put("amount_usd", amount_usd);
            //body.put("coin", coin);
            //body.put("cp_ext", cp_ext);
            //body.put("cp_role_id", cp_role_id);
            //body.put("currency", currency);
            //body.put("pay_channel", pay_channel);
            //body.put("pay_no", pay_no);
            //body.put("product_id", product_id);
            //body.put("role_id", role_id);
            //body.put("user_id", user_id);
            //String jsonStr = JsonMapper.toJson(body);
            StringBuilder sb = new StringBuilder();
            sb.append(bodyJson);
            sb.append(appkey);
            String validSign = MD5.encode(sb.toString());
            JSONObject keyValueObj = new JSONObject(keyValue);
            String sign = keyValueObj.optString("sign");

            PlatformStatsLogger.info(PlatformStatsLogger.LINGDONG, "加密的appkey：" + appkey);
            PlatformStatsLogger.info(PlatformStatsLogger.LINGDONG, "签名结果：" + validSign);
            if (validSign.equals(sign)) {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "ok";
            } else {
                PlatformStatsLogger.info(PlatformStatsLogger.LINGDONG, "签名验证失败 ");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Sigin error");
                returnMap.put("content", "");
                logger.info(JsonMapper.toJson(returnMap));
                return "1";
            }
        } catch (Exception e) {
            logger.error("Verify 灵动 Session error", e);
            returnMap.put("ret", 1);
            returnMap.put("msg", "回调解析 error");
            logger.info(JsonMapper.toJson(returnMap));
            return "1";
        }
    }

    @Override
    public String verifyZhizhuyouSession(ZhizhuyouSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            logger.error("**********************【ygAppId或platformId 为空】");
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            int platformId = Integer.valueOf(session.getPlatformId());
            long ygAppId = Long.valueOf(session.getYgAppId());
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(platformId, ygAppId);
            if (platformGame == null) {
                result.put("code", "0");
                result.put("msg", "platorm not unition");
                logger.error("**********************【platorm not unition】");
                return JsonMapper.toJson(result);
            }

            long loginTime = System.currentTimeMillis();
            String time = Long.toString(loginTime / 1000);
            String sessionid = session.getSessionid();
            String sessionIdUrlDecode = URLDecoder.decode(sessionid);
            String zzyAppId = session.getZzyAppId();
            String ac = "check";
            String sdkversion = "4.1";
            logger.error("**********************【ac】:" + ac);
            logger.error("**********************【zzyAppId】:" + zzyAppId);
            logger.error("**********************【sdkversion】:" + sdkversion);
            logger.error("**********************【sessionid】:" + sessionIdUrlDecode);
            logger.error("**********************【sessionidUrlEncode】:" + sessionid);
            logger.error("**********************【time】:" + time);

            String url = platformGame.getConfigParamsList().get(0);
            String appkey = platformGame.getConfigParamsList().get(1);
            //logger.error("**********************【url】:" + url);
            //logger.error("**********************【appkey】:" + appkey);

            StringBuffer sb = new StringBuffer();
            sb.append("ac=").append(ac).append("&");
            sb.append("appid=").append(zzyAppId).append("&");
            sb.append("sdkversion=").append(sdkversion).append("&");
            sb.append("sessionid=").append(sessionid).append("&");
            sb.append("time=").append(time).append(appkey);

            String signStr = sb.toString();
            String sign = MD5.encode(signStr);

            logger.error("**********************【signStr】:" + signStr);
            logger.error("**********************【sign】:" + sign);

            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("ac", ac);
            params.put("appid", zzyAppId);
            params.put("sdkversion", sdkversion);
            params.put("sessionid", sessionIdUrlDecode);
            params.put("time", time);
            params.put("sign", sign);

            String msg = HttpUtils.doPost(url, params);
            logger.error("**********************【msg】:" + msg);
            JSONObject msgJson = new JSONObject(msg);
            int code = msgJson.optInt("code");
            if (code == 1) {
                result.put("code", "1");
                result.put("msg", msg);
            } else {
                result.put("code", "0");
                result.put("msg", msg);
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify 智蛛游 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyZhizhuyou(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String cporderid = request.getParameter("cporderid");
        String orderid = request.getParameter("orderid");
        String appid = request.getParameter("appid");
        String uid = request.getParameter("uid");
        String time = request.getParameter("time");
        String extinfo = request.getParameter("extinfo");
        String amount = request.getParameter("amount");
        String serverid = request.getParameter("serverid");
        String charid = request.getParameter("charid");
        String gold = request.getParameter("gold");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(cporderid);
        if (order == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, JsonMapper.toJson(returnMap));
            return "1";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, JsonMapper.toJson(returnMap));
            return "1";
        }

        String pay_key = platformGame.getConfigParamsList().get(2);
        PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, pay_key);
        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append(amount).append("&");
        sb.append("appid=").append(appid).append("&");
        sb.append("charid=").append(URLEncoder.encode(charid)).append("&");
        sb.append("cporderid=").append(cporderid).append("&");
        sb.append("extinfo=").append(URLEncoder.encode(extinfo)).append("&");
        sb.append("gold=").append(gold).append("&");
        sb.append("orderid=").append(orderid).append("&");
        sb.append("serverid=").append(URLEncoder.encode(serverid)).append("&");
        sb.append("time=").append(time).append("&");
        sb.append("uid=").append(uid);
        sb.append(pay_key);
        String validSigin = MD5.encode(sb.toString());
        PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, validSigin);
        if (validSigin.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(amount) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "ERROR";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "SUCCESS";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.ZHIZHUYOU, "签名验证失败 ");
            returnMap.put("ret", 1);
            returnMap.put("msg", "Sigin error");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "ERROR";
        }
    }

    @Override
    public String verifyXingkongshijieSession(XingkongshijieSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            logger.error("**********************【ygAppId或platformId 为空】");
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            int platformId = Integer.valueOf(session.getPlatformId());
            long ygAppId = Long.valueOf(session.getYgAppId());
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(platformId, ygAppId);
            if (platformGame == null) {
                result.put("code", "0");
                result.put("msg", "platorm not unition");
                logger.error("**********************【platorm not unition】");
                return JsonMapper.toJson(result);
            }

            String appid = session.getAppid();
            String logintoken = session.getLogintoken();
            String url = platformGame.getConfigParamsList().get(0);
            String APPV_KEY = platformGame.getConfigParamsList().get(1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appid", appid);
            jsonObject.put("logintoken", logintoken);
            String content = jsonObject.toString();// 组装成 json格式数据
            String sign = SignHelper.sign(content, APPV_KEY);// 调用签名函数
            String data = "transdata=" + URLEncoder.encode(content) + "&sign=" + URLEncoder.encode(sign) + "&signtype=RSA";// 组装请求参数

            String returnMsg = HttpUtils.doPost(url, data, 10000);
            String returnMsgDecode = URLDecoder.decode(returnMsg);
            logger.error("**********************【returnMsg】:" + returnMsg);
            logger.error("**********************【returnMsgDecode】:" + returnMsgDecode);
            //第一次分割，分隔符'&'
            String[] stepOne = returnMsgDecode.split("&");
            Map<String, String> reslutMap = new HashMap<String, String>();
            for (int i = 0; i < stepOne.length; i++) {
                //第二次分割，分隔符'='
                String[] stepTwo = stepOne[i].split("=");
                //向HashMap中添加
                reslutMap.put(stepTwo[0], stepTwo[1]);
            }
            logger.error("**********************【reslutMap】:" + reslutMap);
            String returnSign = reslutMap.get("sign");
            //String signtype = reslutMap.get("signtype");
            String transdataStr = reslutMap.get("transdata");
            JSONObject transdata = new JSONObject(transdataStr);
            int code = 0;
            if (returnSign != null) {
                code = 1;
            }
            if (code == 1) {
                result.put("code", "1");
                result.put("msg", returnMsg);
                result.put("userid", transdata.optString("userid"));
                result.put("loginname", transdata.optString("loginname"));
            } else {
                result.put("code", "0");
                result.put("msg", returnMsg);
                result.put("errmsg", transdata.optString("errmsg"));
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify 星空世界 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyXingkongshijiepaySession(XingkongshijiepaySession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            logger.error("**********************【ygAppId或platformId 为空】");
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            int platformId = Integer.valueOf(session.getPlatformId());
            long ygAppId = Long.valueOf(session.getYgAppId());
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(platformId, ygAppId);
            if (platformGame == null) {
                result.put("code", "0");
                result.put("msg", "platorm not unition");
                logger.error("**********************【platorm not unition】");
                return JsonMapper.toJson(result);
            }

            String APPV_KEY = platformGame.getConfigParamsList().get(1);

            String appid = session.getAppid();
            String waresname = session.getWaresname();
            String cporderid = session.getCporderid();
            float price = Float.valueOf(session.getPrice());
            String appuserid = session.getAppuserid();
            String cpprivateinfo = session.getCpprivateinfo();
            String notifyurl = session.getNotifyUrl();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appid", appid);
            jsonObject.put("waresid", 1);
            jsonObject.put("cporderid", cporderid);
            jsonObject.put("currency", "RMB");
            jsonObject.put("appuserid", appuserid);
            //以下是参数列表中的可选参数
            if (!waresname.isEmpty()) {
                jsonObject.put("waresname", waresname);
            }
            //当使用的是 开放价格策略的时候 price的值是 程序自己 设定的价格，使用其他的计费策略的时候,price 不用传值
            jsonObject.put("price", price);
            if (!cpprivateinfo.isEmpty()) {
                jsonObject.put("cpprivateinfo", cpprivateinfo);
            }
            if (!notifyurl.isEmpty()) {
                //如果此处不传同步地址，则是以后台传的为准。
                jsonObject.put("notifyurl", notifyurl);
            }
            String content = jsonObject.toString();// 组装成 json格式数据
            logger.error("JSON：" + content);
            // 调用签名函数
            String sign = SignHelper.sign(content, APPV_KEY);

            String data = "transdata=" + URLEncoder.encode(content) + "&sign=" + URLEncoder.encode(sign) + "&signtype=RSA";// 组装请求参数
            logger.error("APPV_KEY：" + APPV_KEY);
            logger.error("请求数据：" + data);
            String param = data; // 请求验证服务端
            logger.error("响应数据：" + param);
            if (param != null) {
                result.put("code", "1");
                result.put("msg", "订单param生成成功！");
                result.put("param", param);
            } else {
                result.put("code", "0");
                result.put("msg", "订单param生成失败！");
                result.put("param", param);
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify 星空世界 订单请求 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyXingkongshijie(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();

        String transdata = request.getParameter("transdata");
        String sign = (String) request.getParameter("sign");
        String signtype = request.getParameter("signtype");

        String orderId = null;
        float money = 0f;
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(transdata);
            orderId = jsonObject.optString("cporderid");
            money = Float.parseFloat(jsonObject.optString("money"));
            result = jsonObject.optString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Order not fund");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
            return "Order not fund";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());

        String platp_key = platformGame.getConfigParamsList().get(2);
        if (platformGame == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "platform not unition");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
            return "platform not unition";
        }
        if (signtype == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "signType null");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
            return "signType null";
        } else {
            if (SignHelper.verify(transdata, sign, platp_key)) {
                if (order.getAmount() > money * 100) {
                    PlatformStatsLogger.info(PlatformStatsLogger.CHANGQU, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                    returnMap.put("ret", 1);
                    returnMap.put("msg", "money not match");
                    returnMap.put("content", "");
                    PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
                    return "money not match";
                } else {
                    if (result.equals("0")) {
                        orderService.paySuccess(order.getOrderId());
                        returnMap.put("ret", 0);
                        returnMap.put("msg", "SUCCESS");
                        returnMap.put("content", "");
                        PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
                        return "SUCCESS";
                    } else {
                        returnMap.put("ret", 1);
                        returnMap.put("msg", "result:" + result);
                        returnMap.put("content", "");
                        PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
                        return "result:" + result;
                    }
                }
            } else {
                returnMap.put("ret", 1);
                returnMap.put("msg", "verify fail");
                returnMap.put("content", "");
                PlatformStatsLogger.info(PlatformStatsLogger.XINGKONGSHIJIE, JsonMapper.toJson(returnMap));
                return "verify fail";
            }
        }
    }

    @Override
    public String verifyUcPaySign(UcSession ucSession) {
        Map<String, String> result = new HashMap<String, String>();
        if (ucSession.verifySession() != null) {
            logger.error("**********************【ygAppId或platformId 为空】");
            return JsonMapper.toJson(ucSession.verifySession());
        }
        try {
            int platformId = Integer.valueOf(ucSession.getPlatformId());
            long ygAppId = Long.valueOf(ucSession.getYgAppId());
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(platformId, ygAppId);
            if (platformGame == null) {
                result.put("code", "0");
                result.put("msg", "platorm not unition");
                logger.error("**********************【platorm not unition】");
                return JsonMapper.toJson(result);
            }

            String apikey = platformGame.getConfigParamsList().get(0);
            StringBuffer sb = new StringBuffer();
            sb.append("accountId=").append(ucSession.getAccountId());
            sb.append("amount=").append(ucSession.getAmount());
            sb.append("callbackInfo=").append(ucSession.getCallbackInfo());
            sb.append("cpOrderId=").append(ucSession.getCpOrderId());
            sb.append("notifyUrl=").append(ucSession.getNotifyUrl());
            sb.append(apikey);

            String sign = MD5.encode(sb.toString());
            result.put("code", "1");
            result.put("sign", sign);
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("verifyUcPaySign error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyMoguwanSession(MoguwanSession session) {
        Map<String, String> result = new HashMap<String, String>();
        if (session.verifySession() != null) {
            return JsonMapper.toJson(session.verifySession());
        }
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                result.put("code", "1");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", session.getUser_id());
            params.put("token", session.getToken());
            String returnMsg = HttpUtils.doPost(url, params);
            logger.info(returnMsg);
            JSONObject returnJson = new JSONObject(returnMsg);
            int status = returnJson.optInt("status");
            if (status == 1) {
                String user_id = returnJson.optString("user_id");
                String user_account = returnJson.optString("user_account");
                result.put("code", "0");
                result.put("user_id", user_id);
                result.put("user_account", user_account);
            } else if (status == -1) {
                result.put("code", "1");
                String msg = returnJson.optString("msg");
                result.put("msg", msg);
            } else if (status == -2) {
                result.put("code", "1");
                String msg = returnJson.optString("msg");
                result.put("msg", msg);
            } else {
                result.put("code", "1");
                result.put("msg", "登录验证失败");
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify 蘑菇玩 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyMoguwan(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.MOGUWAN, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String out_trade_no = request.getParameter("out_trade_no");
        String price = request.getParameter("price");
        String pay_status = request.getParameter("pay_status");
        String extend = request.getParameter("extend");
        String signType = request.getParameter("signType");
        String sign = request.getParameter("sign");
        Order order = basicRepository.getOrderByOrderId(extend);
        if (order == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }

        String app_secret = platformGame.getConfigParamsList().get(1);

        StringBuilder sb = new StringBuilder();
        sb.append(out_trade_no);
        sb.append(price);
        sb.append(pay_status);
        sb.append(extend);
        sb.append(app_secret);
        String validSigin = MD5.encode(sb.toString());
        logger.info(validSigin);
        if (validSigin.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(price) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.MOGUWAN, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 0);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "0";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 1);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "1";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.MOGUWAN, "签名验证失败 ");
            returnMap.put("ret", 0);
            returnMap.put("msg", "Sigin error");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }
    }

    @Override
    public String verifyM2166(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.M2166, HttpUtils.getRequestParams(request).toString());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String cp_order_id = request.getParameter("cp_order_id");
        String extend_info = request.getParameter("extend_info");
        String game_id = request.getParameter("game_id");
        String pay_amount = request.getParameter("pay_amount");
        String pay_order_number = request.getParameter("pay_order_number");
        String pay_status = request.getParameter("pay_status");
        String pay_time = request.getParameter("pay_time");
        String props_name = request.getParameter("props_name");
        String server_id = request.getParameter("server_id");
        String user_account = request.getParameter("user_account");

        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(cp_order_id);
        if (order == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 0);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }

        String signKey = platformGame.getConfigParamsList().get(0);

        StringBuilder sb = new StringBuilder();
        sb.append("cp_order_id=").append(cp_order_id).append("&");
        sb.append("extend_info=").append(URLEncoder.encode(extend_info)).append("&");
        sb.append("game_id=").append(game_id).append("&");
        sb.append("pay_amount=").append(pay_amount).append("&");
        sb.append("pay_order_number=").append(pay_order_number).append("&");
        sb.append("pay_status=").append(pay_status).append("&");
        sb.append("pay_time=").append(pay_time).append("&");
        sb.append("props_name=").append(URLEncoder.encode(props_name)).append("&");
        sb.append("server_id=").append(server_id).append("&");
        sb.append("user_account=").append(user_account);
        sb.append(signKey);

        String validSign = URLEncoder.encode(Base64.encode(MD5.encode(sb.toString()).toLowerCase()));

        logger.info(validSign);
        if (validSign.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(pay_amount) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.M2166, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 0);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "0";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 1);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "1";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.M2166, "签名验证失败 ");
            returnMap.put("ret", 0);
            returnMap.put("msg", "Sigin error");
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "0";
        }
    }

    @Override
    public String verifySix7Session(Six7Session session) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatform()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                result.put("code", "1");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("apk_id", session.getApk_id());
            params.put("token", session.getToken());
            params.put("device_code", session.getDevice_code());
            params.put("uid", session.getUid());
            String returnMsg = HttpUtils.doPost(url, params);
            logger.info(returnMsg);
            JSONObject returnJson = new JSONObject(returnMsg);
            int status = returnJson.optInt("status");
            if (status == 1) {
                result.put("code", "0");
                result.put("uid", session.getUid());
            } else {
                result.put("code", "1");
                result.put("msg", "登录验证失败");
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify Six7 Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifySix7(HttpServletRequest request) {

        PlatformStatsLogger.info(PlatformStatsLogger.SIX7, HttpUtils.getRequestParams(request).toString());

        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }


        Map<String, Object> returnMap = new HashMap<String, Object>();
        String orderid = request.getParameter("out_order_no");
        String order_status = request.getParameter("order_status");

        String amount = request.getParameter("total_amount");
        Order order = basicRepository.getOrderByOrderId(orderid);
        if (order == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.SIX7, JsonMapper.toJson(returnMap));
            return "fail";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.SIX7, JsonMapper.toJson(returnMap));
            return "fail";
        }

        String pay_key = platformGame.getConfigParamsList().get(1);

        if (order_status.equals("PAY_SUCCESS") && Six7payUtils.verify(params, "UTF-8", pay_key)) {
            if (order.getAmount() > Float.parseFloat(amount) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.SIX7, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "fail";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "success";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.SIX7, "签名验证失败 ");
            returnMap.put("ret", 1);
            returnMap.put("msg", "Sigin error or not pay " + order_status);
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "fail";
        }
    }

    @Override
    public String verifyXmwSession(XmwSession xmwSession) {
        Map<String, String> result = new HashMap<String, String>();


        logger.error(JsonMapper.toJson(xmwSession));

        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(xmwSession.getPlatformId()), Long.valueOf(xmwSession.getYgAppId()));
            if (platformGame == null) {
                result.put("code", "1");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(0);
            Map<String, Object> params = new HashMap<String, Object>(16);
            params.put("client_id", xmwSession.getClient_id());
            params.put("client_secret", xmwSession.getClient_secret());
            params.put("grant_type", xmwSession.getGrant_type());
            if ("authorization_code".equals(xmwSession.getGrant_type())) {
                params.put("code", xmwSession.getCode());
            } else if ("refresh_token".equals(xmwSession.getGrant_type())) {
                params.put("refresh_token", xmwSession.getRefresh_token());
            }

            String returnMsg = HttpUtils.doPost(url, params);
            logger.info(returnMsg);
            JSONObject returnJson = new JSONObject(returnMsg);
            if (returnJson.has("access_token")) {
                result.put("code", "0");
                result.put("access_token", returnJson.getString("access_token"));
            } else {
                result.put("code", "1");
                result.put("msg", "登录验证失败");
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Verify Xmw Session error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常！");
            result.put("exception", e.getMessage());
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyXmw(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.XMW, HttpUtils.getRequestParams(request).toString());

        Map<String, Object> returnMap = new HashMap<String, Object>();

        String orderId = request.getParameter("app_order_id");
        String orderStatus = request.getParameter("status");
        String amount = request.getParameter("amount");
        Order order = basicRepository.getOrderByOrderId(orderId);

        if (order == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.XMW, JsonMapper.toJson(returnMap));
            return "fail";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.XMW, JsonMapper.toJson(returnMap));
            return "fail";
        }
        String client_secret = platformGame.getConfigParamsList().get(3);
        logger.info("client_secret = {}", client_secret);

        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append(request.getParameter("amount")).append("&");
        if (request.getParameter("app_description") != null) {
            sb.append("app_description=").append(request.getParameter("app_description")).append("&");
        }
        if (request.getParameter("app_ext1") != null) {
            sb.append("app_ext1=").append(request.getParameter("app_ext1")).append("&");
        }
        if (request.getParameter("app_ext2") != null) {
            sb.append("app_ext2=").append(request.getParameter("app_ext2")).append("&");
        }
        sb.append("app_order_id=").append(request.getParameter("app_order_id")).append("&");
        if (request.getParameter("app_subject") != null) {
            sb.append("app_subject=").append(request.getParameter("app_subject")).append("&");
        }
        sb.append("app_user_id=").append(request.getParameter("app_user_id")).append("&");
        sb.append("serial=").append(request.getParameter("serial")).append("&");
        sb.append("status=").append(request.getParameter("status")).append("&");
        sb.append("client_secret=").append(client_secret);


        logger.info("签名文本 = {}", sb.toString());
        String validSign = MD5.encode(sb.toString());
        logger.info("签名后md5值validSign = {}", validSign);

        if ("success".equals(orderStatus) && validSign.equals(request.getParameter("sign"))) {
            if (order.getAmount() > Float.parseFloat(amount) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.XMW, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                PlatformStatsLogger.info(PlatformStatsLogger.XMW, JsonMapper.toJson(returnMap));
                return "fail";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                PlatformStatsLogger.info(PlatformStatsLogger.XMW, JsonMapper.toJson(returnMap));
                return "success";
            }

        } else {
            returnMap.put("ret", 1);
            returnMap.put("msg", "not pay or sign error" + orderStatus);
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "fail";
        }


    }

    @Override
    public String createXmwOrder(XmwOrderSession xmwOrderSession) {
        Map<String, Object> result = new HashMap<String, Object>();

        logger.info("createXmwOrder req={}", JsonMapper.toJson(xmwOrderSession));

        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(xmwOrderSession.getPlatformId()), Long.valueOf(xmwOrderSession.getYgAppId()));
            if (platformGame == null) {
                result.put("code", "1");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(1);
            //signParamsByMD5WithKey
            StringBuilder sb = new StringBuilder();
            sb.append("amount=").append(xmwOrderSession.getAmount()).append("&");
            if (xmwOrderSession.getApp_description() != null) {
                sb.append("app_description=").append(URLEncoder.encode(xmwOrderSession.getApp_description())).append("&");
            }
            if (xmwOrderSession.getApp_ext1() != null) {
                sb.append("app_ext1=").append(URLEncoder.encode(xmwOrderSession.getApp_ext1())).append("&");
            }
            if (xmwOrderSession.getApp_ext2() != null) {
                sb.append("app_ext2=").append(URLEncoder.encode(xmwOrderSession.getApp_ext2())).append("&");
            }
            sb.append("app_order_id=").append(xmwOrderSession.getApp_order_id()).append("&");
            sb.append("app_subject=").append(URLEncoder.encode(xmwOrderSession.getApp_subject())).append("&");
            sb.append("app_user_id=").append(xmwOrderSession.getApp_user_id()).append("&");
            sb.append("notify_url=").append(xmwOrderSession.getNotify_url()).append("&");
            sb.append("timestamp=").append(xmwOrderSession.getTimestamp()).append("&");
            sb.append("client_secret=").append(xmwOrderSession.getClient_secret());

            String signStr = sb.toString();
            logger.info("signStr = {}", signStr);
            String validSign = MD5.encode(sb.toString());
            logger.info("sign = {}", validSign);
            Map<String, Object> params = new HashMap<String, Object>(16);
            params.put("access_token", xmwOrderSession.getAccess_token());
            params.put("client_id", xmwOrderSession.getClient_id());
            params.put("client_secret", xmwOrderSession.getClient_secret());
            params.put("app_order_id", xmwOrderSession.getApp_order_id());
            params.put("app_user_id", xmwOrderSession.getApp_user_id());
            params.put("notify_url", xmwOrderSession.getNotify_url());
            params.put("amount", xmwOrderSession.getAmount());
            params.put("timestamp", xmwOrderSession.getTimestamp());
            params.put("app_subject", URLEncoder.encode(xmwOrderSession.getApp_subject(), "utf-8"));
            if (xmwOrderSession.getApp_description() != null) {
                params.put("app_description", URLEncoder.encode(xmwOrderSession.getApp_description()));
            }
            if (xmwOrderSession.getApp_ext1() != null) {
                params.put("app_ext1", URLEncoder.encode(xmwOrderSession.getApp_ext1()));
            }
            if (xmwOrderSession.getApp_ext2() != null) {
                params.put("app_ext2", URLEncoder.encode(xmwOrderSession.getApp_ext2()));
            }
            params.put("game_detail", URLEncoder.encode(xmwOrderSession.getGame_detail()));
            params.put("sign", validSign);
            logger.info("new req params={}", JsonMapper.toJson(params));

            logger.info("url = {}", url);
            String returnMsg = HttpUtils.doPostToJson(url, JsonMapper.toJson(params), 3000);
            logger.info("返回消息 = {}", returnMsg);

            JSONObject returnJson = new JSONObject(returnMsg);
            if (returnJson.has("serial")) {
                result.put("result", returnJson.toString());
                result.put("code", "0");
                result.put("msg", "生成订单成功");
            } else {
                result.put("code", "1");
                result.put("msg", "生成订单失败");
                result.put("param", params);
                result.put("result", returnMsg);
            }
            return JsonMapper.toJson(result);
        } catch (Exception e) {
            logger.error("Create Xmw order error", e);
            result.put("status", "2");
            result.put("msg", "服务器异常");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyWuKong(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, HttpUtils.getRequestParams(request).toString());

        Map<String, Object> map = new HashMap<String, Object>();
        String openId = request.getParameter("openId");
        String serverId = request.getParameter("serverId");
        String serverName = request.getParameter("serverName");
        String roleId = request.getParameter("roleId");
        String roleName = request.getParameter("roleName");
        String orderId = request.getParameter("orderId");
        String orderStatus = request.getParameter("orderStatus");
        String payType = request.getParameter("payType");
        String amount = request.getParameter("amount");
        String remark = request.getParameter("remark");
        String callBackInfo = request.getParameter("callBackInfo");
        String payTime = request.getParameter("payTime");
        String paySUTime = request.getParameter("paySUTime");
        String sign = request.getParameter("sign");

        Order order = basicRepository.getOrderByOrderId(callBackInfo);
        logger.debug("order= {}", order);
        if (order == null) {
            map.put("code", 1);
            map.put("msg", "notfind order");
            PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, JsonMapper.toJson(map));
            return "error";
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            map.put("code", 1);
            map.put("msg", "notfind platformGame");
            PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, JsonMapper.toJson(map));
            return "error";
        }
        String appkey = platformGame.getConfigParamsList().get(0);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("openId", openId);
        params.put("serverId", serverId);
        params.put("serverName", serverName);
        params.put("roleId", roleId);
        params.put("roleName", roleName);
        params.put("orderId", orderId);
        params.put("orderStatus", orderStatus);
        params.put("payType", payType);
        params.put("amount", amount);
        params.put("remark", remark);
        params.put("callBackInfo", callBackInfo);
        params.put("payTime", payTime);
        params.put("paySUTime", paySUTime);

        PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, params.toString() + " sign: " + sign);

        /** 生成签名 */
        String validSign = null;
        try {
            validSign = Sign.signByMD5KeySortValNoNull(params, appkey);
        } catch (UnsupportedEncodingException e) {
            logger.debug("sign error");
            e.printStackTrace();
        }
        PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, " validSign: " + validSign);
        if (StringUtils.equals(validSign, sign)) {
            logger.debug("verify wukong valid sign success");
            if (order.getAmount() > Integer.valueOf(amount) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                map.put("code", 1);
                map.put("msg", "Amount error");
                return "error";
            } else {
                orderService.paySuccess(order.getOrderId());
                map.put("code", 0);
                map.put("msg", "sing success");
                PlatformStatsLogger.info(PlatformStatsLogger.WUKONG, JsonMapper.toJson(map));
                return "success";
            }
        } else {
            logger.debug("verify wukong valid sign failed");
            return "errorSign";
        }
    }

    @Override
    public String verifyDlSession(DlSession dlSession) {
        PlatformStatsLogger.info(PlatformStatsLogger.DL, dlSession.toString());
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        int appid = dlSession.getAppid();
        String umid = dlSession.getUmid();
        String token = dlSession.getToken();
        PlatformGame platformGame = basicRepository.
                getByPlatformAndGameId(Integer.valueOf(dlSession.getPlatformId()), Long.valueOf(dlSession.getYgAppId()));
        if (platformGame == null) {
            logger.debug("error not find platformGame");
            map.put("valid", "2");
            map.put("roll", true);
            map.put("interval", 60);
            map.put("times", 1);
            map.put("msg_code", 6011);
            map.put("msg_desc", "platformGame not find");
            return JsonUtil.toJson(map);
        }

        String appkey = platformGame.getConfigParamsList().get(0);
        StringBuilder sbUrl = new StringBuilder();
        if (token.startsWith("ZB_")) {
            sbUrl.append(platformGame.getConfigParamsList().get(1));
        } else {
            sbUrl.append(platformGame.getConfigParamsList().get(2));
        }
        sbUrl.append("?appid=").append(appid);
        sbUrl.append("&umid=").append(umid);
        sbUrl.append("&token=").append(token);
        String sigStr = appid + "|" + appkey + "|" + token + "|" + umid;
        String sig = MD5Util.MD5Encode(sigStr, "UTF-8");
        sbUrl.append("&sig=").append(sig);

        PlatformStatsLogger.info(PlatformStatsLogger.DL, sbUrl.toString() + " sig: " + sig);

        try {
            String result = HttpUtils.get(sbUrl.toString());
            logger.info("verifyDlSession Result: {}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("doGet error");
            map.put("valid", "2");
            map.put("roll", true);
            map.put("interval", 60);
            map.put("times", 1);
            map.put("msg_code", 2002);
            map.put("msg_desc", "失败");
            return JsonUtil.toJson(map);
        }
    }

    @Override
    public String createDlPaySign(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.DL, HttpUtils.getRequestParams(request).toString());

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        String cpOrder = request.getParameter("cpOrder");
        String ext = request.getParameter("ext");
        String money = request.getParameter("money");
        String roleId = request.getParameter("roleId");
        String umid = request.getParameter("umid");

        Order order = basicRepository.getOrderByOrderId(cpOrder);
        logger.debug("order= {}", order);
        if (order == null) {
            map.put("code", 1);
            map.put("msg", "error order not find");
            return JsonUtil.toJson(map);
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());

        if (platformGame == null) {
            map.put("code", 1);
            map.put("msg", "error platformGame not find");
            return JsonUtil.toJson(map);
        }
        String paymentKey = platformGame.getConfigParamsList().get(3);
        String moneyTow = new BigDecimal(money).setScale(2, RoundingMode.DOWN).toString();

        /** 生成签名*/
        StringBuilder sb = new StringBuilder();
        sb.append(cpOrder == null ? "" : cpOrder).append("|");
        sb.append(ext == null ? "" : ext).append("|");
        sb.append(moneyTow == null ? "" : moneyTow).append("|");
        sb.append(roleId == null ? "" : roleId).append("|");
        sb.append(umid == null ? "" : umid).append("|");
        sb.append(paymentKey);
        logger.info("cpSignSb: {}", sb.toString());
        String cpSign = MD5Util.MD5Encode(sb.toString(), "UTF-8");

        PlatformStatsLogger.info(PlatformStatsLogger.DL, "cpSign:" + cpSign);

        map.put("code", 0);
        map.put("msg", "成功");
        map.put("cpSign", cpSign);
        return JsonUtil.toJson(map);
    }

    @Override
    public String verifyDl(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.DL, HttpUtils.getRequestParams(request).toString());

        String result = request.getParameter("result");
        String cpOrder = request.getParameter("cpOrder");
        String money = request.getParameter("money");
        String ext = request.getParameter("ext") == null ? "" : request.getParameter("ext");
        String signature = request.getParameter("signature");

        Order order = basicRepository.getOrderByOrderId(cpOrder);
        logger.info("order {}", order);
        if (order == null) {
            return "error order not find";
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());

        if (platformGame == null) {
            return "error platformGame not find";
        }
        String paymentKey = platformGame.getConfigParamsList().get(3);

        StringBuilder sb = new StringBuilder();
        sb.append("order=").append(request.getParameter("order"));
        sb.append("&money=").append(money);
        sb.append("&mid=").append(request.getParameter("mid"));
        sb.append("&time=").append(request.getParameter("time"));
        sb.append("&result=").append(result);
        sb.append("&cpOrder=").append(cpOrder);
        sb.append("&ext=").append(ext);
        sb.append("&key=").append(paymentKey);
        logger.info("signSb: {} ,orderAmount {}", sb.toString(), order.getAmount());
        String sig = MD5Util.MD5Encode(sb.toString(), "UTF-8");

        PlatformStatsLogger.info(PlatformStatsLogger.DL, "signature:" + signature + ",sig:" + sig);
        try {
            if (sig.equalsIgnoreCase(signature)) { // 验证通过
                if ("1".equals(result)) {
                    if (order.getAmount() > Float.valueOf(money) * 100) {
                        orderService.payFail(order.getOrderId(), "order amount error");
                        PlatformStatsLogger.error(PlatformStatsLogger.DL, order.getOrderId(), "order amount error");
                    } else {
                        logger.info("verifyDl success");
                        orderService.paySuccess(order.getOrderId());
                    }
                } else {
                    orderService.payFail(order.getOrderId(), "支付结果失败,订单支付失败");
                }
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            return "failure";
        }
    }

    @Override
    public String verifyJianguoSession(JianguoSession session) {
        Map<String, String> result = new HashMap<String, String>();
        logger.info("坚果session req = {}", JsonMapper.toJson(session));

        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                result.put("status", "0");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(0);
            String app_key = platformGame.getConfigParamsList().get(1);
            String app_id = session.getApp_id();
            String mem_id = session.getMem_id();
            String user_token = session.getUser_token();

            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(app_id).append("&");
            sb.append("mem_id=").append(mem_id).append("&");
            sb.append("user_token=").append(user_token).append("&");
            sb.append("app_key=").append(app_key);

            String validSign = MD5.encode(sb.toString());

            Map<String, Object> params = new LinkedHashMap<String, Object>(16);
            params.put("app_id", app_id);
            params.put("mem_id", mem_id);
            params.put("user_token", user_token);
            params.put("sign", validSign);

            String returnMsg = HttpUtils.doPost(url, params);

            logger.info("坚果session 请求返回结果 = {}", returnMsg);

            JSONObject returnJson = new JSONObject(returnMsg);

            if ("1".equals(returnJson.get("status"))) {
                result.put("code", "0");
                result.put("msg", "校验成功！");
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "1");
                result.put("msg", returnJson.get("msg").toString());
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("Verify Jianguo Session error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常！");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyJianguo(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.JG, HttpUtils.getRequestParams(request).toString());
        String app_id = request.getParameter("app_id");
        String cp_order_id = request.getParameter("cp_order_id");
        String mem_id = request.getParameter("mem_id");
        String order_id = request.getParameter("order_id");
        String order_status = request.getParameter("order_status");
        String pay_time = request.getParameter("pay_time");
        String product_id = request.getParameter("product_id");
        String product_name = request.getParameter("product_name");
        String product_price = request.getParameter("product_price");
        String sign = request.getParameter("sign");

        Map<String, Object> returnMap = new HashMap<String, Object>();
        Order order = basicRepository.getOrderByOrderId(cp_order_id);
        if (order == null) {
            logger.error("坚果支付回调订单不存在:{}", cp_order_id);
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.JG, JsonMapper.toJson(returnMap));
            return "fail";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            logger.error("坚果支付回调platformGame不存在");
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.JG, JsonMapper.toJson(returnMap));
            return "fail";
        }

        String app_key = platformGame.getConfigParamsList().get(1);

        Map<String, Object> signParams = new LinkedHashMap<String, Object>();
        signParams.put("app_id", app_id);
        signParams.put("cp_order_id", cp_order_id);
        signParams.put("mem_id", mem_id);
        signParams.put("order_id", order_id);
        signParams.put("order_status", order_status);
        signParams.put("pay_time", pay_time);
        signParams.put("product_id", product_id);
        signParams.put("product_name", URLEncoder.encode(product_name));
        signParams.put("product_price", product_price);
        signParams.put("app_key", app_key);

        String validSign = Sign.signByMD5Unsort(signParams, "");

        if ("2".equals(order_status) && validSign.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(product_price) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.JG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "FAILURE";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "SUCCESS";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.JG, "签名验证失败 ");
            returnMap.put("ret", 1);
            returnMap.put("msg", "Sigin error or not pay " + order_status);
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "FAILURE";
        }
    }


    @Override
    public String verifyBinghuSession(BinghuSession session) {
        Map<String, String> result = new HashMap<String, String>();
        logger.info(JsonMapper.toJson(session));

        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                result.put("status", "0");
                result.put("msg", "platorm not unition");
                return JsonMapper.toJson(result);
            }
            String url = platformGame.getConfigParamsList().get(0);
            String app_key = platformGame.getConfigParamsList().get(1);
            String app_id = session.getApp_id();
            String mem_id = session.getMem_id();
            String user_token = session.getUser_token();

            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(app_id).append("&");
            sb.append("mem_id=").append(mem_id).append("&");
            sb.append("user_token=").append(user_token).append("&");
            sb.append("app_key=").append(app_key);

            String validSign = MD5.encode(sb.toString());

            Map<String, Object> params = new LinkedHashMap<String, Object>(16);
            params.put("app_id", app_id);
            params.put("mem_id", mem_id);
            params.put("user_token", user_token);
            params.put("sign", validSign);

            String returnMsg = HttpUtils.doPost(url, params);
            JSONObject returnJson = new JSONObject(returnMsg);

            if ("1".equals(returnJson.get("status"))) {
                result.put("code", "0");
                result.put("msg", "校验成功！");
                return JsonMapper.toJson(result);
            } else {
                result.put("code", "1");
                result.put("msg", returnJson.get("msg").toString());
                return JsonMapper.toJson(result);
            }
        } catch (Exception e) {
            logger.error("Verify Jianguo Session error", e);
            result.put("code", "1");
            result.put("msg", "服务器异常！");
            return JsonMapper.toJson(result);
        }
    }

    @Override
    public String verifyBinghu(HttpServletRequest request) {

        PlatformStatsLogger.info(PlatformStatsLogger.BH, HttpUtils.getRequestParams(request).toString());
        String app_id = request.getParameter("app_id");
        String cp_order_id = request.getParameter("cp_order_id");
        String mem_id = request.getParameter("mem_id");
        String order_id = request.getParameter("order_id");
        String order_status = request.getParameter("order_status");
        String pay_time = request.getParameter("pay_time");
        String product_id = request.getParameter("product_id");
        String product_name = request.getParameter("product_name");
        String product_price = request.getParameter("product_price");
        String sign = request.getParameter("sign");

        Map<String, Object> returnMap = new HashMap<String, Object>();
        Order order = basicRepository.getOrderByOrderId(cp_order_id);
        if (order == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Order");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.BH, JsonMapper.toJson(returnMap));
            return "fail";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            returnMap.put("ret", 1);
            returnMap.put("msg", "Can find Game");
            returnMap.put("content", "");
            PlatformStatsLogger.info(PlatformStatsLogger.BH, JsonMapper.toJson(returnMap));
            return "fail";
        }

        String app_key = platformGame.getConfigParamsList().get(1);

        Map<String, Object> signParams = new LinkedHashMap<String, Object>();
        signParams.put("app_id", app_id);
        signParams.put("cp_order_id", cp_order_id);
        signParams.put("mem_id", mem_id);
        signParams.put("order_id", order_id);
        signParams.put("order_status", order_status);
        signParams.put("pay_time", pay_time);
        signParams.put("product_id", product_id);
        signParams.put("product_name", URLEncoder.encode(product_name));
        signParams.put("product_price", product_price);
        signParams.put("app_key", app_key);

        String validSign = Sign.signByMD5Unsort(signParams, "");

        logger.info("validSign = {}", validSign);
        logger.info("sign = {}", sign);
        logger.info("order_status = {}", order_status);

        if ("2".equals(order_status) && validSign.equals(sign)) {
            if (order.getAmount() > Float.parseFloat(product_price) * 100) {
                PlatformStatsLogger.info(PlatformStatsLogger.JG, "order amount error");
                orderService.payFail(order.getOrderId(), "order amount error");
                returnMap.put("ret", 1);
                returnMap.put("msg", "Amount error");
                returnMap.put("content", "");
                return "FAILURE";
            } else {
                orderService.paySuccess(order.getOrderId());
                returnMap.put("ret", 0);
                returnMap.put("msg", "success");
                returnMap.put("content", "");
                return "SUCCESS";
            }
        } else {
            PlatformStatsLogger.info(PlatformStatsLogger.JG, "签名验证失败 ");
            returnMap.put("ret", 1);
            returnMap.put("msg", "Sigin error or not pay " + order_status);
            returnMap.put("content", "");
            logger.info(JsonMapper.toJson(returnMap));
            return "FAILURE";
        }
    }

    @Override
    public String verifyDxSession(DianxinSession session) {
        PlatformStatsLogger.info(PlatformStatsLogger.DX, session.toString());
        if (StringUtils.isBlank(session.getYgAppId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getCode())) {
            return "param delivery error";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
        if (platformGame == null) {
            return "platformGame not find";
        }
        //==========================
        String client_id = platformGame.getConfigParamsList().get(0);
        String client_secret = platformGame.getConfigParamsList().get(1);
        String verifySessionUrl = platformGame.getConfigParamsList().get(2);
        //==========================
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", session.getCode());
        params.put("client_secret", client_secret);
        try {
            // 进行数字签名，并把签名相关字段放入请求参数MAP
            RequestParasUtil.signature("2", client_id, client_secret, "MD5", "v3.0.4", params);
            String sign_sort = params.get("sign_sort");
            params.put("sign_sort", URLEncoder.encode(sign_sort));
            // 发起请求
            String result = RequestParasUtil.sendPostRequest(verifySessionUrl, params);
            logger.info("verifyDxSession result : " + result);
            return result;
        } catch (Exception e) {
            logger.error("verifyDxSession error", e);
            return "";
        }
    }

    @Override
    public String verifyDxToken(DianxinSession session) {
        PlatformStatsLogger.info(PlatformStatsLogger.DX, session.toString());
        if (StringUtils.isBlank(session.getYgAppId()) || StringUtils.isBlank(session.getPlatformId())) {
            return "param delivery error";
        }
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
        if (platformGame == null) {
            return "platformGame not find";
        }
        //==========================
        String client_id = platformGame.getConfigParamsList().get(0);
        String client_secret = platformGame.getConfigParamsList().get(1);
        String verifyTokenUrl = platformGame.getConfigParamsList().get(3);
        //==========================
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", session.getAccessToken());
        try {
            // 进行数字签名，并把签名相关字段放入请求参数MAP
            RequestParasUtil.signature("2", client_id, client_secret, "MD5", "v3.0.4", params);
            String sign_sort = params.get("sign_sort");
            params.put("sign_sort", URLEncoder.encode(sign_sort));
            // 发起请求
            String result = RequestParasUtil.sendPostRequest(verifyTokenUrl, params);
            logger.info("verifyDxToken result : " + result);
            return result;
        } catch (Exception e) {
            logger.error("verifyDxToken error", e);
            return "";
        }
    }

    @Override
    public String verifyUuSyzhuSession(UuSyzhuSession session) {
        PlatformStatsLogger.info(PlatformStatsLogger.UUSYZ, session.toString());
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "platformGame not find";
            }
            String serverMD5Key = platformGame.getConfigParamsList().get(0);
            String verifySessionUrl = platformGame.getConfigParamsList().get(1);
            String appid = session.getAppid();
            String token = session.getToken();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmssSSS");
            String qtime = sdf.format(new Date());
            String sign = MD5Util.sign(appid + qtime + serverMD5Key).toUpperCase();

            StringBuilder sbData = new StringBuilder();
            sbData.append("appid=").append(appid);
            sbData.append("&token=").append(token);
            sbData.append("&qtime=").append(qtime);
            sbData.append("&sign=").append(sign);

            String result = HttpUtils.post(verifySessionUrl, sbData.toString());
            logger.info("verifyUuSyzhuSession result: {}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "verifyUuSyzhuSession error";
    }

    @Override
    public String verifyUuSyzhu(HttpServletRequest request) {
        PlatformStatsLogger.info(PlatformStatsLogger.UUSYZ, HttpUtils.getRequestParams(request).toString());
        String cporderno = request.getParameter("cporderno");
        Order order = basicRepository.getOrderByOrderId(cporderno);
        if (order == null) {
            logger.info("Order not find");
            return "failure";
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
        if (platformGame == null) {
            logger.info("PlatformGame not find");
            return "failure";
        }
        String serverMD5Key = platformGame.getConfigParamsList().get(0);

        String money = request.getParameter("money");
        String status = request.getParameter("status");
        String qtime = request.getParameter("qtime");
        String sign = request.getParameter("sign");
        String verifySign = MD5Util.sign(cporderno + money + status + qtime + serverMD5Key).toUpperCase();
        logger.info("sign:{} verifySign:{}", sign, verifySign);

        if (sign.equalsIgnoreCase(verifySign)) { // 验证通过
            if ("1".equals(status)) {
                logger.info("orderAmount:{} money:{}", order.getAmount(), Float.valueOf(money));
                if (order.getAmount() > Float.valueOf(money) * 100) {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    PlatformStatsLogger.error(PlatformStatsLogger.UUSYZ, order.getOrderId(), "order amount error");
                    return "failure";
                } else {
                    logger.info("verifyUuSyzhu success");
                    orderService.paySuccess(order.getOrderId());
                    return "success";
                }
            } else {
                orderService.payFail(order.getOrderId(), "支付结果失败,订单支付失败");
                return "failure";
            }
        } else {
            return "failure";
        }
    }

    @Override
    public String verifyGuangFanSession(GuangFanSession session) {
        PlatformStatsLogger.info(PlatformStatsLogger.GF, session.toString());
        try {

            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "platformGame not find";
            }
            String appKey = platformGame.getConfigParamsList().get(0);
            String verifySessionUrl = platformGame.getConfigParamsList().get(1);
            String token = session.getToken();
            String userID = session.getUserID();

            String sign = MD5Util.MD5Encode("userID=" + userID + "token=" + token + appKey, "UTF-8");
            StringBuilder sbData = new StringBuilder();
            sbData.append("userID=").append(userID)
                    .append("&token=").append(token)
                    .append("&sign=").append(sign);

            String result = HttpUtils.post(verifySessionUrl, sbData.toString());
            logger.info("verifyGuangFanSession result: {}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "verifyGuangFanSession error";
        }
    }

    @Override
    public String createGuangFanOrderId(GuangFanSession session) {
        PlatformStatsLogger.info(PlatformStatsLogger.GF, session.toString());
        try {
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getYgAppId()));
            if (platformGame == null) {
                return "platformGame not find";
            }
            String appSecret = platformGame.getConfigParamsList().get(2);
            String orderIdUrl = platformGame.getConfigParamsList().get(3);
            /** 加密源串拼接*/
            String productName = new String(session.getProductName().getBytes("ISO-8859-1"), "UTF-8");
//            String productName = URLDecoder.decode(session.getProductName(), "UTF-8");
            StringBuilder sb = new StringBuilder();
            sb.append("userID=").append(session.getUserID())
                    .append("&productID=").append(session.getProductID())
                    .append("&productName=").append(productName)
                    .append("&productDesc=").append(session.getProductDesc())
                    .append("&money=").append(session.getMoney())
                    .append("&roleID=").append(session.getRoleID())
                    .append("&roleName=").append(session.getRoleName())
                    .append("&serverID=").append(session.getServerID())
                    .append("&serverName=").append(session.getServerName())
                    .append("&extension=").append(session.getExtension());
            String notifyUrl = session.getNotifyUrl();
            if (null != notifyUrl && !notifyUrl.trim().equals("")) {
                sb.append("&notifyUrl=").append(notifyUrl);
            }
            sb.append(appSecret);
            logger.info("sbSign: {}", sb.toString());
            /** URLEncode拼接的源字符串,MD5加密*/
            String encodeData = URLEncoder.encode(sb.toString(), "UTF-8");
            String sign = MD5Util.MD5Encode(encodeData, "UTF-8");
            /** 封装请求参数*/
            StringBuilder sbUrlData = new StringBuilder();
            sbUrlData.append("userID=").append(session.getUserID())
                    .append("&productID=").append(session.getProductID())
                    .append("&productName=").append(URLEncoder.encode(productName, "UTF-8"))
                    .append("&productDesc=").append(session.getProductDesc())
                    .append("&money=").append(session.getMoney())
                    .append("&roleID=").append(session.getRoleID())
                    .append("&roleName=").append(session.getRoleName())
                    .append("&serverID=").append(session.getServerID())
                    .append("&serverName=").append(session.getServerName())
                    .append("&extension=").append(session.getExtension())
                    .append("&signType=").append("md5")
                    .append("&sign=").append(sign);
            String result = HttpUtils.post(orderIdUrl, sbUrlData.toString());
            logger.info("createGuangFanOrderId result: {}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception error";
        }
    }

    @Override
    public String verifyGuangFan(HttpServletRequest request) {
        try {
            BufferedReader br = request.getReader();
            String line;
            StringBuilder sbr = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sbr.append(line).append("\r\n");
            }

            logger.info("U8Server Pay Callback response params:" + sbr.toString());
            Map map = JsonUtil.toObject(sbr.toString(), Map.class);

            if (StringUtil.isNullOrEmpty(map.get("state").toString()) ||
                    StringUtil.isNullOrEmpty(map.get("data").toString())) {
                logger.info("verifyGuangFan param error");
                return "FAIL";
            }

            String state = map.get("state").toString();
            if (!state.equals("1")) {
                logger.info("verifyGuangFan state fail");
                return "FAIL";
            }
            Object dataObj = map.get("data");
            String dataJson = JsonUtil.toJson(dataObj);
            Map dataMap = JsonUtil.toObject(dataJson, Map.class);
            logger.info("verifyGuangFan dataJson: {}", dataJson);
            String cpOrderId = dataMap.get("extension").toString();
            Order order = basicRepository.getOrderByOrderId(cpOrderId);
            if (order == null) {
                logger.info("Order not find");
                return "FAIL";
            }
            PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getChannelId(), order.getGameId());
            if (platformGame == null) {
                logger.info("PlatformGame not find");
                return "FAIL";
            }
            String appSecret = platformGame.getConfigParamsList().get(2);
            String publicKey = platformGame.getConfigParamsList().get(5);
            /** 拼接生成签名字符串,生成签名*/
            StringBuilder sb = new StringBuilder();
            sb.append("channelID=").append(dataMap.get("channelID").toString())
                    .append("&currency=").append(dataMap.get("currency").toString())
                    .append("&extension=").append(dataMap.get("extension").toString())
                    .append("&gameID=").append(dataMap.get("gameID").toString())
                    .append("&money=").append(dataMap.get("money").toString())
                    .append("&orderID=").append(dataMap.get("orderID").toString())
                    .append("&productID=").append(dataMap.get("productID").toString())
                    .append("&serverID=").append(dataMap.get("serverID").toString())
                    .append("&userID=").append(dataMap.get("userID").toString())
                    .append("&").append(appSecret);
            String sign = dataMap.get("sign").toString();

            /** 验证回调*/
            boolean flag = RSAUtils.verify(sb.toString(), sign, publicKey, "UTF-8", "SHA1withRSA");
            if (flag) {
                logger.info("amount: {} money: {}", order.getAmount(), dataMap.get("money").toString());
                if (order.getAmount() > Float.parseFloat(dataMap.get("money").toString())) {
                    orderService.payFail(order.getOrderId(), "order amount error");
                    return "FAIL";
                } else {
                    orderService.paySuccess(order.getOrderId());
                    return "SUCCESS";
                }
            } else {
                logger.info("verify sign fail");
                return "FAIL";
            }
        } catch (Exception e) {
            return "FAIL";
        }
    }


}
