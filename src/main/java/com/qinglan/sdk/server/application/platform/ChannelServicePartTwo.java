package com.qinglan.sdk.server.application.platform;

import com.qinglan.sdk.server.presentation.channel.entity.HMSPaySignRequest;
import com.qinglan.sdk.server.presentation.channel.entity.HMSVerifyRequest;
import com.qinglan.sdk.server.presentation.platform.dto.AoChuangSession;
import com.qinglan.sdk.server.presentation.platform.dto.MangGuoWanSession;
import com.qinglan.sdk.server.presentation.platform.dto.Six7Session;
import com.qinglan.sdk.server.presentation.platform.dto.TaoShouYouSession;
import com.qinglan.sdk.server.presentation.platform.dto.dtotwo.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by engine on 2016/10/21.
 */
public interface ChannelServicePartTwo {

    String signOrderHuawei(HMSPaySignRequest request);

    String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException;

    String verifyHuawei(HMSVerifyRequest request);

    String verifyAoChuangsdk(HttpServletRequest request);

    String verifyAoChuangSession(AoChuangSession request);

    String verifyPaPaYou(HttpServletRequest request);

    String verifyTaoShouYouSession(TaoShouYouSession request);

    String verifyTaoShouYousdk(HttpServletRequest request);

    String verifyMangGuoWanSession(MangGuoWanSession request);

    String verifyMangGuoWansdk(HttpServletRequest request);

    String verifyQinmuSession(QinmuSession session);

    String verifyQimu(HttpServletRequest request);

    String verifyChangqu(HttpServletRequest request);

    String verifyQitianlediSession(QitianlediSession session);

    String verifyQitianledi(HttpServletRequest request);

    String verifyCangluanSession(CangluanSession session);

    String verifyCangluan(HttpServletRequest request);

    String verifyLingdongSession(LingdongSession session);

    String verifyLingdong(HttpServletRequest request);

    String verifyZhizhuyouSession(ZhizhuyouSession session);

    String verifyZhizhuyou(HttpServletRequest request);

    String verifyXingkongshijieSession(XingkongshijieSession session);

    String verifyXingkongshijiepaySession(XingkongshijiepaySession session);

    String verifyXingkongshijie(HttpServletRequest request);

    String verifyUcPaySign(UcSession session);

    String verifyMoguwanSession(MoguwanSession session);

    String verifyMoguwan(HttpServletRequest request);

    String verifyM2166(HttpServletRequest request);

    String verifySix7Session(Six7Session session);

    String verifySix7(HttpServletRequest request);

    String verifyXmwSession(XmwSession xmwSession);

    String verifyXmw(HttpServletRequest request);

    String createXmwOrder(XmwOrderSession xmwOrderSession);

    String verifyWuKong(HttpServletRequest request);

    String verifyDlSession(DlSession dlSession);

    String createDlPaySign(HttpServletRequest request);

    String verifyDl(HttpServletRequest request);

    String verifyJianguoSession(JianguoSession session);

    String verifyJianguo(HttpServletRequest request);

    String verifyBinghuSession(BinghuSession session);

    String verifyBinghu(HttpServletRequest request);

    String verifyDxSession(DianxinSession session);

    String verifyDxToken(DianxinSession session);

    String verifyUuSyzhuSession(UuSyzhuSession session);

    String verifyUuSyzhu(HttpServletRequest request);

    String verifyGuangFanSession(GuangFanSession session);

    String createGuangFanOrderId(GuangFanSession session);

    String verifyGuangFan(HttpServletRequest request);
}
