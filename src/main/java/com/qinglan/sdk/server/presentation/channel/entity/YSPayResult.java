package com.qinglan.sdk.server.presentation.channel.entity;

import com.qinglan.sdk.server.presentation.channel.utils.YSSignHelper;

import java.util.Map;

public class YSPayResult {
    @SuppressWarnings("unused")
    private String appId;
    private String appKey;
    private String pubKey;

    public YSPayResult(String appId, String appKey, String pubKey) {
        this.appId = appId;
        this.appKey = appKey;
        this.pubKey = pubKey;
    }

    /**
     * 校验交易通知的sign
     *
     * @param queryPara 请求参数map
     * @return
     * @throws Exception
     */
    public Boolean checkSignNotifyPayResult(Map<String, String> queryPara) throws Exception {
        Boolean isOk = false;
        try {
            isOk = YSSignHelper.verifyResponseSign(queryPara, pubKey, appKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return isOk;
    }
}
