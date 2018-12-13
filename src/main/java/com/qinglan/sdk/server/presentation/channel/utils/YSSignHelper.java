package com.qinglan.sdk.server.presentation.channel.utils;

import com.qinglan.sdk.server.common.RSAUtils;

import java.util.*;

public class YSSignHelper {
    public static final String SIGN_ALGORITHMS = "SHA256withRSA";
    public static final String INPUT_CHARSET = "utf-8";

    public static boolean verifyResponseSign(Map<String, String> sParaTemp, String publicKey, String appKey) {
        if (sParaTemp.get("sign") == null) {
            return false;
        }
        String content = getRequestContent(sParaTemp);
        content = content + "&appKey=" + appKey;
        return RSAUtils.verify(content, sParaTemp.get("sign"), publicKey, INPUT_CHARSET, SIGN_ALGORITHMS);
    }

    /**
     * @param sParaTemp  请求参数
     * @param privateKey 私钥
     */
    public static void buildPara(Map<String, String> sParaTemp, String privateKey, String appKey) {
        // 除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        // 生成签名结果
        String mysign = buildRequestMysign(sPara, privateKey, appKey);
        // 签名结果与签名方式加入请求提交参数组中
        sParaTemp.put("sign", mysign);
    }

    /**
     * @param sParaTemp
     * @return
     */
    private static String getRequestContent(Map<String, String> sParaTemp) {
        // 除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        return createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
    }

    /**
     * 除去数组中的空值、签名参数和appKey
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("appKey")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 生成签名结果
     *
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
    private static String buildRequestMysign(Map<String, String> sPara, String privateKey, String appKey) {
        String prestr = createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        prestr = prestr + "&appKey=" + appKey;
        String sign = RSAUtils.sign(prestr, privateKey, INPUT_CHARSET, SIGN_ALGORITHMS);
        return sign;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}
