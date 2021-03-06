package com.qinglan.sdk.server.application;

import com.qinglan.sdk.server.channel.entity.*;
import com.qinglan.sdk.server.domain.platform.YaoyueCallback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ChannelService {

    String verifyYaoyue(YaoyueCallback zhidian);

    String ucPayReturn(HttpServletRequest request);

    String verifyUcSession(UCVerifyRequest ucSession);

    String signOrderHuawei(HMSPaySignRequest request);

    String huaweiPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException;

    String verifyHuawei(HMSVerifyRequest request);

    String yeshenPayReturn(HttpServletRequest request);

    String verifyYeshen(YSVerifyRequest request);

    String huoSdkPayReturn(HttpServletRequest request);

    String verifyHuoSdk(HuoSdkVerifyRequest request);

    String hanfengPayReturn(HttpServletRequest request);

    String verifyHangfeng(HanfengVerifyRequest request);

    String chongchongPayReturn(HttpServletRequest request);

    String verifyChongchong(ChongchongVerifyRequest request);
}
