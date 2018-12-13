package com.qinglan.sdk.server.presentation;

import com.qinglan.sdk.server.application.ChannelServicePartTwo;
import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.entity.YSVerifyRequest;
import com.qinglan.sdk.server.presentation.channel.impl.HmsChannel;
import com.qinglan.sdk.server.presentation.channel.impl.YSChannel;
import com.qinglan.sdk.server.presentation.dto.channel.Six7Session;
import com.qinglan.sdk.server.presentation.dto.channel.channel2.*;
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
@RequestMapping("/channel2")
public class ChannelControllerPartTwo {

    private static final Logger logger = LoggerFactory.getLogger(ChannelControllerPartTwo.class);

    @Resource
    private ChannelServicePartTwo channelServiceTwo;

    @RequestMapping(value = HmsChannel.PAY_SIGN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String signHuaweiPay(HMSPaySignRequest request) {
        return channelServiceTwo.signOrderHuawei(request);
    }

    @RequestMapping(value = HmsChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return channelServiceTwo.huaweiPayReturn(request, response);
    }

    @RequestMapping(value = HmsChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huaweiSession(HMSVerifyRequest req) {
        logger.debug(req.toString());
        return channelServiceTwo.verifyHuawei(req);
    }

    @RequestMapping(value = YSChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String yeshenPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return channelServiceTwo.yeshenPayReturn(request);
    }

    @RequestMapping(value = YSChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String yeshenSession(YSVerifyRequest req) {
        logger.debug(req.toString());
        return channelServiceTwo.verifyYeshen(req);
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
        return channelServiceTwo.verifyQinmuSession(request);
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
        return channelServiceTwo.verifyQimu(request);
    }

    @RequestMapping(value = "/changqu", produces = "text/html;charset=UTF-8")
    public String verifyChangqu(HttpServletRequest request) {
        return channelServiceTwo.verifyChangqu(request);
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
        return channelServiceTwo.verifyQitianlediSession(request);
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
        return channelServiceTwo.verifyQitianledi(request);
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
        return channelServiceTwo.verifyCangluanSession(request);
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
        return channelServiceTwo.verifyCangluan(request);
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
        return channelServiceTwo.verifyLingdongSession(request);
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
        return channelServiceTwo.verifyLingdong(request);
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
        return channelServiceTwo.verifyZhizhuyouSession(request);
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
        return channelServiceTwo.verifyZhizhuyou(request);
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
        return channelServiceTwo.verifyXingkongshijieSession(request);
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
        return channelServiceTwo.verifyXingkongshijiepaySession(request);
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
        return channelServiceTwo.verifyXingkongshijie(request);
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
        return channelServiceTwo.verifyUcPaySign(request);
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
        return channelServiceTwo.verifyMoguwanSession(request);
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
        return channelServiceTwo.verifyMoguwan(request);
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
        return channelServiceTwo.verifyM2166(request);
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
        return channelServiceTwo.verifySix7Session(request);
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
        return channelServiceTwo.verifySix7(request);
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
        return channelServiceTwo.verifyXmwSession(request);
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
        return channelServiceTwo.createXmwOrder(request);
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
        return channelServiceTwo.verifyXmw(request);
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
        return channelServiceTwo.verifyWuKong(request);
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
        return channelServiceTwo.verifyDlSession(dlSession);
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
        return channelServiceTwo.createDlPaySign(request);
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
        return channelServiceTwo.verifyDl(request);
    }

    @RequestMapping(value = "/jianguo/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyJianguoSession(JianguoSession session) {
        return channelServiceTwo.verifyJianguoSession(session);
    }

    @RequestMapping(value = "/jianguo", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyJianguo(HttpServletRequest request) {
        return channelServiceTwo.verifyJianguo(request);
    }

    @RequestMapping(value = "/binghu/session", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyBinghuSession(BinghuSession session) {
        return channelServiceTwo.verifyBinghuSession(session);
    }

    @RequestMapping(value = "/binghu", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verifyBinghu(HttpServletRequest request) {
        return channelServiceTwo.verifyBinghu(request);
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
        return channelServiceTwo.verifyDxSession(session);
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
        return channelServiceTwo.verifyDxToken(session);
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
        return channelServiceTwo.verifyUuSyzhuSession(session);
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
        return channelServiceTwo.verifyUuSyzhu(request);
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
        return channelServiceTwo.verifyGuangFanSession(session);
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
        return channelServiceTwo.createGuangFanOrderId(session);
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
        return channelServiceTwo.verifyGuangFan(request);
    }
}
