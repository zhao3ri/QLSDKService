package com.qinglan.sdk.server.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN;
import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_SIGN_TYPE;


public class Sign {
    private static final Logger logger = Logger.getLogger(Sign.class);
    /**
     * 使用加密算法规则
     */
    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    /**
     * 字符串编码
     */
    private static final String CHARSET = "UTF-8";

    public static String signByMD5(Map<String, Object> params, String secretKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        logger.info(sb.substring(0, sb.length() - 1).concat(secretKey));
        return MD5.encode(sb.substring(0, sb.length() - 1).concat(secretKey));
    }

    public static String signByMD5NoKey(Map<String, Object> params) throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8") + "&");
        }
        System.out.println(sb.substring(0, sb.length() - 1));
        return MD5.encode(sb.substring(0, sb.length() - 1));
    }

    public static String signByMD5KeyPre(Map<String, Object> params, String secretKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        System.out.println(sb.toString());
        return MD5.encode(secretKey.concat(sb.substring(0, sb.length() - 1)));
    }

    public static String signParamsByMD5(Map<String, Object> params, String secretKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
        }
        return MD5.encode(sb.toString().concat(secretKey));
    }


    public static String signParamsByMD5WithKey(Map<String, Object> params, String secretKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return MD5.encode(sb.toString().concat(secretKey));
    }

    public static String signParamsByMD5Linked(LinkedHashMap<String, String> params, String appKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        sb.append(appKey);
        return MD5.encode(sb.toString());
    }


    public static String signByMD5Unsort(Map<String, Object> params, String secretKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return MD5.encode(sb.substring(0, sb.length() - 1).concat(secretKey));
    }

    public static String signByMD5KeySortValNoNull(Map<String, Object> params, String secretKey) throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            list.add(entry.getKey());
        }
        Collections.sort(list);
        //拼接
        StringBuilder sb = new StringBuilder();
        for (String key : list) {
            sb.append(key + "=");
            if (null != params.get(key)) {
                sb.append(URLDecoder.decode(params.get(key).toString(), "utf-8") + "&");
            } else {
                sb.append("&");
            }
        }
        sb.append("appKey=" + secretKey);
        return MD5.encode(sb.toString());
    }

    public static String signByMD5ValNullSkip(Map<String, Object> params, String secretKey) throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            list.add(entry.getKey());
        }
        Collections.sort(list);
        //拼接
        StringBuilder sb = new StringBuilder();
        for (String key : list) {
            if (null != params.get(key)) {
                sb.append(key + "=").append(URLEncoder.encode(params.get(key).toString(), "utf-8") + "&");
            } else {
                continue;
            }
        }
        sb.append("key=" + secretKey);
        String str = sb.toString().replaceAll("\\+", "");
        return MD5.encode(str);
    }

    public static String signByMD5UnsortURLEncode(Map<String, Object> params, String secretKey) throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "utf-8") + "&");
        }
        return MD5.encode(sb.substring(0, sb.length() - 1).concat(secretKey));
    }

    public static String signByMD5AndBASE64(String data, String key) {
        if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key)) {
            return "源串或key为null";
        }

        String sign = "";
        try {
            sign = Base64.encodeBase64String(digestMD5((data + key).getBytes("UTF-8")));
        } catch (Exception e) {
            return "签名异常";
        } finally {
            return sign;
        }
    }

    /**
     * MD5签名
     *
     * @param args 参数
     * @return 签名结果
     */
    public static String encode(Object... args) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(String.format(getFormat(args), filterObject(args)).getBytes("UTF-8"));
            return toHexString(hash);//encodeToString(hash, true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5签名
     *
     * @param params 参数
     * @return 签名结果
     */
    public static String encode(Map<String, String> params) {
        Object[] args = filterObject(params);
        return encode(args);
    }

    /**
     * 签名原字符串过滤,将空串转为null
     *
     * @param args
     * @return
     */
    private static Object[] filterObject(Object... args) {
        int len = args.length;
        Object[] os = new Object[len];
        for (int i = 0; i < len; i++) {
            if (args[i] == null || args[i].equals(""))
                os[i] = null;
            else
                os[i] = args[i];
        }
        return os;
    }

    /**
     * 签名原字符串过滤,将空串转为null
     *
     * @param params
     * @return
     */
    private static Object[] filterObject(Map<String, String> params) {
        int len = params.size();
        Object[] os = new Object[len];
        Iterator<String> iter = params.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            String value = params.get(key);
            if (StringUtils.isBlank(value)) {
                os[i++] = null;
            } else {
                os[i++] = value;
            }
        }
        return os;
    }

    /**
     * 获取格式
     *
     * @param args
     * @return
     */
    private static String getFormat(Object... args) {
        int size = args.length;
        String format = "";
        for (int i = 0; i < size; i++) {
            format += "%s";
        }
        return format;
    }

    /**
     * 获取格式
     *
     * @param params
     * @return
     */
    private static String getFormat(Map<String, String> params) {
        String format = "";
        Iterator<String> iter = params.keySet().iterator();
        while (iter.hasNext()) {
            format += "%s";
        }
        return format;
    }

    /**
     * 转为十六进制
     *
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes) {
        String result = "";
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            result += Integer.toHexString((0x000000ff & bytes[i]) | 0xffffff00).substring(6);
        }
        return result;
    }

    public static String encodeBASE64(byte[] key) {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    public static byte[] digestMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    /**
     * 签名工具方法
     *
     * @param reqMap
     * @return
     */
    public static String aliSign(Map<String, String> reqMap, String signKey) {
        //将所有key按照字典顺序排序
        TreeMap<String, String> signMap = new TreeMap<String, String>(reqMap);
        StringBuilder stringBuilder = new StringBuilder(1024);
        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            // sign和signType不参与签名
            if (RESPONSE_KEY_SIGN.equals(entry.getKey()) || RESPONSE_KEY_SIGN_TYPE.equals(entry.getKey())) {
                continue;
            }
            //值为null的参数不参与签名
            if (entry.getValue() != null) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        //拼接签名秘钥
        stringBuilder.append(signKey);
        //剔除参数中含有的'&'符号
        String signSrc = stringBuilder.toString().replaceAll("&", "");
        return MD5.encode(signSrc).toLowerCase();
    }

    public static void main(String[] args) {
		/*Map<String, Object> map = new HashMap<String, Object>();
		map.put("b", "ccccc");
        map.put("ad", "aaaaa");
        map.put("ah", "bbbbb");
        map.put("am", "ddddd");
        System.out.println(signByMD5(map, "0cf59d8dda3968186b982d107154b34b"));*/
        System.out.println(Sign.encode("91SDK"));
    }
}
