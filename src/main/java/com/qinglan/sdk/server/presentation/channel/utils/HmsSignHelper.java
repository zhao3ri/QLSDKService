package com.qinglan.sdk.server.presentation.channel.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class HmsSignHelper {
    /**
     * SIGN_ALGORITHMS
     * 使用加密算法规则
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    public static final String SIGN_ALGORITHMS256 = "SHA256WithRSA";
    public static final String PARAM_SIGN = "sign";
    public static final String PARAM_SIGN_TYPE = "signType";

    /**
     * 字符串编码
     */
    private static final String CHARSET = "UTF-8";

    private static String sign(byte[] data, String privateKey) {
        try {
            byte[] e = Base64.decodeBase64(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS256);
            signature.initSign(privateK);
            signature.update(data);
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception var) {
            System.out.println("SignUtil.sign error." + var);
            return "";
        }
    }

    /**
     * 签名字符串（SHA256WithRSA）
     *
     * @param content    待签名字符串
     * @param privateKey 私钥
     * @return 字符串的签名
     */
    public static String sign(String content, String privateKey) {
        if (null == content || privateKey == null) {
            return null;
        }
        try {
            return sign(content.getBytes(CHARSET), privateKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Construct parameter strings in the sorted order based on parameter Map.
     *
     * @param params
     * @return
     */
    private static String format(Map<String, Object> params) {
        StringBuffer base = new StringBuffer();
        Map<String, Object> tempMap = new TreeMap<>(params);

        // Obtain the basic string to calculate nsp_key.
        try {
            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue().toString();
                base.append(k).append("=").append(URLEncoder.encode(v, CHARSET)).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encode parameters failed.");
            e.printStackTrace();
        }

        String body = base.toString().substring(0, base.toString().length() - 1);
        // Space and asterisks are escape characters.
        body = body.replaceAll("\\+", "%20").replaceAll("\\*", "%2A");

        return body;
    }

    public static String generateCPSign(Map<String, Object> requestParams, final String cpAuthKey) {
        // Rank the message strings in the alphabetic order and encode the URLCode.
        String baseStr = format(requestParams);

        // Perform signature authentication on the encoded request strings using the CP signature private key.
        String cpSign = sign(baseStr.getBytes(Charset.forName(CHARSET)), cpAuthKey);

        return cpSign;
    }

    public static boolean doCheck(String content, String sign, String publicKey, String signtype) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = null;
            if ("RSA256".equals(signtype)) {
                signature = java.security.Signature.getInstance(SIGN_ALGORITHMS256);
            } else {
                signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            }

            signature.initVerify(pubKey);
            signature.update(content.getBytes(CHARSET));

            boolean bverify = signature.verify(Base64.decodeBase64(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getSignData(Map<String, Object> params) {
        StringBuffer content = new StringBuffer();

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if (PARAM_SIGN.equals(key) || PARAM_SIGN_TYPE.equals(key)) {
                continue;
            }
            String value = (String) params.get(key);
            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                content.append((i == 0 ? "" : "&") + key + "=");
            }

        }
        return content.toString();
    }

    public static String getNoSortSignData(Map<String, Object> params) {
        StringBuffer content = new StringBuffer();

        List<String> keys = new ArrayList<>(params.keySet());

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if (PARAM_SIGN.equals(key)) {
                continue;
            }
            String value = (String) params.get(key);
            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                content.append((i == 0 ? "" : "&") + key + "=");
            }

        }
        return content.toString();
    }


}
