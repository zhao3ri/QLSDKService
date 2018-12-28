package com.qinglan.sdk.server.channel.utils;

import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HuoSdkSignHelper {
    private static final String ALGORITHM = "MD5";

    public static String sign(String appId, String memId, String userToken, String appKey) {
        String signStr = String.format("app_id%s&mem_id%s&user_token%s&app_key%s", appId, memId, userToken, appKey);
        String sign = md5(signStr);
        return sign;
    }

    public static String md5(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffers = md.digest(content.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buffers.length; i++) {
                String s = Integer.toHexString(0xff & buffers[i]);
                if (s.length() == 1) {
                    sb.append("0" + s);
                }
                if (s.length() != 1) {
                    sb.append(s);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static char sHexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static MessageDigest sDigest;

    static {
        try {
            sDigest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
//            Log.e(LOG_TAG, "Get MD5 Digest failed.");
            throw new UnsupportedDigestAlgorithmException(ALGORITHM, e);
        }
    }
}
