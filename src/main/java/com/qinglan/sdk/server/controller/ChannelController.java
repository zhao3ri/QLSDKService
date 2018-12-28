package com.qinglan.sdk.server.controller;

import com.qinglan.sdk.server.application.ChannelService;
import com.qinglan.sdk.server.channel.entity.*;
import com.qinglan.sdk.server.channel.impl.*;
import com.qinglan.sdk.server.domain.platform.YaoyueCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/channel")
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Resource
    private ChannelService channelService;

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

    @RequestMapping(value = HmsChannel.PAY_SIGN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String signHuaweiPay(HMSPaySignRequest request) {
        return channelService.signOrderHuawei(request);
    }

    @RequestMapping(value = HmsChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return channelService.huaweiPayReturn(request, response);
    }

    @RequestMapping(value = HmsChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huaweiSession(HMSVerifyRequest req) {
        logger.debug(req.toString());
        return channelService.verifyHuawei(req);
    }

    @RequestMapping(value = YSChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String yeshenPayReturn(HttpServletRequest request) throws IOException {
        return channelService.yeshenPayReturn(request);
    }

    @RequestMapping(value = YSChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String yeshenSession(YSVerifyRequest req) {
        logger.debug(req.toString());
        return channelService.verifyYeshen(req);
    }

    @RequestMapping(value = HuoSdkChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huoSdkPayReturn(HttpServletRequest request) {
        return channelService.huoSdkPayReturn(request);
    }

    @RequestMapping(value = HuoSdkChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String huoSdkSession(HuoSdkVerifyRequest req) {
        logger.debug(req.toString());
        return channelService.verifyHuoSdk(req);
    }


    @RequestMapping(value = HanfengChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String hanfengSession(HanfengVerifyRequest req) {
        logger.debug(req.toString());
        return channelService.verifyHangfeng(req);
    }

    @RequestMapping(value = HanfengChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String hanfengPayReturn(HttpServletRequest request) {
        return channelService.hanfengPayReturn(request);
    }

    @RequestMapping(value = ChongchongChannel.VERIFY_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String chongchongSession(ChongchongVerifyRequest req) {
        logger.debug(req.toString());
        return channelService.verifyChongchong(req);
    }

    @RequestMapping(value = ChongchongChannel.PAY_RETURN_URL, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String chongchongPayReturn(HttpServletRequest request) {
        return channelService.chongchongPayReturn(request);
    }
}
