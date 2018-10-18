package com.qinglan.sdk.server.release.presentation.platform;

import com.qinglan.sdk.server.release.application.platform.PlatformService;
import com.qinglan.sdk.server.release.application.platform.PlatformServicePartTwo;
import com.qinglan.sdk.server.release.domain.platform.YaoyueCallback;
import com.qinglan.sdk.server.release.domain.platform.YouleCallback;
import com.qinglan.sdk.server.release.presentation.platform.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/platform")
public class PlatformController {
	private static final Logger logger = LoggerFactory.getLogger(PlatformController.class);

	@Resource
	private PlatformService platformService;

	@Resource
	private PlatformServicePartTwo platformServicePartTwo;
	/**
	 * yaoyue 支付结果回调
	 *
	 * @param zhidian
	 * @return
	 */
	@RequestMapping(value = "/yaoyue", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String zhidian(YaoyueCallback zhidian) {
		return platformService.verifyYaoyue(zhidian);
	}

	/**
	 * UC session验证
	 *
	 * @param ucGameSession
	 * @return
	 */
	@RequestMapping(value = "/ucgame/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String ucGameSession(UcGameSession ucGameSession) {
		logger.debug(ucGameSession.toString());
		return platformService.verifyUcSession(ucGameSession);
	}

	/**
	 * UC 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/ucgame")
	@ResponseBody
	public String ucgame(HttpServletRequest request) {
		return platformService.verifyUcGame(request);
	}

	/**
	 * 小米 session验证
	 *
	 * @param ucGameSession
	 * @return
	 */
	@RequestMapping(value = "/xiaomi/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String xiaomiSession(XiaomiSession xiaomiSession) {
		logger.debug(xiaomiSession.toString());
		return platformService.verifyXiaomiSession(xiaomiSession);
	}

	/**
	 * 小米 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/xiaomi")
	@ResponseBody
	public String xiaomi(HttpServletRequest request) {
		return platformService.verifyXiaomi(request);
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
		return platformService.verifyQihooSession(qihooSession);
	}

	/**
	 * 360 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/qihoo")
	@ResponseBody
	public String qihoo(HttpServletRequest request) {
		return platformService.verifyQihoo(request);
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
		return platformService.verifyBaiduSession(baiduSession);
	}

	/**
	 * 百度 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/baidu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String baidu(HttpServletRequest request) {
		return platformService.verifyBaidu(request);
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
		return platformService.verifyAnzhiSession(anzhiSession);
	}

	/**
	 * 安智支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/anzhi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String anzhi(HttpServletRequest request) {
		return platformService.verifyAnzhi(request);
	}

	/**
	 * 豌豆荚 session验证
	 *
	 * @param anzhiSession
	 * @return
	 */
	@RequestMapping(value = "/wdj/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyWdjSession(WdjSession session) {
		logger.debug(session.toString());
		return platformService.verifyWdjSession(session);
	}

	/**
	 * 豌豆荚支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/wdj", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackWdj(HttpServletRequest request) {
		return platformService.verifyWdj(request);
	}

	/**
	 * 当乐 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/downjoy", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackDownjoy(HttpServletRequest request) {
		return platformService.verifyDownjoy(request);
	}

	/**
	 * 搜狗 session验证
	 *
	 * @param anzhiSession
	 * @return
	 */
	@RequestMapping(value = "/sougou/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String sougouSession(SougouSession sougouSession) {
		logger.debug(sougouSession.toString());
		return platformService.verifySougouSession(sougouSession);
	}

	/**
	 * 搜狗支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/sougou", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String sougou(HttpServletRequest request) {
		return platformService.verifySougou(request);
	}

	/**
	 * 酷派 session验证
	 *
	 * @param kupaiSession
	 * @return
	 */
	@RequestMapping(value = "/kupai/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyKupaiSession(KupaiSession session) {
		logger.debug(session.toString());
		return platformService.verifyKupaiSession(session);
	}

	/**
	 * 酷派 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kupai", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackKupai(HttpServletRequest request) {
		return platformService.verifyKupai(request);
	}

	/**
	 * oppo session验证
	 *
	 * @param oppoSession
	 * @return
	 */
	@RequestMapping(value = "/oppo/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyOppoSession(OppoSession session) {
		logger.debug(session.toString());
		return platformService.verifyOppoSession(session);
	}

	/**
	 * oppo 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oppo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackOppo(HttpServletRequest request) {
		logger.debug(request.getParameterMap().toString());
		return platformService.verifyOppo(request);
	}

	/**
	 * 金立 session验证
	 *
	 * @param gioneeSession
	 * @return
	 */
	@RequestMapping(value = "/gionee/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyGioneeSession(GioneeSession session) {
		logger.debug(session.toString());
		return platformService.verifyGioneeSession(session);
	}

	/**
	 * 金立 创建订单
	 *
	 * @return
	 */
	@RequestMapping(value = "/gionee/order/create", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String gioneeOrderCreate(HttpServletRequest request) {
		return platformService.gioneeOrderCreate(request);
	}

	/**
	 * 金立支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/gionee", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackGionee(HttpServletRequest request) {
		return platformService.verifyGionee(request);
	}


	/**
	 * 91 session验证
	 *
	 * @param varify91Session
	 * @return
	 */
	@RequestMapping(value = "/91/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verify91Session(Varify91Session session) {
		logger.debug(session.toString());
		return platformService.verify91Session(session);
	}

	/**
	 * 91支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/91", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBack91(HttpServletRequest request) {
		logger.debug(request.getParameterMap().toString());
		return platformService.verify91(request);
	}

	/**
	 * vivo session验证
	 *
	 * @param vivoSession
	 * @return
	 */
	@RequestMapping(value = "/vivo/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyVivoSession(VivoSession session) {
		logger.debug(session.toString());
		return platformService.verifyVivoSession(session);
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
		return platformService.vivoPaySign(vivoPaySign);
	}

	/**
	 * vivo支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/vivo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackVivo(HttpServletRequest request) {
		return platformService.verifyVivo(request);
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
		return platformService.verifyAppchinaSession(session);
	}

	/**
	 * 应用汇 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/appchina", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackAppchina(HttpServletRequest request) {
		return platformService.verifyAppchina(request);
	}

	/**
	 * 偶玩 session验证
	 *
	 * @param vivoSession
	 * @return
	 */
	@RequestMapping(value = "/ouwan/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyOuwanSession(OuwanSession session) {
		logger.debug(session.toString());
		return platformService.verifyOuwanSession(session);
	}

	/**
	 * 偶玩 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/ouwan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackOuwan(HttpServletRequest request) {
		return platformService.verifyOuwan(request);
	}

	/**
	 * 优酷 session验证
	 *
	 * @param YoukuSession
	 * @return
	 */
	@RequestMapping(value = "/youku/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYoukuSession(YoukuSession session) {
		return platformService.verifyYoukuSession(session);
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
		return platformService.verifyYouku(request);
	}

	/**
	 * 机锋 session验证
	 *
	 * @param JifengSession
	 * @return
	 */
	@RequestMapping(value = "/jifeng/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyJifengSession(JifengSession session) {
		return platformService.verifyJifengSession(session);
	}

	/**
	 * 机锋 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/jifeng", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackJifeng(HttpServletRequest request) {
		return platformService.verifyJifeng(request);
	}

	/**
	 * HTC 支付内容加密
	 *
	 * @param String content
	 * @return
	 */
	@RequestMapping(value = "/htc/pay/sign", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String HTCPaySign(HttpServletRequest request) {
		return platformService.signHTCPayContent(request);
	}

	/**
	 * HTC验证
	 *
	 * @param HTCSession
	 * @return
	 */
	@RequestMapping(value = "/htc/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyHtcSession(HTCSession session) {
		return platformService.verifyHTCSession(session);
	}

	/**
	 * HTC 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/htc", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackHTC(HttpServletRequest request) {
		return platformService.verifyHTC(request);
	}

	/**
	 * 魅族 session验证
	 *
	 * @param vivoSession
	 * @return
	 */
	@RequestMapping(value = "/meizu/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyMeizuSession(MeizuSession session) {
		return platformService.verifyMeizuSession(session);
	}

	/**
	 * 魅族 支付参数签名
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/meizu/pay/sign", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String meizuPaySign(HttpServletRequest request) {
		return platformService.meizuPaySign(request);
	}

	/**
	 * 魅族 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/meizu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackMeizu(HttpServletRequest request) {
		return platformService.verifyMeizu(request);
	}

	/**
	 * n多网 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/nduo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackNduo(HttpServletRequest request) {
		return platformService.verifyNduo(request);
	}

	/**
	 * 游龙 session验证
	 *
	 * @param youlongSession
	 * @return
	 */
	@RequestMapping(value = "/yl/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYoulongSession(YoulongSession session) {
		return platformService.verifyYoulongSession(session);
	}

	/**
	 * 游龙 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/yl", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackYoulong(HttpServletRequest request) {
		return platformService.verifyYoulong(request);
	}

	/**
	 * 联想 session验证
	 *
	 * @param lenovoSession
	 * @return
	 */
	@RequestMapping(value = "/lenovo/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLenovoSession(LenovoSession session) {
		return platformService.verifyLenovoSession(session);
	}

	/**
	 * 联想 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/lenovo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLenovo(HttpServletRequest request) {
		return platformService.verifyLenovo(request);
	}

	/**
	 * 酷动 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kudong", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackKudong(HttpServletRequest request) {
		return platformService.verifyKudong(request);
	}

	/**
	 * 乐视 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/letv", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLetv(HttpServletRequest request) {
		return platformService.verifyLetv(request);
	}

	/**
	 * 手盟 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/19meng", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBack19meng(HttpServletRequest request) {
		return platformService.verify19meng(request);
	}

	/**
	 * 酷我 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kuwo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackKuwo(HttpServletRequest request) {
		return platformService.verifyKuwo(request);
	}

	/**
	 * 木蚂蚁 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mumayi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackMumayi(HttpServletRequest request) {
		return platformService.verifyMumayi(request);
	}

	/**
	 * 电信爱游戏 session验证
	 *
	 * @param playSession
	 * @return
	 */
	@RequestMapping(value = "/play/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyPlaySession(PlaySession session) {
		return platformService.verifyPlaySession(session);
	}

	/**
	 * 电信爱游戏 短信支付确认
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/playSms", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargePalySms(HttpServletRequest request) {
		return platformService.verifyPlaySms(request);
	}

	/**
	 * 电信爱游戏 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/play", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackPaly(HttpServletRequest request) {
		return platformService.verifyPlay(request);
	}

	/**
	 * 玖度科技爱上游戏 session验证
	 *
	 * @param jiuduSession
	 * @return
	 */
	@RequestMapping(value = "/jiudu/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyJiuduSession(JiuduSession session) {
		return platformService.verifyJiuduSession(session);
	}

	/**
	 * 玖度科技爱上游戏 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/jiudu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackJiudu(HttpServletRequest request) {
		return platformService.verifyJiudu(request);
	}

	/**
	 * 泡椒 session验证
	 *
	 * @param paojiaoSession
	 * @return
	 */
	@RequestMapping(value = "/paojiao/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyPaojiaoSession(PaojiaoSession session) {
		return platformService.verifyPaojiaoSession(session);
	}

	/**
	 * 泡椒 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/paojiao", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackPaojiao(HttpServletRequest request) {
		return platformService.verifyPaojiao(request);
	}

	/**
	 * 七匣子 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/qixiazi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackQixiazi(HttpServletRequest request) {
		return platformService.verifyQixiazi(request);
	}

	/**
	 * 快用session验证
	 *
	 * @param kuaiyongSession
	 * @return
	 */
	@RequestMapping(value = "/kuaiyong/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyKuaiyongSession(KuaiyongSession session) {
		return platformService.verifyKuaiyongSession(session);
	}

	/**
	 * 快用支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kuaiyong", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackKuaiyong(HttpServletRequest request) {
		return platformService.verifyKuaiyong(request);
	}

	/**
	 * 华为 session验证
	 *
	 * @param huaweiSession
	 * @return
	 */
	@RequestMapping(value = "/huawei/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyHuaweiSession(HuaweiSession session) {
		return platformService.verifyHuaweiSession(session);
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

		return platformService.huaweiPaySign(request);
	}

	/**
	 * 华为支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/huawei", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackHuawei(HttpServletRequest request) {
		return platformService.verifyHuawei(request);
	}

	/**
	 * 4399 session验证
	 *
	 * @param ftnnSession
	 * @return
	 */
	@RequestMapping(value = "/4399/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyFtnnSession(FtnnSession session) {
		return platformService.verifyFtnnSession(session);
	}

	/**
	 * 4399支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/4399", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackFtnn(HttpServletRequest request) {
		return platformService.verifyFtnn(request);
	}

	/**
	 * 37 session验证
	 *
	 * @param TsSession
	 * @return
	 */
	@RequestMapping(value = "/37/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyTsnnSession(TsSession session) {
		return platformService.verifyTsSession(session);
	}

	/**
	 * 37 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/37", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackTs(HttpServletRequest request) {
		return platformService.verifyTs(request);
	}

	/**
	 * 拇指游玩 session验证
	 *
	 * @param MuzhiSession
	 * @return
	 */
	@RequestMapping(value = "/muzhi/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyMuzhiSession(MuzhiSession session) {
		return platformService.verifyMuzhiSession(session);
	}

	/**
	 * 拇指游玩 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/muzhi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackMuzhi(HttpServletRequest request) {
		return platformService.verifyMuzhi(request);
	}

	/**
	 * 拇指玩 session验证
	 *
	 * @param MuzhiwanSession
	 * @return
	 */
	@RequestMapping(value = "/muzhiwan/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyMuzhiwanSession(MuzhiwanSession session) {
		return platformService.verifyMuzhiwanSession(session);
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
		return platformService.verifyMuzhiwan(request);
	}

	/**
	 * 靠谱 session验证
	 *
	 * @param KaopuSession
	 * @return
	 */
	@RequestMapping(value = "/kaopu/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyKaopuSession(KaopuSession session) {
		return platformService.verifyKaopuSession(session);
	}

	/**
	 * 靠谱 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kaopu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackKaopu(HttpServletRequest request) {
		return platformService.verifyKaopu(request);
	}

	/**
	 * 游戏坛子 session验证
	 *
	 * @param GametanziSession
	 * @return
	 */
	@RequestMapping(value = "/gametanzi/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyGametanziSession(GametanziSession session) {
		return platformService.verifyGametanziSession(session);
	}

	/**
	 * 游戏坛子 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/gametanzi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackGametanzi(HttpServletRequest request) {
		return platformService.verifyGametanzi(request);
	}

	/**
	 * 维动 session验证
	 *
	 * @param WeidongSession
	 * @return
	 */
	@RequestMapping(value = "/weidong/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyWeidongSession(WeidongSession session) {
		return platformService.verifyWeidongSession(session);
	}

	/**
	 * 维动 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/weidong", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackWeidong(HttpServletRequest request) {
		return platformService.verifyWeidong(request);
	}

	/**
	 * 乐非凡 session验证
	 *
	 * @param EdgSession
	 * @return
	 */
	@RequestMapping(value = "/edg/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyEdgSession(EdgSession session) {
		return platformService.verifyEdgSession(session);
	}

	/**
	 * 乐非凡 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/edg", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackEdg(HttpServletRequest request) {
		return platformService.verifyEdg(request);
	}

	/**
	 * 应用宝 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tencent", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackTencent(HttpServletRequest request) {

		return platformService.verifyTencent(request);
	}

	/**
	 * 应用宝 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tencent2", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackTencent2(HttpServletRequest request) {

		return platformService.verifyTencent2(request);
	}

	/**
	 * UU村 session验证
	 *
	 * @param UucunSession
	 * @return
	 */
	@RequestMapping(value = "/uucun/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyUucunSession(UucunSession session) {
		return platformService.verifyUucunSession(session);
	}

	/**
	 * UU村 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uucun", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackUucun(HttpServletRequest request) {
		return platformService.verifyUucun(request);
	}

	/**
	 * 联旭开游 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kaiuc", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackKaiuc(HttpServletRequest request) {
		return platformService.verifyKaiuc(request);
	}

	/**
	 * 猎宝 session验证
	 *
	 * @param LiebaoSession
	 * @return
	 */
	@RequestMapping(value = "/liebao/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLiebaoSession(LiebaoSession session) {
		return platformService.verifyLiebaoSession(session);
	}

	/**
	 * 猎宝 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/liebao", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLiebao(HttpServletRequest request) {
		return platformService.verifyLiebao(request);
	}

	/**
	 * 07073 session验证
	 *
	 * @param LeshanSession
	 * @return
	 */
	@RequestMapping(value = "/07073/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLeshanSession(LeshanSession session) {
		return platformService.verifyLeshanSession(session);
	}

	/**
	 * 07073 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/07073", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLeshan(HttpServletRequest request) {
		return platformService.verifyLeshan(request);
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
		return platformService.atetPaypoing(request);
	}

	/**
	 * 时讯 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/atet", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackAtet(HttpServletRequest request) {
		return platformService.verifyAtet(request);
	}

	/**
	 * 神起爱娱乐 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/2yl", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackShenqi(HttpServletRequest request) {
		return platformService.verifyShenqi(request);
	}

	/**
	 * 海马 session验证
	 *
	 * @param HaimaSession
	 * @return
	 */
	@RequestMapping(value = "/haima/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyHaimaSession(HaimaSession session) {
		logger.debug(session.toString());
		return platformService.verifyHaimaSession(session);
	}

	/**
	 * 07073 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/haima", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackHaima(HttpServletRequest request) {
		return platformService.verifyHaima(request);
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
		return platformService.pengyouwanPaypoing(request);
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
		return platformService.verifyPengyouwan(request);
	}

	/**
	 * 3899 session验证
	 *
	 * @param TennSession
	 * @return
	 */
	@RequestMapping(value = "/3899/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verify3899Session(TennSession session) {
		logger.debug(session.toString());
		return platformService.verify3899Session(session);
	}

	/**
	 * 3899支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/3899", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBack3899(HttpServletRequest request) {
		return platformService.verify3899(request);
	}

	/**
	 * 榴莲 session验证
	 *
	 * @param LiulianSession
	 * @return
	 */
	@RequestMapping(value = "/liulian/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLiulianSession(LiulianSession session) {
		logger.debug(session.toString());
		return platformService.verifyLiulianSession(session);
	}

	/**
	 * 榴莲 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/liulian", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLiulian(HttpServletRequest request) {
		return platformService.verifyLiulian(request);
	}

	/**
	 * 迅雷支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/xunlei", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackXunlei(HttpServletRequest request) {
		return platformService.verifyXunlei(request);
	}

	/**
	 * 果盘 session验证
	 *
	 * @param GuopanSession
	 * @return
	 */
	@RequestMapping(value = "/guopan/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyGuopanSession(GuopanSession session) {
		logger.debug(session.toString());
		return platformService.verifyGuopanSession(session);
	}

	/**
	 * 果盘支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/guopan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackGuopan(HttpServletRequest request) {
		return platformService.verifyGuopan(request);
	}

	/**
	 * 群兴飞阳 session验证
	 *
	 * @param QxfySession
	 * @return
	 */
	@RequestMapping(value = "/qxfy/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyQxfySession(QxfySession session) {
		logger.debug(session.toString());
		return platformService.verifyQxfySession(session);
	}

	/**
	 * 群兴飞阳支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/qxfy", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackQxfy(HttpServletRequest request) {
		return platformService.verifyQxfy(request);
	}

	/**
	 * 19游戏支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/19game", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBack19game(HttpServletRequest request) {
		return platformService.verify19game(request);
	}

	/**
	 * 龙翔 session验证
	 *
	 * @param LongxiangSession
	 * @return
	 */
	@RequestMapping(value = "/longxiang/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLongxiangSession(LongxiangSession session) {
		logger.debug(session.toString());
		return platformService.verifyLongxiangSession(session);
	}

	/**
	 * 龙翔游戏支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/longxiang", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLongxiang(HttpServletRequest request) {
		return platformService.verifyLongxiang(request);
	}

	/**
	 * 乐嗨嗨 session验证
	 *
	 * @param LehihiSession
	 * @return
	 */
	@RequestMapping(value = "/lehihi/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLehihiSession(LehihiSession session) {
		logger.debug(session.toString());
		return platformService.verifyLehihiSession(session);
	}

	/**
	 * 乐嗨嗨 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/lehihi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackLehihi(HttpServletRequest request) {
		return platformService.verifyLehihi(request);
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
		return platformService.verifyKoudai(request);
	}

	/**
	 * 游乐SDK2.0 session验证
	 *
	 * @param YouleSession
	 * @return
	 */
	@RequestMapping(value = "/youle/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYouleSession(YouleSession session) {
		logger.debug(session.toString());
		return platformService.verifyYouleSession(session);
	}

	/**
	 * 游乐SDK2.0  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/youle", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackYoule(YouleCallback callback) {
		return platformService.verifyYoule(callback);
	}

	/**
	 * 逑途 session验证
	 *
	 * @param QiutuSession
	 * @return
	 */
	@RequestMapping(value = "/qiutu/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyQiutuSession(QiutuSession session) {
		logger.debug(session.toString());
		return platformService.verifyQiutuSession(session);
	}

	/**
	 * 逑途  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/qiutu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackQiutu(HttpServletRequest request) {
		return platformService.verifyQiutu(request);
	}

	/**
	 * 悦玩 session验证
	 *
	 * @param YuewanSession
	 * @return
	 */
	@RequestMapping(value = "/yuewan/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYuewanSession(YuewanSession session) {
		logger.debug(session.toString());
		return platformService.verifyYuewanSession(session);
	}

	/**
	 * 悦玩  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/yuewan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackYuewan(HttpServletRequest request) {
		return platformService.verifyYuewan(request);
	}

	/**
	 * 无属性  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/wsx", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackWsx(HttpServletRequest request) {
		logger.info("rechargeCallBackWsx----------------");
		return platformService.verifyWsx(request);
	}
    /**
     * Iveryone session验证
     *
     * @param IveryoneSession
     * @return
     */
    @RequestMapping(value = "/iveryone/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyiVeryoneSession(IveryoneSession session) {
        logger.debug(session.toString());
        return platformService.verifyIveryoneSession(session);
    }

    /**
     * Iveryone  支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/iveryone", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackIveryone(HttpServletRequest request) {
        return platformService.verifyIveryone(request);
    }


	/**
	 * 多元互动 session验证
	 *
	 * @param dyhdSession
	 * @return
	 */
	@RequestMapping(value = "/dyhd/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyDyhdSession(DyhdSession session) {
		logger.debug(session.toString());
		return platformService.verifyDyhdSession(session);
	}

	/**
	 * 多元互动  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dyhd", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackDyhd(HttpServletRequest request) {
		return platformService.verifyDyhd(request);
	}

	/**
	 * 齐齐乐 session验证
	 *
	 * @param qiqileSession
	 * @return
	 */
	@RequestMapping(value = "/qiqile/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyQiqileSession(QiqileSession session) {
		logger.debug(session.toString());
		return platformService.verifyQiqileSession(session);
	}

	/**
	 * 齐齐乐  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/qiqile", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackQiqile(HttpServletRequest request) {
		return platformService.verifyQiqile(request);
	}

	/**
	 * 7723 session验证
	 *
	 * @param ssttSession
	 * @return
	 */
	@RequestMapping(value = "/7723/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verify7723Session(SsttSession session) {
		logger.debug(session.toString());
		return platformService.verify7723Session(session);
	}

	/**
	 * 7723  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/7723", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBack7723(HttpServletRequest request) {
		return platformService.verify7723(request);
	}

	/**
	 * 摩格 session验证
	 *
	 * @param mogeSession
	 * @return
	 */
	@RequestMapping(value = "/moge/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyMogeSession(MogeSession session) {
		logger.debug(session.toString());
		return platformService.verifyMogeSession(session);
	}

	/**
	 * 摩格  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/moge", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackMoge(HttpServletRequest request) {
		return platformService.verifyMoge(request);
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
		return platformService.miguLoginNotify(request);
	}

	/**
	 * 咪咕  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/migu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackMigu(HttpServletRequest request) {
		return platformService.verifyMigu(request);
	}

	/**
	 * 同游游 session验证
	 *
	 * @param tuuSession
	 * @return
	 */
	@RequestMapping(value = "/tuu/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyTuuSession(TuuSession session) {
		logger.debug(session.toString());
		return platformService.verifyTuuSession(session);
	}

	/**
	 * 同游游  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tuu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackTuu(HttpServletRequest request) {
		return platformService.verifyTuu(request);
	}

	/**
	 * 魔游游  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/moyoyo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackMoyoyo(HttpServletRequest request) {
		return platformService.verifyMoyoyo(request);
	}

	/**
	 * 大麦助手  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/damai", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackDamai(HttpServletRequest request) {
		return platformService.verifyDamai(request);
	}

	/**
	 * 说玩 session验证
	 *
	 * @param shuowanSession
	 * @return
	 */
	@RequestMapping(value = "/shuowan/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyShuowanSession(ShuowanSession session) {
		logger.debug(session.toString());
		return platformService.verifyShuowanSession(session);
	}

	/**
	 * 说玩  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/shuowan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackShuowan(HttpServletRequest request) {
		return platformService.verifyShuowan(request);
	}

	/**
	 * 第一应用 session验证
	 *
	 * @param firstappSession
	 * @return
	 */
	@RequestMapping(value = "/firstapp/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyFirstappSession(FirstappSession session) {
		logger.debug(session.toString());
		return platformService.verifyFirstappSession(session);
	}

	/**
	 * 第一应用  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/firstapp", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackFirstapp(HttpServletRequest request) {
		return platformService.verifyFirstapp(request);
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
		return platformService.qbaoLogin(request);
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
		return platformService.qbaoPaySign(request);
	}

	/**
	 * 钱宝  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/qbao", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackQbao(HttpServletRequest request) {
		return platformService.rechargeCallBackQbao(request);
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
		return platformService.bingquBaowanLogin(request);
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
		return platformService.bingquBaowanPaySign(request);
	}

	/**
	 * 冰趣  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bingqu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackBingqu(HttpServletRequest request) {
		return platformService.rechargeCallBackbingquBaowan(request);
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
		return platformService.bingquBaowanLogin(request);
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
		return platformService.bingquBaowanPaySign(request);
	}

	/**
	 * 宝玩  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/baowan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackBaowan(HttpServletRequest request) {
		return platformService.rechargeCallBackbingquBaowan(request);
	}

	/**
	 * 奇葩  支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/qipa", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackQipa(HttpServletRequest request) {
		return platformService.rechargeCallBackQipa(request);
	}

	/**
	 * 爱谱 session验证
	 *
	 * @param AipuSession
	 * @return
	 */
	@RequestMapping(value = "/aipu/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyAipuSession(AipuSession session) {
		logger.debug(session.toString());
		return platformService.verifyAipuSession(session);
	}

	/**
	 * 爱谱 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/aipu", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackAipu(HttpServletRequest request) {
		return platformService.verifyAipu(request);
	}


	/**
	 * 顺网 session验证
	 *
	 * @param AipuSession
	 * @return
	 */
	@RequestMapping(value = "/shunwang/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyShunwangSession(HttpServletRequest request) {
		return platformService.verifyShunwangSession(request);
	}

	/**
	 * 顺网 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/shunwang", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackShunwang(HttpServletRequest request) {
		return platformService.verifyShunwang(request);
	}


	/**
	 * 卓易 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/zhuoyi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeCallBackZhouyi(HttpServletRequest request) {
		return platformService.verifyZhuoyi(request);
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
		return platformService.verifyYunxiaotanSession(session);
	}

	/**
	 * 云宵堂 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/yxt", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYunxiaoTan(HttpServletRequest request) {
		return platformService.verifyYunxiaotan(request);
	}

	/**
	 * 广州配对 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/gzpd", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyGuangzhoupeidui(HttpServletRequest request) {
		return platformService.verifyGuangzhoupeidui(request);
	}

	/**
	 * 点优 支付结果回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dyoo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyDianyoo(HttpServletRequest request) {
		return platformService.verifyDianyoo(request);
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
		return platformService.verifyhongchongSession(request);
	}

	@RequestMapping(value = "/chongchong", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyChongChong(HttpServletRequest request) {
		return platformService.verifyChongchong(request);
	}

	@RequestMapping(value = "/qishi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyQishi(HttpServletRequest request) {
		return platformService.verifyQishi(request);
	}

	@RequestMapping(value = "/tt", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyTt(HttpServletRequest request) {
		return platformService.verifyTt(request);
	}

	/**
	 * TT 登录校验
	 * @param ttSession
	 * @return
	 */
	@RequestMapping(value = "/tt/session", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyTtSession(TtSession ttSession) {
		return platformService.verifyTtSession(ttSession);
	}

	/**
	 * TT 充值下单(安卓)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tt/recharge", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String rechargeTt(HttpServletRequest request) {
		return platformService.rechargeTt(request);
	}

	@RequestMapping(value = "/yeshen", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYeshen(HttpServletRequest request) {
		platformService.verifyYeshen(request);
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
		return platformService.verifyLewanSession(request);
	}

	/**
	 * 乐玩 支付回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/lewan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyLewan(HttpServletRequest request) {
		return platformService.verifyLewan(request);
	}

	/**
	 * 玩客 支付回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/wanke", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyWanke(HttpServletRequest request) {
		return platformService.verifyWanke(request);
	}

	/**
	 * 道盟 支付回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/daomeng", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyDaomen(HttpServletRequest request) {
		return platformService.verifyDaomen(request);
	}

	/**
	 * 无限动力 支付回调
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/wxdl", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyWuxiandongli(HttpServletRequest request) {
		return platformService.verifyWuxiandongli(request);
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
		return platformService.getWuxiandongliPaycode(request);
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
		return platformService.verifyXiao7Session(request);
	}

	/**
	 * 小7 充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/xiao7", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyXiao7(HttpServletRequest request) {
		return platformService.verifyXiao7(request);
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
		return platformService.verifyQuickSession(request);
	}

	/**
	 * QuickSDK 充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/quicksdk", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyQuicksdk(HttpServletRequest request) {
		return platformService.verifyQuick(request);
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
		return platformService.verifyYijieSession(request);
	}

	/**
	 * yijieSDK 充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/yijiesdk", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYijiesdk(HttpServletRequest request) {
		return platformService.verifyYijie(request);
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
		return platformService.verifyKuaifaSession(request);
	}

	/**
	 * 快发 充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kuaifa", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyKuaifa(HttpServletRequest request) {
		return platformService.verifyKuaifa(request);
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
		return platformService.verifyFtxSession(request);
	}

	/**
	 * 应用汇充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/ftx", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyFtx(HttpServletRequest request) {
		return platformService.verifyFtx(request);
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
		return platformService.verifyYihuanSession(request);
	}


	@RequestMapping(value = "/yihuan/paycode", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getYihuanCode(HttpServletRequest request){
		return platformService.getYihuanPayCode(request);
	}
	/**
	 * 易幻充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/yihuan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyYihuan(HttpServletRequest request) {
		return  platformService.verifyYihuan(request);
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
		return platformService.verifyHongshouzhiSession(request);
	}

	/**
	 * 红手指充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hongshouzhi", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyHongshouzhi(HttpServletRequest request) {
		return  platformService.verifyHongshouzhi(request);
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
		return  platformService.submitHongshouzhiRole(role);
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
		return platformService.verifyFansdkSession(request);
	}

	/**
	 * 值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fansdk", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyFansdk(HttpServletRequest request) {
		return  platformService.verifyFansdk(request);
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
		return platformService.verifyNiguangSession(request);
	}

	/**
	 * 逆光充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/niguang", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyNiguangsdk(HttpServletRequest request) {
		return  platformService.verifyNiguangsdk(request);
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
		return platformServicePartTwo.verifyAoChuangSession(request);
	}

	/**
	 * 奥创充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/aochuang", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyAoChuangsdk(HttpServletRequest request) {
		return  platformServicePartTwo.verifyAoChuangsdk(request);
	}

	/**
	 * 啪啪游充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/papayou", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyPaPaYou(HttpServletRequest request) {
		return  platformServicePartTwo.verifyPaPaYou(request);
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
		return platformServicePartTwo.verifyTaoShouYouSession(request);
	}

	/**
	 * 淘手游充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/taoshouyou", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyTaoShouYousdk(HttpServletRequest request) {
		return  platformServicePartTwo.verifyTaoShouYousdk(request);
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
		return platformServicePartTwo.verifyMangGuoWanSession(request);
	}

	/**
	 * 芒果玩充值验证
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mangguowan", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyMangGuoWansdk(HttpServletRequest request) {
		return  platformServicePartTwo.verifyMangGuoWansdk(request);
	}

}
