package com.qinglan.sdk.server.platform.service;

import com.qinglan.sdk.server.platform.domain.dto.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by engine on 2016/10/21.
 */
public interface PlatformServicePartTwo {

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

    String verifySix7Session(Six7Session session) ;
    String verifySix7(HttpServletRequest request) ;

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
