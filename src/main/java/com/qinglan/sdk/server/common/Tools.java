package com.qinglan.sdk.server.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 描述：基础操作函数集合
 */
public class Tools {
    public Tools() {
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

    /**
     * 将二维数组转化为一维数组
     *
     * @param arraySenS
     * @return
     */
    public static byte[] StringToByte(byte[][] arraySenS) {
        int i, dab = 0;
        for (i = 0; i < arraySenS.length; i++) {
            if (arraySenS[i] == null) {
                return null;
            }
            dab = dab + arraySenS[i].length;
        }
        List<Byte> listByte = new ArrayList<Byte>();
        int j;
        for (i = 0; i < arraySenS.length; i++) {
            for (j = 0; j < arraySenS[i].length; j++) {
                if (arraySenS[i][j] != ' ') {
                    listByte.add(arraySenS[i][j]);
                }
            }
        }
        Byte[] arrayByte = listByte.toArray(new Byte[0]);
        byte[] result = new byte[arrayByte.length];
        for (int k = 0; k < arrayByte.length; k++) {
            result[k] = (arrayByte[k]).byteValue();
        }
        listByte = null;
        arrayByte = null;
        return result;
    }

}

