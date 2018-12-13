package com.qinglan.sdk.server.presentation;

import com.qinglan.sdk.server.application.ChannelService;
import com.qinglan.sdk.server.application.ChannelServicePartTwo;
import com.qinglan.sdk.server.domain.platform.YaoyueCallback;
import com.qinglan.sdk.server.domain.platform.YouleCallback;
import com.qinglan.sdk.server.presentation.channel.entity.UCVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.impl.UCChannel;
import com.qinglan.sdk.server.presentation.dto.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/channel")
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Resource
    private ChannelService channelService;

    @Resource
    private ChannelServicePartTwo channelServicePartTwo;

    /**
     * yaoyue 支付结果回调
     *
     * @param zhidian
     * @return
     */
    @RequestMapping(value = "/yaoyue/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String zhidian(YaoyueCallback zhidian) {
        return channelService.verifyYaoyue(zhidian);
    }

    /**
     * UC session验证
     *
     * @param ucGameSession
     * @return
     */
    @RequestMapping(value = UCChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ucGameSession(UCVerifyRequest ucGameSession) {
        logger.debug(ucGameSession.toString());
        return channelService.verifyUcSession(ucGameSession);
    }

    /**
     * UC 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(UCChannel.PAY_RETURN_URL)
    @ResponseBody
    public String ucPayResult(HttpServletRequest request) {
        return channelService.ucPayReturn(request);
    }

    /**
     * 小米 session验证
     *
     * @return
     */
    @RequestMapping(value = "/xiaomi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String xiaomiSession(XiaomiSession xiaomiSession) {
        logger.debug(xiaomiSession.toString());
        return channelService.verifyXiaomiSession(xiaomiSession);
    }

    /**
     * 小米 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/xiaomi/pay")
    @ResponseBody
    public String xiaomi(HttpServletRequest request) {
        return channelService.verifyXiaomi(request);
    }


    /**
     * 360 session验证
     *
     * @param qihooSession
     * @return
     */
    @RequestMapping(value = "/qihoo/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String qihooSession(QihooSession qihooSession) {
        logger.debug(qihooSession.toString());
        return channelService.verifyQihooSession(qihooSession);
    }

    /**
     * 360 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/qihoo/pay")
    @ResponseBody
    public String qihoo(HttpServletRequest request) {
        return channelService.verifyQihoo(request);
    }


    /**
     * 百度 session验证
     *
     * @param baiduSession
     * @return
     */
    @RequestMapping(value = "/baidu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String baiduSession(BaiduSession baiduSession) {
        logger.debug(baiduSession.toString());
        return channelService.verifyBaiduSession(baiduSession);
    }

    /**
     * 百度 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/baidu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String baidu(HttpServletRequest request) {
        return channelService.verifyBaidu(request);
    }

    /**
     * 安智 session验证
     *
     * @param anzhiSession
     * @return
     */
    @RequestMapping(value = "/anzhi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String anzhiSession(AnzhiSession anzhiSession) {
        logger.debug(anzhiSession.toString());
        return channelService.verifyAnzhiSession(anzhiSession);
    }

    /**
     * 安智支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/anzhi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String anzhi(HttpServletRequest request) {
        return channelService.verifyAnzhi(request);
    }

    /**
     * 豌豆荚 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/wdj/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyWdjSession(WdjSession session) {
        logger.debug(session.toString());
        return channelService.verifyWdjSession(session);
    }

    /**
     * 豌豆荚支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wdj/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackWdj(HttpServletRequest request) {
        return channelService.verifyWdj(request);
    }

    /**
     * 当乐 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/downjoy/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackDownjoy(HttpServletRequest request) {
        return channelService.verifyDownjoy(request);
    }

    /**
     * 搜狗 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/sougou/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String sougouSession(SougouSession sougouSession) {
        logger.debug(sougouSession.toString());
        return channelService.verifySougouSession(sougouSession);
    }

    /**
     * 搜狗支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/sougou/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String sougou(HttpServletRequest request) {
        return channelService.verifySougou(request);
    }

    /**
     * 酷派 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/kupai/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyKupaiSession(KupaiSession session) {
        logger.debug(session.toString());
        return channelService.verifyKupaiSession(session);
    }

    /**
     * 酷派 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kupai/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKupai(HttpServletRequest request) {
        return channelService.verifyKupai(request);
    }

    /**
     * oppo session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/oppo/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyOppoSession(OppoSession session) {
        logger.debug(session.toString());
        return channelService.verifyOppoSession(session);
    }

    /**
     * oppo 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/oppo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackOppo(HttpServletRequest request) {
        logger.debug(request.getParameterMap().toString());
        return channelService.verifyOppo(request);
    }

    /**
     * 金立 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/gionee/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyGioneeSession(GioneeSession session) {
        logger.debug(session.toString());
        return channelService.verifyGioneeSession(session);
    }

    /**
     * 金立 创建订单
     *
     * @return
     */
    @RequestMapping(value = "/gionee/order/create", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String gioneeOrderCreate(HttpServletRequest request) {
        return channelService.gioneeOrderCreate(request);
    }

    /**
     * 金立支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/gionee/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackGionee(HttpServletRequest request) {
        return channelService.verifyGionee(request);
    }


    /**
     * 91 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/91/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verify91Session(Varify91Session session) {
        logger.debug(session.toString());
        return channelService.verify91Session(session);
    }

    /**
     * 91支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/91/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBack91(HttpServletRequest request) {
        logger.debug(request.getParameterMap().toString());
        return channelService.verify91(request);
    }

    /**
     * vivo session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/vivo/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyVivoSession(VivoSession session) {
        logger.debug(session.toString());
        return channelService.verifyVivoSession(session);
    }

    /**
     * vivo 订单推送
     *
     * @param vivoPaySign
     * @return
     */
    @RequestMapping(value = "/vivo/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String vivoPaySign(VivoPaySign vivoPaySign) {
        return channelService.vivoPaySign(vivoPaySign);
    }

    /**
     * vivo支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vivo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackVivo(HttpServletRequest request) {
        return channelService.verifyVivo(request);
    }

    /**
     * 应用汇 session验证
     *
     * @param vivoSession
     * @return
     */
    @RequestMapping(value = "/appchina/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyAppchinaSession(AppchinaSession session) {
        logger.debug(session.toString());
        return channelService.verifyAppchinaSession(session);
    }

    /**
     * 应用汇 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/appchina/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackAppchina(HttpServletRequest request) {
        return channelService.verifyAppchina(request);
    }

    /**
     * 偶玩 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/ouwan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyOuwanSession(OuwanSession session) {
        logger.debug(session.toString());
        return channelService.verifyOuwanSession(session);
    }

    /**
     * 偶玩 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/ouwan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackOuwan(HttpServletRequest request) {
        return channelService.verifyOuwan(request);
    }

    /**
     * 优酷 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/youku/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYoukuSession(YoukuSession session) {
        return channelService.verifyYoukuSession(session);
    }

    /**
     * 优酷 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/youku", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackYouku(HttpServletRequest request) {
        return channelService.verifyYouku(request);
    }

    /**
     * 机锋 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/jifeng/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyJifengSession(JifengSession session) {
        return channelService.verifyJifengSession(session);
    }

    /**
     * 机锋 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/jifeng/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackJifeng(HttpServletRequest request) {
        return channelService.verifyJifeng(request);
    }

    /**
     * HTC 支付内容加密
     *
     * @return
     */
    @RequestMapping(value = "/htc/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String HTCPaySign(HttpServletRequest request) {
        return channelService.signHTCPayContent(request);
    }

    /**
     * HTC验证
     *
     * @return
     */
    @RequestMapping(value = "/htc/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyHtcSession(HTCSession session) {
        return channelService.verifyHTCSession(session);
    }

    /**
     * HTC 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/htc/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackHTC(HttpServletRequest request) {
        return channelService.verifyHTC(request);
    }

    /**
     * 魅族 session验证
     *
     * @return
     */
    @RequestMapping(value = "/meizu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMeizuSession(MeizuSession session) {
        return channelService.verifyMeizuSession(session);
    }

    /**
     * 魅族 支付参数签名
     *
     * @return
     */
    @RequestMapping(value = "/meizu/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String meizuPaySign(HttpServletRequest request) {
        return channelService.meizuPaySign(request);
    }

    /**
     * 魅族 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/meizu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMeizu(HttpServletRequest request) {
        return channelService.verifyMeizu(request);
    }

    /**
     * n多网 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/nduo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackNduo(HttpServletRequest request) {
        return channelService.verifyNduo(request);
    }

    /**
     * 游龙 session验证
     *
     * @return
     */
    @RequestMapping(value = "/yl/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYoulongSession(YoulongSession session) {
        return channelService.verifyYoulongSession(session);
    }

    /**
     * 游龙 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yl/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackYoulong(HttpServletRequest request) {
        return channelService.verifyYoulong(request);
    }

    /**
     * 联想 session验证
     *
     * @return
     */
    @RequestMapping(value = "/lenovo/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLenovoSession(LenovoSession session) {
        return channelService.verifyLenovoSession(session);
    }

    /**
     * 联想 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lenovo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLenovo(HttpServletRequest request) {
        return channelService.verifyLenovo(request);
    }

    /**
     * 酷动 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kudong/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKudong(HttpServletRequest request) {
        return channelService.verifyKudong(request);
    }

    /**
     * 乐视 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/letv/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLetv(HttpServletRequest request) {
        return channelService.verifyLetv(request);
    }

    /**
     * 手盟 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/19meng/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBack19meng(HttpServletRequest request) {
        return channelService.verify19meng(request);
    }

    /**
     * 酷我 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kuwo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKuwo(HttpServletRequest request) {
        return channelService.verifyKuwo(request);
    }

    /**
     * 木蚂蚁 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mumayi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMumayi(HttpServletRequest request) {
        return channelService.verifyMumayi(request);
    }

    /**
     * 电信爱游戏 session验证
     *
     * @return
     */
    @RequestMapping(value = "/play/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyPlaySession(PlaySession session) {
        return channelService.verifyPlaySession(session);
    }

    /**
     * 电信爱游戏 短信支付确认
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/playSms/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargePalySms(HttpServletRequest request) {
        return channelService.verifyPlaySms(request);
    }

    /**
     * 电信爱游戏 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/play/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackPaly(HttpServletRequest request) {
        return channelService.verifyPlay(request);
    }

    /**
     * 玖度科技爱上游戏 session验证
     *
     * @return
     */
    @RequestMapping(value = "/jiudu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyJiuduSession(JiuduSession session) {
        return channelService.verifyJiuduSession(session);
    }

    /**
     * 玖度科技爱上游戏 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/jiudu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackJiudu(HttpServletRequest request) {
        return channelService.verifyJiudu(request);
    }

    /**
     * 泡椒 session验证
     *
     * @return
     */
    @RequestMapping(value = "/paojiao/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyPaojiaoSession(PaojiaoSession session) {
        return channelService.verifyPaojiaoSession(session);
    }

    /**
     * 泡椒 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/paojiao/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackPaojiao(HttpServletRequest request) {
        return channelService.verifyPaojiao(request);
    }

    /**
     * 七匣子 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qixiazi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackQixiazi(HttpServletRequest request) {
        return channelService.verifyQixiazi(request);
    }

    /**
     * 快用session验证
     *
     * @return
     */
    @RequestMapping(value = "/kuaiyong/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyKuaiyongSession(KuaiyongSession session) {
        return channelService.verifyKuaiyongSession(session);
    }

    /**
     * 快用支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kuaiyong/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKuaiyong(HttpServletRequest request) {
        return channelService.verifyKuaiyong(request);
    }

    /**
     * 华为 session验证
     *
     * @return
     */
    @RequestMapping(value = "/huawei/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyHuaweiSession(HuaweiSession session) {
        return channelService.verifyHuaweiSession(session);
    }

    /**
     * 华为支付参数签名
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/huawei/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String hauweiPaySign(HttpServletRequest request) {

        return channelService.huaweiPaySign(request);
    }

    /**
     * 华为支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/huawei/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackHuawei(HttpServletRequest request) {
        return channelService.verifyHuawei(request);
    }

    /**
     * 4399 session验证
     *
     * @return
     */
    @RequestMapping(value = "/4399/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyFtnnSession(FtnnSession session) {
        return channelService.verifyFtnnSession(session);
    }

    /**
     * 4399支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/4399/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackFtnn(HttpServletRequest request) {
        return channelService.verifyFtnn(request);
    }

    /**
     * 37 session验证
     *
     * @return
     */
    @RequestMapping(value = "/37/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyTsnnSession(TsSession session) {
        return channelService.verifyTsSession(session);
    }

    /**
     * 37 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/37/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackTs(HttpServletRequest request) {
        return channelService.verifyTs(request);
    }

    /**
     * 拇指游玩 session验证
     *
     * @return
     */
    @RequestMapping(value = "/muzhi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMuzhiSession(MuzhiSession session) {
        return channelService.verifyMuzhiSession(session);
    }

    /**
     * 拇指游玩 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/muzhi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMuzhi(HttpServletRequest request) {
        return channelService.verifyMuzhi(request);
    }

    /**
     * 拇指玩 session验证
     *
     * @return
     */
    @RequestMapping(value = "/muzhiwan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMuzhiwanSession(MuzhiwanSession session) {
        return channelService.verifyMuzhiwanSession(session);
    }

    /**
     * 拇指玩 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/muzhiwan", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMuzhiwan(HttpServletRequest request) {
        return channelService.verifyMuzhiwan(request);
    }

    /**
     * 靠谱 session验证
     *
     * @return
     */
    @RequestMapping(value = "/kaopu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyKaopuSession(KaopuSession session) {
        return channelService.verifyKaopuSession(session);
    }

    /**
     * 靠谱 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kaopu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKaopu(HttpServletRequest request) {
        return channelService.verifyKaopu(request);
    }

    /**
     * 游戏坛子 session验证
     *
     * @return
     */
    @RequestMapping(value = "/gametanzi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyGametanziSession(GametanziSession session) {
        return channelService.verifyGametanziSession(session);
    }

    /**
     * 游戏坛子 支付结果回调
     *
     * @return
     */
    @RequestMapping(value = "/gametanzi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackGametanzi(HttpServletRequest request) {
        return channelService.verifyGametanzi(request);
    }

    /**
     * 维动 session验证
     *
     * @return
     */
    @RequestMapping(value = "/weidong/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyWeidongSession(WeidongSession session) {
        return channelService.verifyWeidongSession(session);
    }

    /**
     * 维动 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/weidong/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackWeidong(HttpServletRequest request) {
        return channelService.verifyWeidong(request);
    }

    /**
     * 乐非凡 session验证
     *
     * @return
     */
    @RequestMapping(value = "/edg/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyEdgSession(EdgSession session) {
        return channelService.verifyEdgSession(session);
    }

    /**
     * 乐非凡 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edg/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackEdg(HttpServletRequest request) {
        return channelService.verifyEdg(request);
    }

    /**
     * 应用宝 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/tencent/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackTencent(HttpServletRequest request) {

        return channelService.verifyTencent(request);
    }

    /**
     * 应用宝 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/tencent2/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackTencent2(HttpServletRequest request) {

        return channelService.verifyTencent2(request);
    }

    /**
     * UU村 session验证
     *
     * @return
     */
    @RequestMapping(value = "/uucun/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyUucunSession(UucunSession session) {
        return channelService.verifyUucunSession(session);
    }

    /**
     * UU村 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uucun/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackUucun(HttpServletRequest request) {
        return channelService.verifyUucun(request);
    }

    /**
     * 联旭开游 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kaiuc/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKaiuc(HttpServletRequest request) {
        return channelService.verifyKaiuc(request);
    }

    /**
     * 猎宝 session验证
     *
     * @return
     */
    @RequestMapping(value = "/liebao/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLiebaoSession(LiebaoSession session) {
        return channelService.verifyLiebaoSession(session);
    }

    /**
     * 猎宝 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/liebao/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLiebao(HttpServletRequest request) {
        return channelService.verifyLiebao(request);
    }

    /**
     * 07073 session验证
     *
     * @return
     */
    @RequestMapping(value = "/07073/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLeshanSession(LeshanSession session) {
        return channelService.verifyLeshanSession(session);
    }

    /**
     * 07073 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/07073/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLeshan(HttpServletRequest request) {
        return channelService.verifyLeshan(request);
    }

    /**
     * 时讯 支付点查询
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/atet/paypoint", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String atetPaypoing(HttpServletRequest request) {
        return channelService.atetPaypoing(request);
    }

    /**
     * 时讯 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/atet/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackAtet(HttpServletRequest request) {
        return channelService.verifyAtet(request);
    }

    /**
     * 神起爱娱乐 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/2yl/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackShenqi(HttpServletRequest request) {
        return channelService.verifyShenqi(request);
    }

    /**
     * 海马 session验证
     *
     * @return
     */
    @RequestMapping(value = "/haima/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyHaimaSession(HaimaSession session) {
        logger.debug(session.toString());
        return channelService.verifyHaimaSession(session);
    }

    /**
     * 07073 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/haima/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackHaima(HttpServletRequest request) {
        return channelService.verifyHaima(request);
    }

    /**
     * 朋友玩支付点查询
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/pengyouwan/paypoint", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String pengyouwanPaypoing(HttpServletRequest request) {
        return channelService.pengyouwanPaypoing(request);
    }

    /**
     * 朋友玩支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/pengyouwan", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackPengyouwan(HttpServletRequest request) {
        return channelService.verifyPengyouwan(request);
    }

    /**
     * 3899 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/3899/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verify3899Session(TennSession session) {
        logger.debug(session.toString());
        return channelService.verify3899Session(session);
    }

    /**
     * 3899支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/3899/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBack3899(HttpServletRequest request) {
        return channelService.verify3899(request);
    }

    /**
     * 榴莲 session验证
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/liulian/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLiulianSession(LiulianSession session) {
        logger.debug(session.toString());
        return channelService.verifyLiulianSession(session);
    }

    /**
     * 榴莲 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/liulian/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLiulian(HttpServletRequest request) {
        return channelService.verifyLiulian(request);
    }

    /**
     * 迅雷支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xunlei/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackXunlei(HttpServletRequest request) {
        return channelService.verifyXunlei(request);
    }

    /**
     * 果盘 session验证
     *
     * @return
     */
    @RequestMapping(value = "/guopan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyGuopanSession(GuopanSession session) {
        logger.debug(session.toString());
        return channelService.verifyGuopanSession(session);
    }

    /**
     * 果盘支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/guopan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackGuopan(HttpServletRequest request) {
        return channelService.verifyGuopan(request);
    }

    /**
     * 群兴飞阳 session验证
     *
     * @return
     */
    @RequestMapping(value = "/qxfy/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQxfySession(QxfySession session) {
        logger.debug(session.toString());
        return channelService.verifyQxfySession(session);
    }

    /**
     * 群兴飞阳支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qxfy/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackQxfy(HttpServletRequest request) {
        return channelService.verifyQxfy(request);
    }

    /**
     * 19游戏支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/19game/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBack19game(HttpServletRequest request) {
        return channelService.verify19game(request);
    }

    /**
     * 龙翔 session验证
     *
     * @return
     */
    @RequestMapping(value = "/longxiang/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLongxiangSession(LongxiangSession session) {
        logger.debug(session.toString());
        return channelService.verifyLongxiangSession(session);
    }

    /**
     * 龙翔游戏支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/longxiang/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLongxiang(HttpServletRequest request) {
        return channelService.verifyLongxiang(request);
    }

    /**
     * 乐嗨嗨 session验证
     *
     * @return
     */
    @RequestMapping(value = "/lehihi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLehihiSession(LehihiSession session) {
        logger.debug(session.toString());
        return channelService.verifyLehihiSession(session);
    }

    /**
     * 乐嗨嗨 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lehihi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackLehihi(HttpServletRequest request) {
        return channelService.verifyLehihi(request);
    }


    /**
     * 口袋 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/koudai", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackKoudai(HttpServletRequest request) {
        return channelService.verifyKoudai(request);
    }

    /**
     * 游乐SDK2.0 session验证
     *
     * @return
     */
    @RequestMapping(value = "/youle/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYouleSession(YouleSession session) {
        logger.debug(session.toString());
        return channelService.verifyYouleSession(session);
    }

    /**
     * 游乐SDK2.0  支付结果回调
     *
     * @return
     */
    @RequestMapping(value = "/youle/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackYoule(YouleCallback callback) {
        return channelService.verifyYoule(callback);
    }

    /**
     * 逑途 session验证
     *
     * @return
     */
    @RequestMapping(value = "/qiutu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQiutuSession(QiutuSession session) {
        logger.debug(session.toString());
        return channelService.verifyQiutuSession(session);
    }

    /**
     * 逑途  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qiutu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackQiutu(HttpServletRequest request) {
        return channelService.verifyQiutu(request);
    }

    /**
     * 悦玩 session验证
     *
     * @return
     */
    @RequestMapping(value = "/yuewan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYuewanSession(YuewanSession session) {
        logger.debug(session.toString());
        return channelService.verifyYuewanSession(session);
    }

    /**
     * 悦玩  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yuewan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackYuewan(HttpServletRequest request) {
        return channelService.verifyYuewan(request);
    }

    /**
     * 无属性  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wsx/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackWsx(HttpServletRequest request) {
        logger.info("rechargeCallBackWsx----------------");
        return channelService.verifyWsx(request);
    }

    /**
     * Iveryone session验证
     *
     * @return
     */
    @RequestMapping(value = "/iveryone/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyiVeryoneSession(IveryoneSession session) {
        logger.debug(session.toString());
        return channelService.verifyIveryoneSession(session);
    }

    /**
     * Iveryone  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/iveryone/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackIveryone(HttpServletRequest request) {
        return channelService.verifyIveryone(request);
    }


    /**
     * 多元互动 session验证
     *
     * @return
     */
    @RequestMapping(value = "/dyhd/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDyhdSession(DyhdSession session) {
        logger.debug(session.toString());
        return channelService.verifyDyhdSession(session);
    }

    /**
     * 多元互动  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/dyhd/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackDyhd(HttpServletRequest request) {
        return channelService.verifyDyhd(request);
    }

    /**
     * 齐齐乐 session验证
     *
     * @return
     */
    @RequestMapping(value = "/qiqile/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQiqileSession(QiqileSession session) {
        logger.debug(session.toString());
        return channelService.verifyQiqileSession(session);
    }

    /**
     * 齐齐乐  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qiqile/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackQiqile(HttpServletRequest request) {
        return channelService.verifyQiqile(request);
    }

    /**
     * 7723 session验证
     *
     * @return
     */
    @RequestMapping(value = "/7723/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verify7723Session(SsttSession session) {
        logger.debug(session.toString());
        return channelService.verify7723Session(session);
    }

    /**
     * 7723  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/7723/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBack7723(HttpServletRequest request) {
        return channelService.verify7723(request);
    }

    /**
     * 摩格 session验证
     *
     * @return
     */
    @RequestMapping(value = "/moge/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMogeSession(MogeSession session) {
        logger.debug(session.toString());
        return channelService.verifyMogeSession(session);
    }

    /**
     * 摩格  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/moge/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMoge(HttpServletRequest request) {
        return channelService.verifyMoge(request);
    }

    /**
     * 咪咕登陆成功服务器端通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/migu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String miguLoginNotify(HttpServletRequest request) {
        return channelService.miguLoginNotify(request);
    }

    /**
     * 咪咕  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/migu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMigu(HttpServletRequest request) {
        return channelService.verifyMigu(request);
    }

    /**
     * 同游游 session验证
     *
     * @return
     */
    @RequestMapping(value = "/tuu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyTuuSession(TuuSession session) {
        logger.debug(session.toString());
        return channelService.verifyTuuSession(session);
    }

    /**
     * 同游游  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/tuu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackTuu(HttpServletRequest request) {
        return channelService.verifyTuu(request);
    }

    /**
     * 魔游游  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/moyoyo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackMoyoyo(HttpServletRequest request) {
        return channelService.verifyMoyoyo(request);
    }

    /**
     * 大麦助手  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/damai/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackDamai(HttpServletRequest request) {
        return channelService.verifyDamai(request);
    }

    /**
     * 说玩 session验证
     *
     * @return
     */
    @RequestMapping(value = "/shuowan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyShuowanSession(ShuowanSession session) {
        logger.debug(session.toString());
        return channelService.verifyShuowanSession(session);
    }

    /**
     * 说玩  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/shuowan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackShuowan(HttpServletRequest request) {
        return channelService.verifyShuowan(request);
    }

    /**
     * 第一应用 session验证
     *
     * @return
     */
    @RequestMapping(value = "/firstapp/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyFirstappSession(FirstappSession session) {
        logger.debug(session.toString());
        return channelService.verifyFirstappSession(session);
    }

    /**
     * 第一应用  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/firstapp/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackFirstapp(HttpServletRequest request) {
        return channelService.verifyFirstapp(request);
    }

    /**
     * 钱宝服务器登录结果通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qbao/login", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String qbaoLogin(HttpServletRequest request) {
        return channelService.qbaoLogin(request);
    }

    /**
     * 钱宝支付签名
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qbao/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String qbaoPaySign(HttpServletRequest request) {
        return channelService.qbaoPaySign(request);
    }

    /**
     * 钱宝  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qbao/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackQbao(HttpServletRequest request) {
        return channelService.rechargeCallBackQbao(request);
    }

    /**
     * 冰趣服务器登录结果通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bingqu/login", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String bingquLogin(HttpServletRequest request) {
        return channelService.bingquBaowanLogin(request);
    }

    /**
     * 冰趣支付签名
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bingqu/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String bingquPaySign(HttpServletRequest request) {
        return channelService.bingquBaowanPaySign(request);
    }

    /**
     * 冰趣  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bingqu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackBingqu(HttpServletRequest request) {
        return channelService.rechargeCallBackbingquBaowan(request);
    }

    /**
     * 宝玩服务器登录结果通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/baowan/login", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String baowanLogin(HttpServletRequest request) {
        return channelService.bingquBaowanLogin(request);
    }

    /**
     * 宝玩支付签名
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/baowan/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String baowanPaySign(HttpServletRequest request) {
        return channelService.bingquBaowanPaySign(request);
    }

    /**
     * 宝玩  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/baowan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackBaowan(HttpServletRequest request) {
        return channelService.rechargeCallBackbingquBaowan(request);
    }

    /**
     * 奇葩  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qipa/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackQipa(HttpServletRequest request) {
        return channelService.rechargeCallBackQipa(request);
    }

    /**
     * 爱谱 session验证
     *
     * @return
     */
    @RequestMapping(value = "/aipu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyAipuSession(AipuSession session) {
        logger.debug(session.toString());
        return channelService.verifyAipuSession(session);
    }

    /**
     * 爱谱 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/aipu/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackAipu(HttpServletRequest request) {
        return channelService.verifyAipu(request);
    }


    /**
     * 顺网 session验证
     *
     * @return
     */
    @RequestMapping(value = "/shunwang/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyShunwangSession(HttpServletRequest request) {
        return channelService.verifyShunwangSession(request);
    }

    /**
     * 顺网 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/shunwang/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackShunwang(HttpServletRequest request) {
        return channelService.verifyShunwang(request);
    }


    /**
     * 卓易 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/zhuoyi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackZhouyi(HttpServletRequest request) {
        return channelService.verifyZhuoyi(request);
    }

    /**
     * 云宵堂 session验证
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/yxt/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYunXiaoTanSession(YunxiaotanSession session) {
        return channelService.verifyYunxiaotanSession(session);
    }

    /**
     * 云宵堂 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yxt/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYunxiaoTan(HttpServletRequest request) {
        return channelService.verifyYunxiaotan(request);
    }

    /**
     * 广州配对 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/gzpd/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyGuangzhoupeidui(HttpServletRequest request) {
        return channelService.verifyGuangzhoupeidui(request);
    }

    /**
     * 点优 支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/dyoo/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDianyoo(HttpServletRequest request) {
        return channelService.verifyDianyoo(request);
    }

    /**
     * 虫 虫 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/chongchong/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifychongchongSession(ChongchongSession request) {
        return channelService.verifyhongchongSession(request);
    }

    @RequestMapping(value = "/chongchong/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyChongChong(HttpServletRequest request) {
        return channelService.verifyChongchong(request);
    }

    @RequestMapping(value = "/qishi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQishi(HttpServletRequest request) {
        return channelService.verifyQishi(request);
    }

    @RequestMapping(value = "/tt/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyTt(HttpServletRequest request) {
        return channelService.verifyTt(request);
    }

    /**
     * TT 登录校验
     *
     * @param ttSession
     * @return
     */
    @RequestMapping(value = "/tt/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyTtSession(TtSession ttSession) {
        return channelService.verifyTtSession(ttSession);
    }

    /**
     * TT 充值下单(安卓)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/tt/recharge", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeTt(HttpServletRequest request) {
        return channelService.rechargeTt(request);
    }

    @RequestMapping(value = "/yeshen/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYeshen(HttpServletRequest request) {
        channelService.verifyYeshen(request);
        return "";
    }

    /**
     * 乐玩 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lewan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLewanSession(LewanSession request) {
        return channelService.verifyLewanSession(request);
    }

    /**
     * 乐玩 支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lewan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLewan(HttpServletRequest request) {
        return channelService.verifyLewan(request);
    }

    /**
     * 玩客 支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wanke/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyWanke(HttpServletRequest request) {
        return channelService.verifyWanke(request);
    }

    /**
     * 道盟 支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/daomeng/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDaomen(HttpServletRequest request) {
        return channelService.verifyDaomen(request);
    }

    /**
     * 无限动力 支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wxdl/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyWuxiandongli(HttpServletRequest request) {
        return channelService.verifyWuxiandongli(request);
    }

    /**
     * 无限动力 计费点获取
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wxdl/paycode", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getWuxiandongliPaycode(HttpServletRequest request) {
        return channelService.getWuxiandongliPaycode(request);
    }

    /**
     * 小7 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xiao7/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXiao7Session(Xiao7Session request) {
//	    logger.info("xiaoqi   "+ HttpUtils.getRequestParams(request).toString());
        return channelService.verifyXiao7Session(request);
    }

    /**
     * 小7 充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xiao7/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXiao7(HttpServletRequest request) {
        return channelService.verifyXiao7(request);
    }

    /**
     * QuickSDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/quicksdk/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQuickSession(QuickSdkSession request) {
        return channelService.verifyQuickSession(request);
    }

    /**
     * QuickSDK 充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/quicksdk/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQuicksdk(HttpServletRequest request) {
        return channelService.verifyQuick(request);
    }

    /**
     * 易接SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yijiesdk/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYijieSession(YijieSdkSession request) {
        return channelService.verifyYijieSession(request);
    }

    /**
     * yijieSDK 充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yijiesdk/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYijiesdk(HttpServletRequest request) {
        return channelService.verifyYijie(request);
    }

    /**
     * 快发 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kuaifa/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyKuaifaSession(KuaifaSession request) {
        return channelService.verifyKuaifaSession(request);
    }

    /**
     * 快发 充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/kuaifa/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyKuaifa(HttpServletRequest request) {
        return channelService.verifyKuaifa(request);
    }

    /**
     * 应用汇 ftx session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/ftx/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyFtxSession(FtxSession request) {
        return channelService.verifyFtxSession(request);
    }

    /**
     * 应用汇充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/ftx/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyFtx(HttpServletRequest request) {
        return channelService.verifyFtx(request);
    }

    /**
     * 易幻 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yihuan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYihuanSession(YihuanSession request) {
        return channelService.verifyYihuanSession(request);
    }


    @RequestMapping(value = "/yihuan/paycode", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getYihuanCode(HttpServletRequest request) {
        return channelService.getYihuanPayCode(request);
    }

    /**
     * 易幻充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/yihuan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyYihuan(HttpServletRequest request) {
        return channelService.verifyYihuan(request);
    }

    /**
     * 红手指 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/hongshouzhi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyHongshouzhiSession(HongshouzhiSession request) {
        return channelService.verifyHongshouzhiSession(request);
    }

    /**
     * 红手指充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/hongshouzhi/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyHongshouzhi(HttpServletRequest request) {
        return channelService.verifyHongshouzhi(request);
    }

    /**
     * 红手指提交角色信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/hongshouzhi/role", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String submitHongshouzhiRole(HongShouZhiRole role) {
        return channelService.submitHongshouzhiRole(role);
    }

    /**
     * FAN SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fansdk/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyFanSdkSession(FansdkSession request) {
        return channelService.verifyFansdkSession(request);
    }

    /**
     * 值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fansdk/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyFansdk(HttpServletRequest request) {
        return channelService.verifyFansdk(request);
    }


    /**
     * 逆光 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/niguang/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyNiguangSession(NiguangSession request) {
        return channelService.verifyNiguangSession(request);
    }

    /**
     * 逆光充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/niguang/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyNiguangsdk(HttpServletRequest request) {
        return channelService.verifyNiguangsdk(request);
    }

    /**
     * 奥创 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/aochuang/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyNiguangSession(AoChuangSession request) {
        return channelServicePartTwo.verifyAoChuangSession(request);
    }

    /**
     * 奥创充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/aochuang/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyAoChuangsdk(HttpServletRequest request) {
        return channelServicePartTwo.verifyAoChuangsdk(request);
    }

    /**
     * 啪啪游充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/papayou/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyPaPaYou(HttpServletRequest request) {
        return channelServicePartTwo.verifyPaPaYou(request);
    }


    /**
     * 淘手游 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/taoshouyou/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyTaoShouYouSession(TaoShouYouSession request) {
        return channelServicePartTwo.verifyTaoShouYouSession(request);
    }

    /**
     * 淘手游充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/taoshouyou/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyTaoShouYousdk(HttpServletRequest request) {
        return channelServicePartTwo.verifyTaoShouYousdk(request);
    }

    /**
     * 芒果玩 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mangguowan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMangGuoWanSession(MangGuoWanSession request) {
        return channelServicePartTwo.verifyMangGuoWanSession(request);
    }

    /**
     * 芒果玩充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mangguowan/pay", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMangGuoWansdk(HttpServletRequest request) {
        return channelServicePartTwo.verifyMangGuoWansdk(request);
    }

}
