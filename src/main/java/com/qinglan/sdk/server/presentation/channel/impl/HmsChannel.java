package com.qinglan.sdk.server.presentation.channel.impl;

import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.StringUtil;
import com.qinglan.sdk.server.platform.ibei.SignHelper;
import com.qinglan.sdk.server.presentation.channel.entity.BaseRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPayResult;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.utils.HmsSignHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN;
import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN_TYPE;

public class HmsChannel extends BaseChannel {
    private static final String REQUEST_PARAM_METHOD = "method";
    private static final String REQUEST_PARAM_APPID = "appId";
    private static final String REQUEST_PARAM_CPID = "cpId";
    private static final String REQUEST_PARAM_TS = "ts";
    private static final String REQUEST_PARAM_PLAYER_ID = "playerId";
    private static final String REQUEST_PARAM_PLAYER_LEVEL = "playerLevel";
    private static final String REQUEST_PARAM_PLAYER_SIGN = "playerSSign";
    private static final String REQUEST_PARAM_CP_SIGN = "cpSign";

    private static final String REQUEST_PARAM_EXT_RESERVED = "extReserved";
    private static final String REQUEST_PARAM_SYS_RESERVED = "sysReserved";

    private static final int RESULT_PAY_CODE_SUCCESS = 0;
    private static final int RESULT_PAY_CODE_FAIL = 1;

    @Override
    public String verifySession(String... args) {
        checkInit();
        if (null == platformGame || null == platform || null == args || args.length == 0)
            return null;
        String priKey = platformGame.getPrivateKey();
        String verUrl = platform.getVerifyUrl();
        Map<String, Object> mockRequestParams = new HashMap<>();
        mockRequestParams.put(REQUEST_PARAM_METHOD, "external.hms.gs.checkPlayerSign");
        mockRequestParams.put(REQUEST_PARAM_APPID, args[0]);
        if (StringUtil.isNullOrEmpty(args[1])) {
            String cpId = platformGame.getConfig(REQUEST_PARAM_CPID);
            args[1] = cpId;
        }
        mockRequestParams.put(REQUEST_PARAM_CPID, args[1]);
        mockRequestParams.put(REQUEST_PARAM_TS, args[2]);
        mockRequestParams.put(REQUEST_PARAM_PLAYER_ID, args[3]);
        mockRequestParams.put(REQUEST_PARAM_PLAYER_LEVEL, args[4]);
        mockRequestParams.put(REQUEST_PARAM_PLAYER_SIGN, args[5]);
        mockRequestParams.put(REQUEST_PARAM_CP_SIGN, HmsSignHelper.generateCPSign(mockRequestParams, priKey));

        return HttpUtils.post(verUrl, mockRequestParams);
    }

    @Override
    public String signOrder(BaseRequest request) {
        checkInit();
        if (request instanceof HMSPaySignRequest) {
            String sign = HmsSignHelper.sign(((HMSPaySignRequest) request).getContent(), platformGame.getPrivateKey());
            return sign;
        }
        return null;
    }

    @Override
    public String returnPayResult(HttpServletRequest request, OrderService service) {
        Map<String, Object> map = getRequestParams(request);
        HMSPayResult result = new HMSPayResult();
        result.setResult(RESULT_PAY_CODE_FAIL);
        if (null == map || map.isEmpty()) {
            return JsonMapper.toJson(result);
        }

        String sign = (String) map.get(RESPONSE_KEY_SIGN);
        String signType = (String) map.get(RESPONSE_KEY_SIGN_TYPE);
        String content = HmsSignHelper.getSignData(map);
        if (HmsSignHelper.doCheck(content, sign, platformGame.getPublicKey(), signType)) {
            result.setResult(RESULT_PAY_CODE_SUCCESS);
        } else {
            result.setResult(RESULT_PAY_CODE_FAIL);
        }
        return JsonMapper.toJson(result);
    }

    private Map<String, Object> getRequestParams(HttpServletRequest request) {
        String str = getRequestString(request);
        Map<String, Object> valueMap = new HashMap<String, Object>();
        if (null == str || "".equals(str)) {
            return valueMap;
        }

        String[] valueKey = str.split("&");
        for (String temp : valueKey) {
            String[] single = temp.split("=");
            valueMap.put(single[0], single[1]);
        }
        System.out.println("The parameters in map are : " + valueMap);

        //接口中，如下参数sign和extReserved是URLEncode的，所以需要decode，其他参数直接是原始信息发送，不需要decode
        try {
            String sign = (String) valueMap.get(RESPONSE_KEY_SIGN);
            String extReserved = (String) valueMap.get(REQUEST_PARAM_EXT_RESERVED);
            String sysReserved = (String) valueMap.get(REQUEST_PARAM_SYS_RESERVED);

            if (null != sign) {
                sign = URLDecoder.decode(sign, "utf-8");
                valueMap.put(RESPONSE_KEY_SIGN, sign);
            }
            if (null != extReserved) {
                extReserved = URLDecoder.decode(extReserved, "utf-8");
                valueMap.put(REQUEST_PARAM_EXT_RESERVED, extReserved);
            }

            if (null != sysReserved) {
                sysReserved = URLDecoder.decode(sysReserved, "utf-8");
                valueMap.put(REQUEST_PARAM_SYS_RESERVED, sysReserved);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueMap;
    }

    private String getRequestString(HttpServletRequest request) {
        String line = null;
        StringBuffer sb = new StringBuffer();
        try {
            request.setCharacterEncoding("UTF-8");
            InputStream stream = request.getInputStream();
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            System.out.println("The original data is : " + sb.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
