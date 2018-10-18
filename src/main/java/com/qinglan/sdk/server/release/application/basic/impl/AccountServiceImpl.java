package com.qinglan.sdk.server.release.application.basic.impl;

import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.release.Constants;
import com.qinglan.sdk.server.release.application.basic.AccountService;
import com.qinglan.sdk.server.release.application.basic.OrderService;
import com.qinglan.sdk.server.release.domain.basic.*;
import com.qinglan.sdk.server.release.domain.basic.event.*;
import com.qinglan.sdk.server.release.presentation.basic.dto.*;
import com.zhidian3g.ddd.infrastructure.event.EventPublisher;
import com.qinglan.sdk.server.release.application.basic.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    private final static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Resource
    private BasicRepository basicRepository;
    @Resource
    private OrderService orderService;
    @Resource
    private EventPublisher publisher;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public Map<String, Object> initial(InitialPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndAppId(params.getPlatformId(), params.getAppId());
        if (platformGame.getRegistStatus().equals(1)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_STOP_REGIST);
            return result;
        }
        publisher.publish(new InitialEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> login(LoginPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }

        publisher.publish(new LoginEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        result.put("loginTime", System.currentTimeMillis());

        Account account = basicRepository.getRoleCreateTime(params.getAppId(), params.getPlatformId(), params.getZoneId(), params.getRoleId(), params.getRoleName());
        if (account != null && account.getCreateTime() != null) {
            result.put("createTime", account.getCreateTime().getTime());
        } else {
            result.put("createTime", 0);
        }
        return result;
    }

    @Override
    public Map<String, Object> heartbeat(HeartbeatPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }

        publisher.publish(new HeartbeatEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> logout(LogoutPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }

        publisher.publish(new LogoutEvent(params));
        //listener.handleLogoutEvent(new LogoutEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> quit(QuitPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }
        publisher.publish(new QuitEvent(params));
        //listener.handleQuitEvent(new QuitEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> roleEstablish(RoleEstablishPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }
        Date creatTime = new Date();
        params.setCreatTime(creatTime);

        publisher.publish(new RoleEstablishEvent(params));
        //listener.handleRoleEstablishEvent(new RoleEstablishEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        result.put("createTime", creatTime.getTime());
        return result;
    }

    /**
     * @param money
     * @return 0 ：余额不足 2：扣款失败  1：成功
     */
    public boolean checkPlatformBalance(int money, PlatformGame platformGame) {
        Platform platform = basicRepository.getPlatform(platformGame.getPlatformId());
        money = money * platformGame.getDiscount() / 100;
        if (money > platform.getBalance()) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> selforderGenerate(OrderGeneratePattern params) {
        String orderId = orderService.saveOrder(params);
        Map<String, Object> result = new HashMap<String, Object>();

        if (params.getPayType() == 2) {
            logger.info("微信支付开始下单" + JsonMapper.toJson(params));
        } else {
            logger.info("支付宝支付开始下单" + JsonMapper.toJson(params));
        }
        String version = "1";
        String pay_type = "22";
        result.put("payType", "22");
        result.put("agentId", HeepayTradeConfig.getInstance().getHeepayAgentid());
        if (params.getPayType() == 2) {
            pay_type = "30";
            result.put("payType", "30");
        }
        String agent_id = HeepayTradeConfig.getInstance().getHeepayAgentid();
        String agent_bill_id = orderId;
        String pay_amt = new DecimalFormat("0.00").format(params.getAmount() / 100f);
        String notify_url = HeepayTradeConfig.getInstance().getCallbackUrl();
        String user_ip = params.getIp();
        String agent_bill_time = DateUtils.format(new Date(), "yyyyMMddHHmmss");
        String goods_name = params.getCpOrderId();
        String goods_num = "1";
        String remark = params.getCpOrderId();
        String goods_note = params.getCpOrderId();

        Map<String, Object> postparams = new LinkedHashMap<String, Object>();
        postparams.put("version", version);
        postparams.put("agent_id", agent_id);
        postparams.put("agent_bill_id", agent_bill_id);
        postparams.put("agent_bill_time", agent_bill_time);
        postparams.put("pay_type", pay_type);
        postparams.put("pay_amt", pay_amt);
        postparams.put("notify_url", notify_url);

        postparams.put("user_ip", user_ip.replace(".", "_"));
        postparams.put("key", HeepayTradeConfig.getInstance().getWechatPayKey());
        postparams.put("sign", Sign.signByMD5Unsort(postparams, "").toLowerCase());

        postparams.remove("key");
        postparams.put("return_url", notify_url);
        postparams.put("goods_name", goods_name);
        postparams.put("goods_num", goods_num);
        postparams.put("remark", remark);
        postparams.put("goods_note", goods_note);
        postparams.put("meta_option", build_meta_option(params.getAppName(), params.getPackageName()));
        logger.debug("postparams: " + postparams);
        String retStr = "";
        try {
            retStr = HttpUtils.post(HeepayTradeConfig.getInstance().getInitUrl(), postparams);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("postToHttps error：", e);
        }

        logger.debug("汇付宝 pay retStr : " + retStr);
        String token_id = Tools.getXMLValue(retStr, "token_id");
        if (StringUtils.isNotBlank(token_id)) {
            result.put("tokenId", token_id);
            result.put("orderId", orderId);
            result.put("error", "");

            return result;
        }
        result.put("tokenId", "");
        result.put("orderId", orderId);
        result.put("error", Tools.getXMLValue(retStr, "error"));
        return result;
    }


    @Override
    public String payNotify(HttpServletRequest request) {
        String result = request.getParameter("result");
        String pay_message = request.getParameter("pay_message");
        String agent_id = request.getParameter("agent_id");
        String jnet_bill_no = request.getParameter("jnet_bill_no");
        String agent_bill_id = request.getParameter("agent_bill_id");
        String pay_type = request.getParameter("pay_type");
        String pay_amt = request.getParameter("pay_amt");
        String remark = request.getParameter("remark");
        String sign = request.getParameter("sign");

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("result", result);
        params.put("agent_id", agent_id);
        params.put("jnet_bill_no", jnet_bill_no);
        params.put("agent_bill_id", agent_bill_id);
        params.put("pay_type", pay_type);
        params.put("pay_amt", pay_amt);
        params.put("remark", remark);
        params.put("key", HeepayTradeConfig.getInstance().getWechatPayKey());

        String validSign = Sign.signByMD5Unsort(params, "").toLowerCase();

        Order order = orderService.getOrderByOrderId(agent_bill_id);

        if (StringUtils.equals(sign, validSign)) {
            logger.info("Heepay pay notify valid sign success");

            if (null == order) {
                return "error";
            }

            if ("0".equals(result)) {
                return "ok";
            } else if ("-1".equals(result)) {
                orderService.payFail(order.getOrderId(), "回调返回该订单支付失败");
                return "ok";
            }
            if ("1".equals(result)) {
                if (order.getAmount().doubleValue() > Double.valueOf(pay_amt) * 100) {
                    orderService.payFail(agent_bill_id, "实际充值金额与订单金额不一致");
                    return "error";
                }
                orderService.paySuccess(order.getOrderId());
                return "ok";
            }
            return "ok";
        } else {
            logger.info("Heep pay notify valid sign failed data{}, sign, valid sign " + params.toString() + ", " + sign + ", " + validSign);
            return "error";
        }

    }


    private String build_meta_option(String appName, String packageName) {
        Map<String, String> androidMap = new HashMap<String, String>();
        androidMap.put("s", "Android");
        androidMap.put("n", appName);
        androidMap.put("id", packageName);
        Map<String, String> iosMap = new HashMap<String, String>();
        iosMap.put("s", "IOS");
        iosMap.put("n", "");
        iosMap.put("id", "");
        List<Map> list = new ArrayList<Map>();
        list.add(androidMap);
        list.add(iosMap);
        String json = JsonMapper.toJson(list);
        try {
            String base64 = Base64.encode(json.getBytes("gb2312"));
            return base64;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Map<String, Object> orderGenerate(OrderGeneratePattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            return result;
        }
        //联运渠道是否正常
        PlatformGame platformGame = basicRepository.getByPlatformAndAppId(params.getPlatformId(), params.getAppId());
        if (platformGame.getStatus().equals(1)) {
            result.put(Constants.RESPONSE_CODE, Constants.CHANEL_SELF_PAY);
            return result;
        }


        if (!this.checkPlatformBalance(params.getAmount(), platformGame)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_BLANCEERROR);
            return result;
        }

        String orderId = orderService.saveOrder(params);
        if (StringUtils.isEmpty(orderId)) {
            logger.warn("create order failed.");
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION);
            return result;
        }

        Platform platform = basicRepository.getPlatform(params.getPlatformId());
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        result.put("orderId", orderId);
        result.put("notifyUrl", platform.getPlatformCallbackUrl());

        //如果是07073、乐嗨嗨平台则返回加密后的订单号
        if (params.getPlatformId() == Constants.LESHAN_PLATFORM_ID || params.getPlatformId() == Constants.LEHIHI_PLATFORM_ID) {
            result.put("orderId", DES.encryptAndBase64(orderId, Constants.BASE64_ORDERID_KEY));
        }

        return result;
    }

    @Override
    public Map<String, Object> loginSuccess(LoginSuccessPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION);
            result.put("msg", "param errro");
            return result;
        }
        String token = UUID.randomUUID().toString();
        String key = params.getPlatformId() + "_" + params.getAppId() + "_" + token;
        redisUtil.setKeyValue(key, params.getExtend(), 120);
        result.put("token", token);
        return result;
    }

    @Override
    public Map<String, Object> getUserIdByToken(GetUserInfoPattern params) {
        logger.info("-getUserIdByToken--");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION);
            result.put("msg", "param errro");
            return result;
        }
        String key = params.getPlatformId() + "_" + params.getAppId() + "_" + params.getSessionId();
        logger.info("key:{}",key);
        String userid = redisUtil.getValue(key);
        logger.info("userid:{}",userid);
        if (null==userid||StringUtil.isNullOrEmpty(userid)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL);
            result.put("msg", "token errro");
            return result;
        }
        result.put("userid", userid);
        if (params.getPlatformId()==38 && params.getAppId().longValue()==Long.parseLong("180830054479")){
            result.put("userid",getUUWdqkUserid(userid));
        }
        return result;
    }

    @Override
    public Map<String, Object> validateSession(ValidateSessionPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION);
            return result;
        }
        String token = UUID.randomUUID().toString();
        String key = params.getPlatformId() + "_" + params.getAppId() + "_" + token;
        String uid = redisUtil.getValue(key);
        result.put("uid", uid);
        if (uid != null) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS);
            result.put("msg", "");
        } else {

            result.put("msg", "token 无效");
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION);
        }
        return result;

    }

    private boolean isParameterValid(BaseDto params) {
        if (params.isEmpty()) {
            logger.info("params is empty");
            return false;
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndAppId(params.getPlatformId(), params.getAppId());
        if (null == platformGame) {
            logger.debug("{}", "平台和游戏没有关联 appid" + params.getAppId() + " platfromId" + params.getPlatformId());
            return false;
        }
        logger.info("params success");
        return true;
    }

    @Override
    public String orderSuccessNotify(HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        String amount = request.getParameter("amount");
        String status = request.getParameter("status");
        String cpOrderId = request.getParameter("cpOrderId");
        String sign = request.getParameter("sign");

        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(amount) || StringUtils.isBlank(status)
                || StringUtils.isBlank(cpOrderId) || StringUtils.isBlank(sign)) {

            return "success";
        }

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("amount", amount);
        params.put("status", status);
        params.put("cpOrderId", cpOrderId);
        params.put("payKey", Constants.IOS_PAY_KEY);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (!StringUtils.equals(sign, validSign)) {
            return "success";
        }

        Order order = orderService.getOrderByOrderId(orderId);
        if (order == null) {
            return "success";
        }

        if (StringUtils.equals("0", status)) {
            order.setStatus(Order.STATUS_PAYSUCCESS);
            order.setNotifyStatus(Order.NOTIFYSTATUS_SUCCESS);
            order.setUpdateTime(new Date());
            order.setErrorMsg(null);
            basicRepository.updateStatusPay(order);
        } else if (StringUtils.equals("1", status)) {
            orderService.payFail(orderId, "ios server notify order payfailed");
        }
        return "success";
    }


    private String getUUWdqkUserid(String userid){

        if (userid.equals("2018091119300081756670")){
            return "090FA5ABEF62EE5EAD76728D76DCA4F9";
        }
        if (userid.equals("2018091118234228373659")){
           return "C422A1B51B29F3DCDCE8B79A3DB91326" ;
        }
        if (userid.equals("2018091210023549401314")){//淡月幽篁
            return "B9E4BFE482315B816360943F9210D062" ;
        }
        if (userid.equals("2018091209225995110114")){//果果
           return "385C8127C68F493A836E43B2E12B40BF";
        }
        if (userid.equals("2018091119282723366769")){//叶灵
            return "E3D22C06CABA1B197FE4B697239DE7D7";
        }
        if (userid.equals("2018091208250679339691")){//窝窝
            return "0C0628378F459349570C69D896A41D01";
        }
        if (userid.equals("2018091209104344358814")){//无所谓
            return "07CF1F90481F8D90F0AA02395B3CADC8" ;
        }
        if (userid.equals("2018091117223337200800")){// 玖月玲珑心
            return "F5F7195D578F6883FE605967160EF7E9";
        }
        if (userid.equals("2018091118215658081360")){//逍遥开心
            return "8EA11C9146422BD552FF35C22AF8D96E";
        }
        if (userid.equals("2018091210432417461090")){//小枪
            return "340A293804B91F09CCF3215A16A6780D";
        }
        if (userid.equals("2018091213130464794979")){// 小瞳
            return "534984A41459B665EF8AAFCF55137FA2";
        }
        if (userid.equals("2018091214262091543739")){//丨丶锦瑟
            return "AB338CED47977ABD7DEC66B86387F478" ;
        }
        return userid ;
    }

}
