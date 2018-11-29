package com.qinglan.sdk.server.application.platform.log;

import com.qinglan.sdk.server.common.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class PlatformStatsLogger {
    private final static Logger PLATFORM_LOGGER = LoggerFactory.getLogger("platformLogger");

    public static final String UC = "uc";
    public static final String XIAOMI = "xiaomi";
    public static final String QIHOO = "qihoo";
    public static final String ZHIDIAN = "zhidian";
    public static final String BAIDU = "baidu";
    public static final String DOWNJOY = "downjoy";
    public static final String ANZHI = "anzhi";
    public static final String SOUGOU = "sougou";
    public static final String WDJ = "wdj";
    public static final String KUPAI = "kupai";
    public static final String OPPO = "oppo";
    public static final String GIONEE = "gionee";
    public static final String APPCHINA = "appchina";
    public static final String YOUKU = "youku";
    public static final String JIFENG = "jifeng";
    public static final String HTC = "htc";
    public static final String MEIZU = "meizu";
    public static final String NDUO = "nduo";
    public static final String YOULONG = "youlong";
    public static final String LENOVO = "lenovo";
    public static final String VIVO = "vivo";
    public static final String OUWAN = "ouwan";
    public static final String JIUYAO = "91";
    public static final String KUDONG = "kudong";
    public static final String LETV = "letv";
    public static final String YIJIUMENG = "19meng";
    public static final String KUWO = "kuwo";
    public static final String MUMAYI = "mumyayi";
    public static final String PLAY = "play";
    public static final String JIUDU = "jiudu";
    public static final String PAOJIAO = "paojiao";
    public static final String QIXIAZI = "qixiazi";
    public static final String KUAIYONG = "kuaiyong";
    public static final String HUAWEI = "huawei";
    public static final String FTNN = "4399";
    public static final String TS = "37";
    public static final String MUZHI = "muzhi";
    public static final String MUZHIWAN = "muzhiwan";
    public static final String KAOPU = "kaopu";
    public static final String GAMETANZI = "gametanzi";
    public static final String WEIDONG = "weidong";
    public static final String EDG = "edg";
    public static final String TENCENT = "tencent";
    public static final String UUCUN = "uucun";
    public static final String KAIUC = "kaiuc";
    public static final String LIEBAO = "liebao";
    public static final String LESHAN = "07073";
    public static final String ATET = "atet";
    public static final String SHENQI = "2yl";
    public static final String HAIMA = "haima";
    public static final String PENGYOUWAN = "pengyouwan";
    public static final String TENN = "3899";
    public static final String LIULIAN = "liulian";
    public static final String XUNLEI = "xunlei";
    public static final String GUOPAN = "guopan";
    public static final String QXFY = "qxfy";
    public static final String ONGAME = "19game";
    public static final String LONGXIANG = "longxiang";
    public static final String LEHIHI = "lehihi";
    public static final String KOUDAI = "koudai";
    public static final String YOULE = "youle";
    public static final String QIUTU = "qiutu";
    public static final String YUEWAN = "yuewan";
    public static final String Wsx= "wsx";
    public static final String IVERYONE ="iveryone";
    public static final String DYHD = "dyhd";
    public static final String SSTT = "7733";
    public static final String QIQILE = "qiqile";
    public static final String MOGE = "moge";
    public static final String MIGU = "migu";
    public static final String TUU = "tuu";
    public static final String MOYOYO = "moyoyo";
    public static final String DAMAI = "damai";
    public static final String SHUOWAN = "shuowan";
    public static final String FIRSTAPP = "firstapp";
    public static final String QBAO = "qbao";
    public static final String BINGQUBAOWAN = "bingqubaowan";
    public static final String QIPA = "qipa";
    public static final String AIPU = "aipu";
    public static final String SHUNWANG = "shunwang";
    public static final String ZHOUYI = "zhouyi";
    public static final String YUNXIAOTAN = "yunxiaotan";
    public static final String GUANGZHOUPEIDUI = "gzpd";
    public static final String DIANYOO = "dianyoo";
    public static final String CHONGCHONG = "chongchong";
    public static final String QISHI = "qishi";
    public static final String TT = "tt";
    public static final String LEWAN = "lewan";
    public static final String WANKE = "wanke";
    public static final String WXDL = "wxdl";
    public static final String XIAO7 = "xiao7";
    public static final String QUICKSDK = "quicksdk";
    public static final String YIJIESDK = "yijiesdk";
    public static final String KUAIFA = "kuaifa";
    public static final String DAOMEN = "daomen";
    public static final String FTX = "ftx";
    public static final String YIHUAN = "yihuan";
    public static final String HONGSHOUZHI = "hongshouzhi";
    public static final String FANSDK = "fansdk";
    public static final String NIGUANG = "niguang";
    public static final String AOCHUANG = "aochuang";
    public static final String PAPAYOU = "papayou";
    public static final String TAOSHOUYOU = "taoshouyou";
    public static final String MANGGUOWAN = "mangguowan";
    public static final String QINGMU = "qingmu";
    public static final String CHANGQU = "changqu";
    public static final String QITIANLEDI = "qitianledi";
    public static final String CANGLUAN = "cangluan";
    public static final String LINGDONG = "lingdong";
    public static final String ZHIZHUYOU = "zhizhuyou";
    public static final String XINGKONGSHIJIE = "xingkongshijie";
    public static final String MOGUWAN = "moguwan";
    public static final String M2166 = "m2166";
    public static final String SIX7 = "six7";
    public static final String XMW = "xiongmaowan";
    public static final String WUKONG = "wukong";
    public static final String DL = "dangle";
    public static final String DX = "dianxin";
    public static final String JG = "jianguo";
    public static final String BH = "binghu";
    public static final String UUSYZ = "uushouyouzhu";
    public static final String GF = "guangfan";

    public static final void info(String type, String message) {
        PLATFORM_LOGGER.info(type + "|" + message + "|" + DateUtils.format(new Date()));
    }

    public static final void error(String type, String requestMsg, String message) {
        PLATFORM_LOGGER.error(type + "|" + requestMsg + "|" + message + "|" + DateUtils.format(new Date()));
    }

}