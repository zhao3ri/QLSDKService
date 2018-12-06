package com.qinglan.sdk.server.presentation.platform;

import com.qinglan.sdk.server.application.platform.PlatformServicePartTwo;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSVerifyRequest;
import com.qinglan.sdk.server.presentation.platform.dto.Six7Session;
import com.qinglan.sdk.server.presentation.platform.dto.dtotwo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by engine on 2016/10/21.
 */

@Controller
@RequestMapping("/platform2")
public class PlatformControllerPartTwo {

    private static final Logger logger = LoggerFactory.getLogger(PlatformControllerPartTwo.class);

    @Resource
    private PlatformServicePartTwo platformServiceTwo;

    @RequestMapping(value = "/hms/pay/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String signHuaweiPay(HMSPaySignRequest request) {
        return platformServiceTwo.signOrderHuawei(request);
    }

    @RequestMapping(value = "/hms/pay/return", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return platformServiceTwo.huaweiPayReturn(request, response);
    }

    @RequestMapping(value = "/hms/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huaweiSession(HMSVerifyRequest req) {
        logger.debug(req.toString());
        return platformServiceTwo.verifyHuawei(req);
    }

    /**
     * 青木 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qingmu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMangGuoWanSession(QinmuSession request) {
        return platformServiceTwo.verifyQinmuSession(request);
    }

    /**
     * 芒果玩充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qingmu", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMangGuoWansdk(HttpServletRequest request) {
        return platformServiceTwo.verifyQimu(request);
    }

    @RequestMapping(value = "/changqu", produces = "text/html;charset=UTF-8")
    public String verifyChangqu(HttpServletRequest request) {
        return platformServiceTwo.verifyChangqu(request);
    }

    /**
     * 奇天乐地 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qitianledi/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQitianlediSession(QitianlediSession request) {
        return platformServiceTwo.verifyQitianlediSession(request);
    }

    /**
     * 奇天乐地充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/qitianledi", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyQitianledisdk(HttpServletRequest request) {
        return platformServiceTwo.verifyQitianledi(request);
    }

    /**
     * 苍鸾 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/cangluan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyCangluanSession(CangluanSession request) {
        return platformServiceTwo.verifyCangluanSession(request);
    }

    /**
     * 苍鸾充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/cangluan", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyCangluan(HttpServletRequest request) {
        return platformServiceTwo.verifyCangluan(request);
    }

    /**
     * 灵动 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lingdong/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyLingdongSession(LingdongSession request) {
        return platformServiceTwo.verifyLingdongSession(request);
    }

    /**
     * 灵动充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lingdong", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String verifyLingdong(HttpServletRequest request) {
        return platformServiceTwo.verifyLingdong(request);
    }

    /**
     * 智蛛游 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/zhizhuyou/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyZhizhuyouSession(ZhizhuyouSession request) {
        return platformServiceTwo.verifyZhizhuyouSession(request);
    }

    /**
     * 智蛛游充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/zhizhuyou", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyZhizhuyou(HttpServletRequest request) {
        return platformServiceTwo.verifyZhizhuyou(request);
    }

    /**
     * 星空世界 SDK session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xingkongshijie/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXingkongshijieSession(XingkongshijieSession request) {
        return platformServiceTwo.verifyXingkongshijieSession(request);
    }

    /**
     * 星空世界 充值 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xingkongshijiepay/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXingkongshijiepaySession(XingkongshijiepaySession request) {
        return platformServiceTwo.verifyXingkongshijiepaySession(request);
    }

    /**
     * 星空世界充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xingkongshijie", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXingkongshijie(HttpServletRequest request) {
        return platformServiceTwo.verifyXingkongshijie(request);
    }

    /**
     * UC SDK 签名验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uc/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyUcPaySign(UcSession request) {
        return platformServiceTwo.verifyUcPaySign(request);
    }

    /**
     * 蘑菇玩 登录 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/moguwan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMoguwanSession(MoguwanSession request) {
        return platformServiceTwo.verifyMoguwanSession(request);
    }

    /**
     * 蘑菇玩充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/moguwan", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyMoguwan(HttpServletRequest request) {
        return platformServiceTwo.verifyMoguwan(request);
    }

    /**
     * 2166充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/m2166", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyM2166(HttpServletRequest request) {
        return platformServiceTwo.verifyM2166(request);
    }

    /**
     * Six7 登录 session验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/six7/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifySixSession(Six7Session request) {
        return platformServiceTwo.verifySix7Session(request);
    }

    /**
     * 蘑菇玩充值验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/six7", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifySix7(HttpServletRequest request) {
        return platformServiceTwo.verifySix7(request);
    }

    /**
     * 熊猫玩 session 验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xmw/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXmwSession(XmwSession request) {
        return platformServiceTwo.verifyXmwSession(request);
    }

    /**
     * 熊猫玩创建订单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xmw/createOrder", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createXmwOrder(XmwOrderSession request) {
        return platformServiceTwo.createXmwOrder(request);
    }

    /**
     * 熊猫玩 session 验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/xmw", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyXmw(HttpServletRequest request) {
        return platformServiceTwo.verifyXmw(request);
    }

    /**
     * 悟空游戏支付结果回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/wukong", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String rechargeCallBackWuKong(HttpServletRequest request) {
        return platformServiceTwo.verifyWuKong(request);
    }

    /**
     * 当乐网验证
     *
     * @param dlSession
     * @return
     */
    @RequestMapping(value = "/dl/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDlSession(DlSession dlSession) {
        return platformServiceTwo.verifyDlSession(dlSession);
    }

    /**
     * 当乐网生成支付签名
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/dl/sign", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createDlPaySign(HttpServletRequest request) {
        return platformServiceTwo.createDlPaySign(request);
    }

    /**
     * 当乐道具购买回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/dl", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDl(HttpServletRequest request) {
        return platformServiceTwo.verifyDl(request);
    }

    @RequestMapping(value = "/jianguo/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyJianguoSession(JianguoSession session) {
        return platformServiceTwo.verifyJianguoSession(session);
    }

    @RequestMapping(value = "/jianguo", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyJianguo(HttpServletRequest request) {
        return platformServiceTwo.verifyJianguo(request);
    }

    @RequestMapping(value = "/binghu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyBinghuSession(BinghuSession session) {
        return platformServiceTwo.verifyBinghuSession(session);
    }

    @RequestMapping(value = "/binghu", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyBinghu(HttpServletRequest request) {
        return platformServiceTwo.verifyBinghu(request);
    }

    /**
     * 电信爱游戏 授权码兑换令牌
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/dianxin/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDxSession(DianxinSession session) {
        return platformServiceTwo.verifyDxSession(session);
    }

    /**
     * 电信爱游戏 访问令牌鉴权
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/dianxin/token", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyDxToken(DianxinSession session) {
        return platformServiceTwo.verifyDxToken(session);
    }

    /**
     * UU手游猪 用户有效性验证
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/uusyzhu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyUuSyzhuSession(UuSyzhuSession session) {
        return platformServiceTwo.verifyUuSyzhuSession(session);
    }

    /**
     * UU手游猪 订单通知(回调)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uusyzhu", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyUuSyzhu(HttpServletRequest request) {
        return platformServiceTwo.verifyUuSyzhu(request);
    }

    /**
     * 广凡 器登录认证
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/guangfan/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyGuangFanSession(GuangFanSession session) {
        return platformServiceTwo.verifyGuangFanSession(session);
    }

    /**
     * 广凡 获取订单号
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/guangfan/createOrderId", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createGuangFanOrderId(GuangFanSession session) {
        return platformServiceTwo.createGuangFanOrderId(session);
    }

    /**
     * 广凡 支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/guangfan", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyGuangFan(HttpServletRequest request) {
        return platformServiceTwo.verifyGuangFan(request);
    }
}
