package com.qinglan.sdk.server.platform.utils;

import com.qinglan.sdk.server.common.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class PlatformStatsLogger {
    private final static Logger PLATFORM_LOGGER = LoggerFactory.getLogger("platformLogger");

    public static String UC = "uc";
    public static String XIAOMI = "xiaomi";
    public static String QIHOO = "qihoo";
    public static String ZHIDIAN = "zhidian";
    public static String BAIDU = "baidu";
    public static String DOWNJOY = "downjoy";
    public static String ANZHI = "anzhi";
    public static String SOUGOU = "sougou";
    public static String WDJ = "wdj";
    public static String KUPAI = "kupai";
    public static String OPPO = "oppo";
    public static String GIONEE = "gionee";
    public static String APPCHINA = "appchina";
    public static String YOUKU = "youku";
    public static String JIFENG = "jifeng";
    public static String HTC = "htc";
    public static String MEIZU = "meizu";
    public static String NDUO = "nduo";
    public static String YOULONG = "youlong";
    public static String LENOVO = "lenovo";
    public static String VIVO = "vivo";
    public static String OUWAN = "ouwan";
    public static String JIUYAO = "91";
    public static String KUDONG = "kudong";
    public static String LETV = "letv";
    public static String YIJIUMENG = "19meng";
    public static String KUWO = "kuwo";
    public static String MUMAYI = "mumyayi";
    public static String PLAY = "play";
    public static String JIUDU = "jiudu";
    public static String PAOJIAO = "paojiao";
    public static String QIXIAZI = "qixiazi";
    public static String KUAIYONG = "kuaiyong";
    public static String HUAWEI = "huawei";
    public static String FTNN = "4399";
    public static String TS = "37";
    public static String MUZHI = "muzhi";
    public static String MUZHIWAN = "muzhiwan";
    public static String KAOPU = "kaopu";
    public static String GAMETANZI = "gametanzi";
    public static String WEIDONG = "weidong";
    public static String EDG = "edg";
    public static String TENCENT = "tencent";
    public static String UUCUN = "uucun";
    public static String KAIUC = "kaiuc";
    public static String LIEBAO = "liebao";
    public static String LESHAN = "07073";
    public static String ATET = "atet";
    public static String SHENQI = "2yl";
    public static String HAIMA = "haima";
    public static String PENGYOUWAN = "pengyouwan";
    public static String TENN = "3899";
    public static String LIULIAN = "liulian";
    public static String XUNLEI = "xunlei";
    public static String GUOPAN = "guopan";
    public static String QXFY = "qxfy";
    public static String ONGAME = "19game";
    public static String LONGXIANG = "longxiang";
    public static String LEHIHI = "lehihi";
    public static String KOUDAI = "koudai";
    public static String YOULE = "youle";
    public static String QIUTU = "qiutu";
    public static String YUEWAN = "yuewan";
    public static String Wsx= "wsx";
    public static String IVERYONE ="iveryone";
    public static String DYHD = "dyhd";
    public static String SSTT = "7733";
    public static String QIQILE = "qiqile";
    public static String MOGE = "moge";
    public static String MIGU = "migu";
    public static String TUU = "tuu";
    public static String MOYOYO = "moyoyo";
    public static String DAMAI = "damai";
    public static String SHUOWAN = "shuowan";
    public static String FIRSTAPP = "firstapp";
    public static String QBAO = "qbao";
    public static String BINGQUBAOWAN = "bingqubaowan";
    public static String QIPA = "qipa";
    public static String AIPU = "aipu";
    public static String SHUNWANG = "shunwang";
    public static String ZHOUYI = "zhouyi";
    public static String YUNXIAOTAN = "yunxiaotan";
    public static String GUANGZHOUPEIDUI = "gzpd";
    public static String DIANYOO = "dianyoo";
    public static String CHONGCHONG = "chongchong";
    public static String QISHI = "qishi";
    public static String TT = "tt";
    public static String LEWAN = "lewan";
    public static String WANKE = "wanke";
    public static String WXDL = "wxdl";
    public static String XIAO7 = "xiao7";
    public static String QUICKSDK = "quicksdk";
    public static String YIJIESDK = "yijiesdk";
    public static String KUAIFA = "kuaifa";
    public static String DAOMEN = "daomen";
    public static String FTX = "ftx";
    public static String YIHUAN = "yihuan";
    public static String HONGSHOUZHI = "hongshouzhi";
    public static String FANSDK = "fansdk";
    public static String NIGUANG = "niguang";
    public static String AOCHUANG = "aochuang";
    public static String PAPAYOU = "papayou";
    public static String TAOSHOUYOU = "taoshouyou";
    public static String MANGGUOWAN = "mangguowan";
    public static String QINGMU = "qingmu";
    public static String CHANGQU = "changqu";
    public static String QITIANLEDI = "qitianledi";
    public static String CANGLUAN = "cangluan";
    public static String LINGDONG = "lingdong";
    public static String ZHIZHUYOU = "zhizhuyou";
    public static String XINGKONGSHIJIE = "xingkongshijie";
    public static String MOGUWAN = "moguwan";
    public static String M2166 = "m2166";
    public static String SIX7 = "six7";
    public static String XMW = "xiongmaowan";
    public static String WUKONG = "wukong";
    public static String DL = "dangle";
    public static String DX = "dianxin";
    public static String JG = "jianguo";
    public static String BH = "binghu";
    public static String UUSYZ = "uushouyouzhu";
    public static String GF = "guangfan";

    public static void info(String type, String message) {
        PLATFORM_LOGGER.info(type + "|" + message + "|" + DateUtils.format(new Date()));
    }

    public static void error(String type, String requestMsg, String message) {
        PLATFORM_LOGGER.error(type + "|" + requestMsg + "|" + message + "|" + DateUtils.format(new Date()));
    }

}