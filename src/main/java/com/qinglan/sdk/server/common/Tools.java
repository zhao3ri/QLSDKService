package com.qinglan.sdk.server.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 描述：基础操作函数集合
 */
public class Tools {
    public Tools() {
    }

    /**
     * 检查字符串是否为空；如果字符串为null,或空串，或全为空格，返回true;否则返回false
     *
     * @param str
     * @return
     */
    public static boolean isStrEmpty(String str) {
        if ((str != null) && (str.trim().length() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 去除字符串的前后空格；如果字符串为null,返回空串;
     *
     * @param str 输入字符串
     * @return 处理的后字符串
     */
    public static String ruleStr(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    /**
     * 字符串转码，把GBK转ISO-8859-1
     *
     * @param str GBK编码的字符串
     * @return ISO-8859-1编码的字符串
     */
    public static String GBK2Unicode(String str) {
        try {
            str = new String(str.getBytes("GBK"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
        }
        ;
        return str;
    }

    /**
     * 字符串转码，把GBK转ISO-8859-1
     *
     * @param str ISO-8859-1编码的字符串
     * @return GBK编码的字符串
     */
    public static String Unicode2GBK(String str) {
        try {
            str = new String(str.getBytes("ISO-8859-1"), "GBK");
        } catch (java.io.UnsupportedEncodingException e) {
        }
        ;
        return str;
    }

    /**
     * 以字符串的格式取系统时间;格式：YYYYMMDDHHMMSS
     *
     * @return 时间字符串
     */
    public static String getSysTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new java.util.Date());
    }

    /**
     * 以字符串的格式取系统日期;格式：YYYYMMDD
     *
     * @return 日期字符串
     */
    public static String getSysDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(new java.util.Date());
    }

    /**
     * 按输入的时间格式获取时间串
     *
     * @param format ： 时间的格式 ， 如：yyyy-MM-dd HH:mm:ss ， yyyyMMddHHmmss
     * @return ： 时间字符串
     */
    public static String getSysTimeFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new java.util.Date());
    }

    /**
     * 判断字符串是否是有效的日期字符
     *
     * @param d      需要判断的日期字符串
     * @param format java日期格式 如：yyyy-MM-dd HH:mm:ss ， yyyyMMddHHmmss
     * @return true:有效日期 false：无效日期
     */
    public static boolean isDay(String d, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(d);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 检查字符串是否表示金额，此金额小数点后最多带2位
     *
     * @param amount 需要被检查的字符串
     * @return ： true－表示金额，false-不表示金额
     */
    public static boolean checkAmount(String amount) {
        if (amount == null) {
            return false;
        }
        String checkExpressions;
        checkExpressions = "^([1-9]\\d*|[0])\\.\\d{1,2}$|^[1-9]\\d*$|^0$";
        return Pattern.matches(checkExpressions, amount);
    }

    /**
     * 获取XML报文元素，只支持单层的XML，若是存在嵌套重复的元素，只返回开始第一个
     *
     * @param srcXML  ： xml串
     * @param element ： 元素
     * @return ： 元素对应的值
     */
    public static String getXMLValue(String srcXML, String element) {
        String ret = "";
        try {
            String begElement = "<" + element + ">";
            String endElement = "</" + element + ">";
            int begPos = srcXML.indexOf(begElement);
            int endPos = srcXML.indexOf(endElement);
            if (begPos != -1 && endPos != -1 && begPos <= endPos) {
                begPos += begElement.length();
                ret = srcXML.substring(begPos, endPos);
            } else {
                ret = "";
            }
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) throws Exception {
        if (map == null)
            return null;

        T t = beanClass.newInstance();

        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }

            field.setAccessible(true);
            field.set(t, map.get(field.getName()));
        }

        return t;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }

    public static Map<String, Object> getMapByParams(String content, boolean allowEmpty, String... filter) {
        Map<String, Object> map = new HashMap<>();
        List<String> filterList = null;
        if (filter != null) {
            filterList = Arrays.asList(filter);
        }
        String[] params = content.split("&");
        for (String param : params) {
            int index = param.indexOf("=");
            if (index < 0) {
                continue;
            }
            String key = param.substring(0, index);
            if (isStrEmpty(key)) {
                continue;
            }
            if (!isEmptyCollection(filterList)) {
                if (filterList.contains(key))
                    continue;
            }
            String value = param.substring(index + 1);
            if (isStrEmpty(value)) {
                if (!allowEmpty) {
                    continue;
                }
                map.put(key, "");
            }
            map.put(key, value);
        }
        return map;
    }

    public static boolean isEmptyCollection(Collection c) {
        if (null == c || c.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String getParamsString(Map<String, Object> params) {
        Iterator<Map.Entry<String, Object>> entries = params.entrySet().iterator();
        StringBuffer sb = new StringBuffer();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey() + "=" + entry.getValue());
            if (entries.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String content = "amount=100&channelId=1009&clientType=1&goodsId=1607041649533883&notifyUrl=";
        Map<String, Object> map = new TreeMap<>(getMapByParams(content, true, "notifyUrl"));
        System.out.println(map);
        System.out.println(getParamsString(map));
    }

}

