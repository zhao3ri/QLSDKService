package com.qinglan.sdk.server.application.impl;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.channel.ChannelConstants;
import com.qinglan.sdk.server.HeepayTradeConfig;
import com.qinglan.sdk.server.application.AccountService;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.common.*;
import com.qinglan.sdk.server.data.infrastructure.event.EventPublisher;
import com.qinglan.sdk.server.Constants;
import com.qinglan.sdk.server.domain.basic.*;
import com.qinglan.sdk.server.domain.basic.event.*;
import com.qinglan.sdk.server.dto.*;
import com.qinglan.sdk.server.application.redis.RedisUtil;
import com.qinglan.sdk.server.channel.entity.UCOrderSignRequest;
import com.qinglan.sdk.server.channel.impl.UCChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;

import static com.qinglan.sdk.server.Constants.*;
import static com.qinglan.sdk.server.HeepayTradeConfig.*;

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
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(params.getChannelId(), params.getGameId());
        if (channelGame.getRegistStatus().equals(GAME_CHANNEL_CODE_REGISTE_STATUS_DISABLE)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_STOP_REGIST);
            return result;
        }
        publisher.publish(new InitialEvent(params));
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> join(GameStartPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }

        publisher.publish(new GameStartEvent(params));
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        result.put(RESPONSE_KEY_START_TIME, System.currentTimeMillis());

        Role role = basicRepository.getRoleCreateTime(params.getGameId(), params.getChannelId(), params.getZoneId(), params.getRoleId(), params.getRoleName());
        if (role != null && role.getCreateTime() != null) {
            result.put(RESPONSE_KEY_CREATE_TIME, role.getCreateTime().getTime());
        } else {
            result.put(RESPONSE_KEY_CREATE_TIME, 0);
        }
        return result;
    }

    @Override
    public Map<String, Object> heartbeat(HeartbeatPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }

        publisher.publish(new HeartbeatEvent(params));
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> logout(LogoutPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }

        publisher.publish(new LogoutEvent(params));
        //listener.handleLogoutEvent(new LogoutEvent(params));
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> quit(QuitPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }
        publisher.publish(new QuitEvent(params));
        //listener.handleQuitEvent(new QuitEvent(params));
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> roleCreate(RoleCreatePattern params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }
        Date createTime = new Date();
        params.setCreateTime(createTime);

        publisher.publish(new RoleEstablishEvent(params));
        //listener.handleRoleEstablishEvent(new RoleEstablishEvent(params));
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        result.put(RESPONSE_KEY_CREATE_TIME, createTime.getTime());
        return result;
    }

    /**
     * @param money
     * @return 0 ：余额不足 2：扣款失败  1：成功
     */
    public boolean checkChannelBalance(int money, ChannelGameEntity channelGame) {
        ChannelEntity channel = basicRepository.getChannel(channelGame.getChannelId());
        money = money * channelGame.getDiscount() / 100;
        if (money > channel.getBalance()) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> selforderGenerate(OrderGenerateRequest params) {
        String notifyUrl = params.getNotifyUrl();//支付回调地址
        if (StringUtil.isNullOrEmpty(notifyUrl)) {
            ChannelEntity channel = basicRepository.getChannel(params.getChannelId());
            notifyUrl = channel.getChannelCallbackUrl();
            params.setNotifyUrl(notifyUrl);
        }
        String orderId = orderService.saveOrder(params);
        Map<String, Object> result = new HashMap<String, Object>();

        String version = "1";
        String pay_type = HEEPAY_PAY_TYPE_ALIPAY;
        result.put(RESULT_KEY_AGENT_ID, HeepayTradeConfig.getInstance().getHeepayAgentid());
        if (params.getPayType() == REQUEST_PAY_TYPE_WECHAT) {
            logger.info("微信支付开始下单" + JsonMapper.toJson(params));
            pay_type = HEEPAY_PAY_TYPE_WECHA;
        } else {
            logger.info("支付宝支付开始下单" + JsonMapper.toJson(params));
        }
        result.put(RESULT_KEY_PAY_TYPE, pay_type);

        String agentId = HeepayTradeConfig.getInstance().getHeepayAgentid();
        String agentBillId = orderId;
        String payAmt = new DecimalFormat("0.00").format(params.getAmount() / 100f);
        String callbackUrl = HeepayTradeConfig.getInstance().getCallbackUrl();
        String userIp = params.getIp();
        String agentBillTime = DateUtils.format(new Date(), "yyyyMMddHHmmss");
        String goodsName = params.getGoodsName();
        String goodsNum = params.getGoodsCount() == 0 ? "1" : String.valueOf(params.getGoodsCount());
        String remark = params.getOrderId();
        String goodsNote = params.getExtInfo();

        Map<String, Object> postparams = new LinkedHashMap<String, Object>();
        postparams.put(HEEPAY_REQUEST_KEY_VERSION, version);
        postparams.put(HEEPAY_REQUEST_KEY_AGENT_ID, agentId);
        postparams.put(HEEPAY_REQUEST_KEY_AGENT_BILL_ID, agentBillId);
        postparams.put(HEEPAY_REQUEST_KEY_AGENT_BILL_TIME, agentBillTime);
        postparams.put(HEEPAY_REQUEST_KEY_PAY_TYPE, pay_type);
        postparams.put(HEEPAY_REQUEST_KEY_PAY_AMT, payAmt);
        postparams.put(HEEPAY_REQUEST_KEY_NOTIFY_URL, callbackUrl);

        postparams.put(HEEPAY_REQUEST_KEY_USER_IP, userIp.replace(".", "_"));
        postparams.put(HEEPAY_REQUEST_KEY_KEY, HeepayTradeConfig.getInstance().getWechatPayKey());
        postparams.put(HEEPAY_REQUEST_KEY_SIGN, Sign.signByMD5Unsort(postparams, "").toLowerCase());

        postparams.remove(HEEPAY_REQUEST_KEY_SIGN);
        postparams.put(HEEPAY_REQUEST_KEY_RETURN_URL, callbackUrl);
        postparams.put(HEEPAY_REQUEST_KEY_GOODS_NAME, goodsName);
        postparams.put(HEEPAY_REQUEST_KEY_GOODS_NUM, goodsNum);
        postparams.put(HEEPAY_REQUEST_KEY_GOODS_NOTE, remark);
        postparams.put(HEEPAY_REQUEST_KEY_REMARK, goodsNote);
        postparams.put(HEEPAY_REQUEST_KEY_META_OPTION, buildMetaOption(params.getGameName(), params.getPackageName()));
        logger.debug("postparams: " + postparams);
        String retStr = "";
        try {
            retStr = HttpUtils.post(HeepayTradeConfig.getInstance().getInitUrl(), postparams);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("postToHttps error：", e);
        }

        logger.debug("汇付宝 pay retStr : " + retStr);
        String tokenId = Tools.getXMLValue(retStr, HEEPAY_XML_VALUE_TOKEN_ID);
        if (StringUtils.isNotBlank(tokenId)) {
            result.put(RESULT_KEY_TOKEN_ID, tokenId);
            result.put(RESULT_KEY_ORDER_ID, orderId);
            result.put(RESULT_ERROR, "");

            return result;
        }
        result.put(RESULT_KEY_TOKEN_ID, "");
        result.put(RESULT_KEY_ORDER_ID, orderId);
        result.put(RESULT_ERROR, Tools.getXMLValue(retStr, RESULT_ERROR));
        return result;
    }

    private String buildMetaOption(String appName, String packageName) {
        Map<String, String> androidMap = new HashMap<String, String>();
        androidMap.put(HEEPAY_META_OPTION_OS, "Android");
        androidMap.put(HEEPAY_META_OPTION_APP_NAME, appName);
        androidMap.put(HEEPAY_META_OPTION_PACKAGE, packageName);
        Map<String, String> iosMap = new HashMap<String, String>();
        iosMap.put(HEEPAY_META_OPTION_OS, "IOS");
        iosMap.put(HEEPAY_META_OPTION_APP_NAME, "");
        iosMap.put(HEEPAY_META_OPTION_PACKAGE, "");
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
    public String payNotify(HttpServletRequest request) {
        String result = request.getParameter(HEEPAY_RESULT_KEY_RESULT);
        String payMessage = request.getParameter(HEEPAY_RESULT_KEY_PAY_MSG);
        String agentId = request.getParameter(HEEPAY_REQUEST_KEY_AGENT_ID);
        String jnetBillNo = request.getParameter(HEEPAY_REQUEST_KEY_JNET_BILL_NO);
        String agentBillId = request.getParameter(HEEPAY_REQUEST_KEY_AGENT_BILL_ID);
        String payType = request.getParameter(HEEPAY_REQUEST_KEY_PAY_TYPE);
        String payAmt = request.getParameter(HEEPAY_REQUEST_KEY_PAY_AMT);
        String remark = request.getParameter(HEEPAY_REQUEST_KEY_REMARK);
        String sign = request.getParameter(HEEPAY_RESULT_KEY_SIGN);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(HEEPAY_RESULT_KEY_RESULT, result);
        params.put(HEEPAY_REQUEST_KEY_AGENT_ID, agentId);
        params.put(HEEPAY_REQUEST_KEY_JNET_BILL_NO, jnetBillNo);
        params.put(HEEPAY_REQUEST_KEY_AGENT_BILL_ID, agentBillId);
        params.put(HEEPAY_REQUEST_KEY_PAY_TYPE, payType);
        params.put(HEEPAY_REQUEST_KEY_PAY_AMT, payAmt);
        params.put(HEEPAY_REQUEST_KEY_REMARK, remark);
        params.put(HEEPAY_REQUEST_KEY_KEY, HeepayTradeConfig.getInstance().getWechatPayKey());

        String validSign = Sign.signByMD5Unsort(params, "").toLowerCase();

        Order order = orderService.getOrderByOrderId(agentBillId);

        if (StringUtils.equals(sign, validSign)) {
            logger.info("Heepay pay notify valid sign success");

            if (null == order) {
                return RESULT_ERROR;
            }

            if (RESULT_CODE_DEFAULT.equals(result)) {
                return RESULT_OK;
            } else if (RESULT_CODE_PAY_FAIL.equals(result)) {
                orderService.payFail(order.getOrderId(), "回调返回该订单支付失败");
                return RESULT_OK;
            }
            if (RESULT_CODE_SUCCESS.equals(result)) {
                if (order.getAmount() > Double.valueOf(payAmt) * 100) {
                    orderService.payFail(agentBillId, "实际充值金额与订单金额不一致");
                    return RESULT_ERROR;
                }
                orderService.paySuccess(order.getOrderId());
                return RESULT_OK;
            }
        }
        logger.info("Heep pay notify valid sign failed data{}, sign, valid sign " + params.toString() + ", " + sign + ", " + validSign);
        return RESULT_ERROR;

    }

    @Override
    public Map<String, Object> orderGenerate(OrderGenerateRequest params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (!isParameterValid(params)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }
        //联运渠道是否正常
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(params.getChannelId(), params.getGameId());
        if (channelGame.getStatus().equals(CHANNEL_STATUS_NORMAL)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_CHANEL_SELF_PAY);
            return result;
        }

        if (!this.checkChannelBalance(params.getAmount(), channelGame)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_BLANCE_ERROR);
            return result;
        }

        ChannelEntity platform = basicRepository.getChannel(params.getChannelId());
        String notifyUrl = params.getNotifyUrl();//支付回调地址
        if (StringUtil.isNullOrEmpty(notifyUrl)) {
            notifyUrl = platform.getChannelCallbackUrl();
            params.setNotifyUrl(notifyUrl);
        }

        String orderId = orderService.saveOrder(params);
        if (StringUtils.isEmpty(orderId)) {
            logger.warn("create order failed.");
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SERVER_EXCEPTION);
            return result;
        }

        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        result.put(Constants.RESPONSE_KEY_ORDER_ID, orderId);

        result.put(Constants.RESPONSE_KEY_NOTIFY_URL, notifyUrl);

        //如果是07073、乐嗨嗨平台则返回加密后的订单号
        if (params.getChannelId() == Constants.LESHAN_PLATFORM_ID || params.getChannelId() == Constants.LEHIHI_PLATFORM_ID) {
            result.put(Constants.RESPONSE_KEY_ORDER_ID, DES.encryptAndBase64(orderId, Constants.BASE64_ORDERID_KEY));
        } else if (params.getChannelId() == ChannelConstants.CHANNEL_ID_UC) {
            //UC需要返回参数签名
            UCChannel ucChannel = new UCChannel();
            ucChannel.init(platform, channelGame);
            UCOrderSignRequest request = UCOrderSignRequest.getOrderByBean(params);
            result.put(UCChannel.REQUEST_KEY_SIGN, ucChannel.signOrder(request));
        }

        return result;
    }

    @Override
    public Map<String, Object> getToken(TokenPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }
        String token = UUID.randomUUID().toString();
        String key = params.getChannelId() + "_" + params.getGameId() + "_" + token;
        redisUtil.setKeyValue(key, params.getExtend(), 120);
        result.put(Constants.RESPONSE_KEY_TOKEN, token);
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> getUserIdByToken(GetUserInfoPattern params) {
        logger.info("--getUserIdByToken--");
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            result.put(Constants.RESPONSE_KEY_MESSAGE, "param error");
            return result;
        }
        String key = params.getChannelId() + "_" + params.getGameId() + "_" + params.getSessionId();
        logger.info("key:{}", key);
        String userid = redisUtil.getValue(key);
        logger.info("userid:{}", userid);
        if (null == userid || StringUtil.isNullOrEmpty(userid)) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            result.put(Constants.RESPONSE_KEY_MESSAGE, "token error");
            return result;
        }
        result.put("userid", userid);
        if (params.getChannelId() == 38 && params.getGameId().longValue() == Long.parseLong("180830054479")) {
            result.put("userid", getUUWdqkUserid(userid));
        }
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> queryOrder(QueryOrderRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (request.isEmpty()) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }
        Order order = basicRepository.getOrderStatus(request.getOrderId(), request.getGameId(), request.getChannelId());
        if (order == null) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SERVER_EXCEPTION);
            return result;
        }
        result.put(RESPONSE_KEY_ORDER_STATUS, order.getStatus());
        result.put(RESPONSE_KEY_ORDER_NOTIFY_STATUS, order.getNotifyStatus());
        result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> validateSession(ValidateSessionPattern params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.isEmpty()) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_PARAMETER_ILLEGAL);
            return result;
        }
        String token = UUID.randomUUID().toString();
        String key = params.getChannelId() + "_" + params.getGameId() + "_" + token;
        String uid = redisUtil.getValue(key);
        result.put(Constants.RESPONSE_KEY_UID, uid);
        if (uid != null) {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SUCCESS);
        } else {
            result.put(Constants.RESPONSE_KEY_CODE, Constants.RESPONSE_CODE_SERVER_EXCEPTION);
        }
        return result;

    }

    private boolean isParameterValid(BaseDto params) {
        if (params.isEmpty()) {
            logger.info("params is empty");
            return false;
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(params.getChannelId(), params.getGameId());
        if (null == channelGame) {
            logger.debug("{}", "平台和游戏没有关联 appid" + params.getGameId() + " channelId" + params.getChannelId());
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
            order.setStatus(Order.ORDER_STATUS_PAYMENT_SUCCESS);
            order.setNotifyStatus(Order.ORDER_NOTIFY_STATUS_SUCCESS);
            order.setUpdateTime(new Date());
            order.setErrorMsg(null);
            basicRepository.updateStatusPay(order);
        } else if (StringUtils.equals("1", status)) {
            orderService.payFail(orderId, "ios server notify order payfailed");
        }
        return "success";
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
        if (userid.equals("2018091214262091543739")) {//丨丶锦瑟
            return "AB338CED47977ABD7DEC66B86387F478";
        }
        return userid;
    }

}
