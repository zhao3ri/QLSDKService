package com.qinglan.sdk.server.service.impl;

import com.qinglan.sdk.server.common.Base64;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.Constants;
import com.qinglan.sdk.server.domain.*;
import com.qinglan.sdk.server.dao.Account;
import com.qinglan.sdk.server.dao.HeartbeatEvent;
import com.qinglan.sdk.server.domain.dto.*;
import com.qinglan.sdk.server.reporsitory.BasicRepository;
import com.qinglan.sdk.server.service.AccountService;
import com.qinglan.sdk.server.service.OrderService;
import com.qinglan.sdk.server.utils.RedisUtil;
import com.zhidian3g.ddd.infrastructure.event.EventPublisher;
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

    private static final String RESULT_KRY_PAY_TYPE = "payType";
    private static final String RESULT_KEY_AGENT_ID = "agentId";
    private static final String RESULT_KEY_CREATE_TIME = "createTime";
    private static final String RESULT_KEY_LOGIN_TIME = "loginTime";
    private static final String RESULT_KEY_TOKEN_ID = "tokenId";
    private static final String RESULT_KEY_ORDER_ID = "orderId";
    private static final String RESULT_KEY_NOTIFY_URL = "notifyUrl";
    private static final String RESULT_KEY_ERROR = "error";
    private static final String RESULT_KEY_TOKEN = "token";
    private static final int TYPE_ALIPAY = 1;
    private static final int TYPE_WECHATPAY = 2;
    private static final String CODE_ALIPAY = "22";
    private static final String CODE_WECHATPAY = "30";

    private static final String PARAM_KEY_VERSION = "version";
    private static final String PARAM_KEY_AGENT_ID = "agent_id";
    private static final String PARAM_KEY_AGENT_BILL_ID = "agent_bill_id";
    private static final String PARAM_KEY_AGENT_BILL_TIME = "agent_bill_time";
    private static final String PARAM_KEY_PAY_TYPE = "pay_type";
    private static final String PARAM_KEY_PAY_AMT = "pay_amt";
    private static final String PARAM_KEY_WECHAT_PAY_KEY = "key";
    private static final String PARAM_KEY_PAY_MESSAGE = "pay_message";
    private static final String PARAM_KEY_NOTIFY_URL = "notify_url";
    private static final String PARAM_KEY_USER_IP = "user_ip";
    private static final String PARAM_KEY_USER_ID = "userid";
    private static final String PARAM_KEY_UID = "uid";
    private static final String PARAM_KEY_SIGN = "sign";
    private static final String PARAM_KEY_RETURN_URL = "return_url";
    private static final String PARAM_EKY_GOODS_NAME = "goods_name";
    private static final String PARAM_KEY_GOODS_NUM = "goods_num";
    private static final String PARAM_KEY_GOODS_NOTE = "goods_note";
    private static final String PARAM_KEY_REMARK = "remark";
    private static final String PARAM_KEY_META_OPTION = "meta_option";
    private static final String PARAM_KEY_RESULT = "result";
    private static final String PARAM_KEY_JNET_BILL_NO = "jnet_bill_no";
    private static final String PARAM_KEY_OS = "s";
    private static final String PARAM_KEY_APP_NAME = "n";
    private static final String PARAM_KEY_PACKAGE_NAME = "id";
    private static final String PARAM_KEY_ORDER_ID = "orderId";
    private static final String PARAM_KEY_AMOUNT = "amount";
    private static final String PARAM_KEY_STATUS = "status";
    private static final String PARAM_KEY_CP_ORDER_ID = "cpOrderId";
    private static final String PARAM_KEY_PAY_KEY = "payKey";

    private static final String ELEMENT_TOKEN_ID = "token_id";
    private static final String ELEMENT_ERROR = "error";

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
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }

        PlatformGame platformGame = basicRepository.getByPlatformAndAppId(params.getPlatformId(), params.getAppId());
        if (platformGame.getRegistStatus().equals(1)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_STOP_REGIST_CODE);
            return result;
        }
        publisher.publish(new InitialEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        return result;
    }

    @Override
    public Map<String, Object> login(LoginPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }

        publisher.publish(new LoginEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        result.put(RESULT_KEY_LOGIN_TIME, System.currentTimeMillis());

        Account account = basicRepository.getRoleCreateTime(params.getAppId(), params.getPlatformId(), params.getZoneId(), params.getRoleId(), params.getRoleName());
        if (account != null && account.getCreateTime() != null) {
            result.put(RESULT_KEY_CREATE_TIME, account.getCreateTime().getTime());
        } else {
            result.put(RESULT_KEY_CREATE_TIME, 0);
        }
        return result;
    }

    @Override
    public Map<String, Object> heartbeat(HeartbeatPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }

        publisher.publish(new HeartbeatEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        return result;
    }

    @Override
    public Map<String, Object> logout(LogoutPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }

        publisher.publish(new LogoutEvent(params));
        //listener.handleLogoutEvent(new LogoutEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        return result;
    }

    @Override
    public Map<String, Object> quit(QuitPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }
        publisher.publish(new QuitEvent(params));
        //listener.handleQuitEvent(new QuitEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        return result;
    }

    @Override
    public Map<String, Object> roleEstablish(RoleEstablishPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }
        Date createTime = new Date();
        params.setCreatTime(createTime);

        publisher.publish(new RoleEstablishEvent(params));
        //listener.handleRoleEstablishEvent(new RoleEstablishEvent(params));
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        result.put(RESULT_KEY_CREATE_TIME, createTime.getTime());
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

        if (params.getPayType() == TYPE_WECHATPAY) {
            logger.info("微信支付开始下单" + JsonMapper.toJson(params));
        } else {
            logger.info("支付宝支付开始下单" + JsonMapper.toJson(params));
        }
        String version = "1";
        String pay_type = CODE_ALIPAY;
        result.put(RESULT_KRY_PAY_TYPE, CODE_ALIPAY);
        result.put(RESULT_KEY_AGENT_ID, HeepayTradeConfig.getInstance().getHeepayAgentid());
        if (params.getPayType() == TYPE_WECHATPAY) {
            pay_type = CODE_WECHATPAY;
            result.put(RESULT_KRY_PAY_TYPE, CODE_WECHATPAY);
        }
        String agentId = HeepayTradeConfig.getInstance().getHeepayAgentid();
        String agentBillId = orderId;
        String payAmt = new DecimalFormat("0.00").format(params.getAmount() / 100f);
        String notifyUrl = HeepayTradeConfig.getInstance().getCallbackUrl();
        String userIp = params.getIp();
        String agentBillTime = DateUtils.format(new Date(), DateUtils.yyyyMMddHHmmss);
        String goodsName = params.getCpOrderId();
        String goodsNum = "1";
        String remark = params.getCpOrderId();
        String goodsNote = params.getCpOrderId();

        Map<String, Object> postparams = new LinkedHashMap<String, Object>();
        postparams.put(PARAM_KEY_VERSION, version);
        postparams.put(PARAM_KEY_AGENT_ID, agentId);
        postparams.put(PARAM_KEY_AGENT_BILL_ID, agentBillId);
        postparams.put(PARAM_KEY_AGENT_BILL_TIME, agentBillTime);
        postparams.put(PARAM_KEY_PAY_TYPE, pay_type);
        postparams.put(PARAM_KEY_PAY_AMT, payAmt);
        postparams.put(PARAM_KEY_NOTIFY_URL, notifyUrl);

        postparams.put(PARAM_KEY_USER_IP, userIp.replace(".", "_"));
        postparams.put(PARAM_KEY_WECHAT_PAY_KEY, HeepayTradeConfig.getInstance().getWechatPayKey());
        postparams.put(PARAM_KEY_SIGN, Sign.signByMD5Unsort(postparams, "").toLowerCase());

        postparams.remove(PARAM_KEY_WECHAT_PAY_KEY);
        postparams.put(PARAM_KEY_RETURN_URL, notifyUrl);
        postparams.put(PARAM_EKY_GOODS_NAME, goodsName);
        postparams.put(PARAM_KEY_GOODS_NUM, goodsNum);
        postparams.put(PARAM_KEY_REMARK, remark);
        postparams.put(PARAM_KEY_GOODS_NOTE, goodsNote);
        postparams.put(PARAM_KEY_META_OPTION, buildMetaOption(params.getAppName(), params.getPackageName()));
        logger.debug("postparams: " + postparams);
        String retStr = "";
        try {
            retStr = HttpUtils.post(HeepayTradeConfig.getInstance().getInitUrl(), postparams);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("postToHttps error：", e);
        }

        logger.debug("汇付宝 pay retStr : " + retStr);
        String token_id = Tools.getXMLValue(retStr, ELEMENT_TOKEN_ID);
        if (StringUtils.isNotBlank(token_id)) {
            result.put(RESULT_KEY_TOKEN_ID, token_id);
            result.put(RESULT_KEY_ORDER_ID, orderId);
            result.put(RESULT_KEY_ERROR, "");

            return result;
        }
        result.put(RESULT_KEY_TOKEN_ID, "");
        result.put(RESULT_KEY_ORDER_ID, orderId);
        result.put(RESULT_KEY_ERROR, Tools.getXMLValue(retStr, ELEMENT_ERROR));
        return result;
    }

    @Override
    public String payNotify(HttpServletRequest request) {
        String result = request.getParameter(PARAM_KEY_RESULT);
        String pay_message = request.getParameter(PARAM_KEY_PAY_MESSAGE);
        String agent_id = request.getParameter(PARAM_KEY_AGENT_ID);
        String jnet_bill_no = request.getParameter(PARAM_KEY_JNET_BILL_NO);
        String agent_bill_id = request.getParameter(PARAM_KEY_AGENT_BILL_ID);
        String pay_type = request.getParameter(PARAM_KEY_PAY_TYPE);
        String pay_amt = request.getParameter(PARAM_KEY_PAY_AMT);
        String remark = request.getParameter(PARAM_KEY_REMARK);
        String sign = request.getParameter(PARAM_KEY_SIGN);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_KEY_RESULT, result);
        params.put(PARAM_KEY_AGENT_ID, agent_id);
        params.put(PARAM_KEY_JNET_BILL_NO, jnet_bill_no);
        params.put(PARAM_KEY_AGENT_BILL_ID, agent_bill_id);
        params.put(PARAM_KEY_PAY_TYPE, pay_type);
        params.put(PARAM_KEY_PAY_AMT, pay_amt);
        params.put(PARAM_KEY_REMARK, remark);
        params.put(PARAM_KEY_WECHAT_PAY_KEY, HeepayTradeConfig.getInstance().getWechatPayKey());

        String validSign = Sign.signByMD5Unsort(params, "").toLowerCase();

        Order order = orderService.getOrderByOrderId(agent_bill_id);

        if (StringUtils.equals(sign, validSign)) {
            logger.info("Heepay pay notify valid sign success");

            if (null == order) {
                return Constants.RESULT_ERROR;
            }

            if ("0".equals(result)) {
                return Constants.RESULT_OK;
            } else if ("-1".equals(result)) {
                orderService.payFail(order.getOrderId(), "回调返回该订单支付失败");
                return Constants.RESULT_OK;
            }
            if ("1".equals(result)) {
                if (order.getAmount().doubleValue() > Double.valueOf(pay_amt) * 100) {
                    orderService.payFail(agent_bill_id, "实际充值金额与订单金额不一致");
                    return Constants.RESULT_ERROR;
                }
                orderService.paySuccess(order.getOrderId());
                return Constants.RESULT_OK;
            }
            return Constants.RESULT_OK;
        } else {
            logger.info("Heep pay notify valid sign failed data{}, sign, valid sign " + params.toString() + ", " + sign + ", " + validSign);
            return Constants.RESULT_ERROR;
        }

    }

    private String buildMetaOption(String appName, String packageName) {
        Map<String, String> androidMap = new HashMap<String, String>();
        androidMap.put(PARAM_KEY_OS, Constants.OS_ANDROID);
        androidMap.put(PARAM_KEY_APP_NAME, appName);
        androidMap.put(PARAM_KEY_PACKAGE_NAME, packageName);
        Map<String, String> iosMap = new HashMap<String, String>();
        iosMap.put(PARAM_KEY_OS, Constants.OS_IOS);
        iosMap.put(PARAM_KEY_APP_NAME, "");
        iosMap.put(PARAM_KEY_PACKAGE_NAME, "");
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
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            return result;
        }
        //联运渠道是否正常
        PlatformGame platformGame = basicRepository.getByPlatformAndAppId(params.getPlatformId(), params.getAppId());
        if (platformGame.getStatus().equals(1)) {
            result.put(Constants.RESPONSE_CODE, Constants.CHANEL_SELF_PAY);
            return result;
        }


        if (!this.checkPlatformBalance(params.getAmount(), platformGame)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_BALANCE_ERROR_CODE);
            return result;
        }

        String orderId = orderService.saveOrder(params);
        if (StringUtils.isEmpty(orderId)) {
            logger.warn("create order failed.");
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION_CODE);
            return result;
        }

        Platform platform = basicRepository.getPlatform(params.getPlatformId());
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        result.put(RESULT_KEY_ORDER_ID, orderId);
        result.put(RESULT_KEY_NOTIFY_URL, platform.getPlatformCallbackUrl());

        //如果是07073、乐嗨嗨平台则返回加密后的订单号
        if (params.getPlatformId() == Constants.LESHAN_PLATFORM_ID || params.getPlatformId() == Constants.LEHIHI_PLATFORM_ID) {
            result.put(RESULT_KEY_ORDER_ID, DES.encryptAndBase64(orderId, Constants.BASE64_ORDERID_KEY));
        }

        return result;
    }

    @Override
    public Map<String, Object> loginSuccess(LoginSuccessPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION_CODE);
            result.put(Constants.RESPONSE_MSG, Constants.RESPONSE_PARAM_ERROR);
            return result;
        }
        String token = UUID.randomUUID().toString();
        String key = params.getPlatformId() + "_" + params.getAppId() + "_" + token;
        redisUtil.setKeyValue(key, params.getExtend(), 120);
        result.put(RESULT_KEY_TOKEN, token);
        return result;
    }

    @Override
    public Map<String, Object> getUserIdByToken(GetUserInfoPattern params) {
        logger.info("-getUserIdByToken--");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION_CODE);
            result.put(Constants.RESPONSE_MSG, Constants.RESPONSE_PARAM_ERROR);
            return result;
        }
        String key = params.getPlatformId() + "_" + params.getAppId() + "_" + params.getSessionId();
        logger.info("key:{}", key);
        String userid = redisUtil.getValue(key);
        logger.info("userid:{}", userid);
        if (null == userid || StringUtil.isNullOrEmpty(userid)) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_PARAMETER_ILLEGAL_CODE);
            result.put(Constants.RESPONSE_MSG, Constants.RESPONSE_TOKEN_ERROR);
            return result;
        }
        result.put(PARAM_KEY_USER_ID, userid);
        if (params.getPlatformId() == 38 && params.getAppId().longValue() == Long.parseLong("180830054479")) {
            result.put(PARAM_KEY_USER_ID, getUUWdqkUserid(userid));
        }
        return result;
    }

    @Override
    public Map<String, Object> validateSession(ValidateSessionPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION_CODE);
            return result;
        }
        String token = UUID.randomUUID().toString();
        String key = params.getPlatformId() + "_" + params.getAppId() + "_" + token;
        String uid = redisUtil.getValue(key);
        result.put(PARAM_KEY_UID, uid);
        if (uid != null) {
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SUCCESS_CODE);
            result.put(Constants.RESPONSE_MSG, "");
        } else {

            result.put(Constants.RESPONSE_MSG, "token 无效");
            result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION_CODE);
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
        String orderId = request.getParameter(PARAM_KEY_ORDER_ID);
        String amount = request.getParameter(PARAM_KEY_AMOUNT);
        String status = request.getParameter(PARAM_KEY_STATUS);
        String cpOrderId = request.getParameter(PARAM_KEY_CP_ORDER_ID);
        String sign = request.getParameter(PARAM_KEY_SIGN);

        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(amount) || StringUtils.isBlank(status)
                || StringUtils.isBlank(cpOrderId) || StringUtils.isBlank(sign)) {

            return Constants.RESULT_SUCCESS;
        }

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(PARAM_KEY_ORDER_ID, orderId);
        params.put(PARAM_KEY_AMOUNT, amount);
        params.put(PARAM_KEY_STATUS, status);
        params.put(PARAM_KEY_CP_ORDER_ID, cpOrderId);
        params.put(PARAM_KEY_PAY_KEY, Constants.IOS_PAY_KEY);

        String validSign = Sign.signByMD5Unsort(params, "");
        if (!StringUtils.equals(sign, validSign)) {
            return Constants.RESULT_SUCCESS;
        }

        Order order = orderService.getOrderByOrderId(orderId);
        if (order == null) {
            return Constants.RESULT_SUCCESS;
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
        return Constants.RESULT_SUCCESS;
    }


    private String getUUWdqkUserid(String userid) {

        if (userid.equals("2018091119300081756670")) {
            return "090FA5ABEF62EE5EAD76728D76DCA4F9";
        }
        if (userid.equals("2018091118234228373659")) {
            return "C422A1B51B29F3DCDCE8B79A3DB91326";
        }
        if (userid.equals("2018091210023549401314")) {//淡月幽篁
            return "B9E4BFE482315B816360943F9210D062";
        }
        if (userid.equals("2018091209225995110114")) {//果果
            return "385C8127C68F493A836E43B2E12B40BF";
        }
        if (userid.equals("2018091119282723366769")) {//叶灵
            return "E3D22C06CABA1B197FE4B697239DE7D7";
        }
        if (userid.equals("2018091208250679339691")) {//窝窝
            return "0C0628378F459349570C69D896A41D01";
        }
        if (userid.equals("2018091209104344358814")) {//无所谓
            return "07CF1F90481F8D90F0AA02395B3CADC8";
        }
        if (userid.equals("2018091117223337200800")) {// 玖月玲珑心
            return "F5F7195D578F6883FE605967160EF7E9";
        }
        if (userid.equals("2018091118215658081360")) {//逍遥开心
            return "8EA11C9146422BD552FF35C22AF8D96E";
        }
        if (userid.equals("2018091210432417461090")) {//小枪
            return "340A293804B91F09CCF3215A16A6780D";
        }
        if (userid.equals("2018091213130464794979")) {// 小瞳
            return "534984A41459B665EF8AAFCF55137FA2";
        }
        if (userid.equals("2018091214262091543739")) {//锦瑟
            return "AB338CED47977ABD7DEC66B86387F478";
        }
        return userid;
    }

}
