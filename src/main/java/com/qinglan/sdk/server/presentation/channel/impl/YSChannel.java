package com.qinglan.sdk.server.presentation.channel.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.StringUtil;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;
import com.qinglan.sdk.server.presentation.channel.entity.YSPayResponseEntity;
import com.qinglan.sdk.server.presentation.channel.entity.YSPayResult;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 夜神渠道
 */
public class YSChannel extends BaseChannel {
    /**
     * 认证地址
     */
    public static final String VERIFY_URL = "/yeshen/session";
    /**
     * 支付回调地址
     */
    public static final String PAY_RETURN_URL = "/yeshen/pay/return";

    public static final String QUERY_PAY_RESULT_URL = "https://pay.yeshen.com/ws/payapi/v3/trade/query";
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public static final String MSG_SUCCESS = "SUCCESS";
    public static final String MSG_FAILURE = "FAILURE";

    public static final int PAY_STATUS_WAITING = 1;//待支付
    public static final int PAY_STATUS_SUCCESS = 2;//支付成功
    public static final int PAY_STATUS_FAILED = 3;//支付失败

    /**
     * 返回json示例
     * {
     *      "errNum":	"0",
     *      "transdata":	{
     *              "isValidate":	1
     *        }
     * }
     * */
    @Override
    public String verifySession(String... args) {
        checkInit();
        if (null == channelGame || null == channel || null == args || args.length == 0)
            return null;
        String accessToken = args[0];
        String uid = args[1];
        String appId = args[2];
        if (StringUtil.isNullOrEmpty(appId)) {
            appId = channelGame.getAppID();
        }
        try {
            String paramsJoin = "?accessToken=" + accessToken + "&uid=" + uid + "&appId=" + appId;
            String verifyUrl = channel.getVerifyUrl() + paramsJoin;
            String resultJson = HttpUtils.get(verifyUrl);
            return resultJson;
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
        checkInit();
        try {
            String strBody = getRequestString(request);
            JSONObject jo = JSON.parseObject(URLDecoder.decode(strBody, "UTF-8"));
            Map<String, String> params = new HashMap<>();
            for (String key : jo.keySet()) {
                params.put(key, jo.getString(key));
            }
            YSPayResponseEntity payResponseEntity = JSON.parseObject(JSON.toJSONString(params), YSPayResponseEntity.class);

            if (payResponseEntity == null || basicRepository == null) {
                return MSG_FAILURE;
            }
            Order order = getOrder(service, payResponseEntity.getGoodsOrderId(), payResponseEntity.getOrderId());
            channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
            if (order == null)
                return MSG_FAILURE;

            return getOrderResult(payResponseEntity, params, order, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MSG_FAILURE;
    }


    private String getOrderResult(YSPayResponseEntity payResponseEntity, Map<String, String> params, Order order, OrderService service) throws Exception {
        String appId = channelGame.getAppID();
        String appKey = channelGame.getAppKey();
        String pubKey = channelGame.getPublicKey();
        YSPayResult np = new YSPayResult(appId, appKey, pubKey);
        // 请求状态码判断
        if (payResponseEntity.getErrNum() == null || payResponseEntity.getErrNum() != SUCCESS) {
            // 通知请求状态码为非成功状态
            return MSG_FAILURE;
        }
        // 签名验证
        Boolean isOk = np.checkSignNotifyPayResult(params);
        if (isOk) {// 签名验证通过
            // 支付状态验证 支付状态1待支付 2成功 3失败
            System.out.println("支付状态：" + payResponseEntity.getPayStatus());
            if (payResponseEntity.getPayStatus() != null && payResponseEntity.getPayStatus() == PAY_STATUS_SUCCESS) {// 支付成功状态
                // T需判断该订单存在并且为己方合法订单，并且订单金额与商户的下单时的金额完全匹配(待商户处理)
                updateOrder(order, payResponseEntity.getOrderMoney(), service);// 订单金额单位为分
                return MSG_SUCCESS;
            }
        } else {
            // 签名不匹配
            service.payFail(order.getOrderId(), "order sign error");
            return MSG_FAILURE;
        }
        return MSG_FAILURE;
    }

    @Override
    public String queryOrder(Order order) {
        return null;
    }
}
