package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.common.Sign;
import com.qinglan.sdk.server.domain.basic.Platform;
import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.presentation.channel.entity.UCPayResult;
import com.qinglan.sdk.server.presentation.basic.dto.OrderBasicInfo;
import com.qinglan.sdk.server.presentation.channel.IChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UCChannel implements IChannel {
    public static final String REQUEST_KEY_SID = "sid";
    public static final String REQUEST_KEY_GAME_ID = "gameId";
    public static final String REQUEST_KEY_ID = "id";
    public static final String REQUEST_KEY_DATA = "data";
    public static final String REQUEST_KEY_GAME = "game";
    public static final String REQUEST_KEY_SIGN = "sign";
    public static final String SIGN_PREFIX = "sid=";

    public static final String PARAM_CALLBACK_INFO = "callbackInfo";
    public static final String PARAM_NOTIFY_URL = "notifyUrl";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_CP_ORDER_ID = "cpOrderId";
    public static final String PARAM_ACCOUNT_ID = "accountId";

    private Platform mPlatform;
    private PlatformGame mPlatformGame;
    private boolean isInit = false;

    @Override
    public void init(BasicRepository basicRepository, long gameId, int channelId) {
        init(basicRepository.getPlatform(channelId), basicRepository.getByPlatformAndGameId(channelId, gameId));
    }

    @Override
    public void init(Platform platform, PlatformGame platformGame) {
        mPlatform = platform;
        mPlatformGame = platformGame;
        isInit = true;
    }

    @Override
    public Platform getChannelInfo() {
        return mPlatform;
    }

    @Override
    public String verifySession(String... args) {
        checkInit();
        /*
         * 第三方平台认证
         * 请求参数json示例如下：
         * {
         *      "id":1332406591685,
         *      "game":{"gameId":5},
         *      "data":{
         *          "sid":"110adf4c-f2d3-4be5-8a9c-3741a83e5853"
         *      },
         *      "sign":"bb926c2a9944e9b4f2f6639d928dc95c"
         * }
         */
        if (null == mPlatformGame || null == args || args.length == 0)
            return "";
        String sid = args[0];
        String appID = null;
        if (args.length > 1)
            appID = args[1];
        if (null == appID || appID.isEmpty())
            appID = mPlatformGame.getAppID();
        String appKey = mPlatformGame.getAppKey();

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> game = new HashMap<>();
        data.put(REQUEST_KEY_SID, sid);
        game.put(REQUEST_KEY_GAME_ID, appID);
        params.put(REQUEST_KEY_ID, System.currentTimeMillis());
        params.put(REQUEST_KEY_DATA, data);
        params.put(REQUEST_KEY_GAME, game);
        params.put(REQUEST_KEY_SIGN, MD5.encode(SIGN_PREFIX + sid + appKey));
        try {
            return HttpUtils.doPostToJson(mPlatform.getVerifyUrl(), JsonMapper.toJson(params), 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String signOrder(OrderBasicInfo order) {
        checkInit();
        String appKey = mPlatformGame.getAppKey();
        Map<String, String> signMap = new TreeMap<>();
        signMap.put(PARAM_CALLBACK_INFO, order.getExtInfo());
        signMap.put(PARAM_NOTIFY_URL, order.getNotifyUrl());
        signMap.put(PARAM_AMOUNT, order.getAmount().toString());
        signMap.put(PARAM_CP_ORDER_ID, order.getCpOrderId());
        signMap.put(PARAM_ACCOUNT_ID, order.getUid());
        String sign = Sign.aliSign(signMap, appKey);
        return sign;
    }

    @Override
    public boolean getPayResult(String json, Map<String, Object> payResult) {
        checkInit();
        /*
         * 参数json示例如下：
         * {
         *           "ver": "2.0",
         *           "data":{
         *               "orderId":"abcf1330",
         *               "gameId":123,
         *               "accountId":"12221222211123",
         *               "creator":"JY",
         *               "payWay":1,
         *               "amount":"100.00",
         *               "callbackInfo":"custominfo=xxxxx#user=xxxx",
         *               "orderStatus":"S",
         *               "failedDesc":"",
         *               "cpOrderId":"1234567"
         *                },
         *             "sign":"6362e564f832d2e8bbcbd50e75409d47"
         * }
         */
        Map<String, Object> data = (Map<String, Object>) payResult.get(REQUEST_KEY_DATA);
        if (data == null || data.isEmpty()) {
            return false;
        }

        String apiKey = mPlatformGame.getAppKey();

        String sign = Sign.signParamsByMD5(data, apiKey);
        if (sign.equals(payResult.get(REQUEST_KEY_SIGN).toString())) {
            return true;
        }
        return false;
    }

    private void checkInit() {
        if (!isInit)
            throw new RuntimeException("Please must be init before using");
    }
}
