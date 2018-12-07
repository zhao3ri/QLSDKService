package com.qinglan.sdk.server.application.platform;

import com.qinglan.sdk.server.domain.platform.YaoyueCallback;
import com.qinglan.sdk.server.domain.platform.YouleCallback;
import com.qinglan.sdk.server.presentation.channel.entity.UCVerifyRequest;
import com.qinglan.sdk.server.presentation.platform.dto.*;

import javax.servlet.http.HttpServletRequest;

public interface ChannelService {
	
	String verifyYaoyue(YaoyueCallback zhidian);
	
	String ucPayReturn(HttpServletRequest request);
	
	String verifyUcSession(UCVerifyRequest ucSession);
	
	String verifyXiaomi(HttpServletRequest request);
	
	String verifyXiaomiSession(XiaomiSession xiaomiSession);
	
	String verifyQihooSession(QihooSession qihuSession);
	
	String verifyQihoo(HttpServletRequest request);
	
	String verifyBaiduSession(BaiduSession baiduSession);
	
	String verifyBaidu(HttpServletRequest request);

	String verifyAnzhiSession(AnzhiSession anzhiSession);

	String verifyAnzhi(HttpServletRequest request);

	String verifySougouSession(SougouSession sougouSession);

	String verifySougou(HttpServletRequest request);

	String verify91Session(Varify91Session session);

	String verify91(HttpServletRequest request);

	String verifyWdjSession(WdjSession session);

	String verifyWdj(HttpServletRequest request);

	String verifyGioneeSession(GioneeSession session);

	String verifyGionee(HttpServletRequest request);

	String verifyVivoSession(VivoSession session);

	String vivoPaySign(VivoPaySign vivoPaySign);
	
	String verifyVivo(HttpServletRequest request);

	String verifyAppchinaSession(AppchinaSession session);

	String verifyAppchina(HttpServletRequest request);

	String verifyOuwanSession(OuwanSession session);

	String verifyOuwan(HttpServletRequest request);

	String verifyOppoSession(OppoSession session);

	String verifyOppo(HttpServletRequest request);

	String verifyKupaiSession(KupaiSession session);

	String verifyKupai(HttpServletRequest request);
	
	String verifyDownjoy(HttpServletRequest request);

	String gioneeOrderCreate(HttpServletRequest request);

	String verifyYoukuSession(YoukuSession session);

	String verifyYouku(HttpServletRequest request);

	String verifyJifengSession(JifengSession session);

	String verifyJifeng(HttpServletRequest request);
	
	String signHTCPayContent(HttpServletRequest request);
	
	String verifyHTCSession(HTCSession session);

	String verifyHTC(HttpServletRequest request);

	String verifyMeizuSession(MeizuSession session);

	String meizuPaySign(HttpServletRequest request);
	
	String verifyMeizu(HttpServletRequest request);

	String verifyNduo(HttpServletRequest request);

	String verifyYoulongSession(YoulongSession session);

	String verifyYoulong(HttpServletRequest request);

	String verifyLenovoSession(LenovoSession session);

	String verifyLenovo(HttpServletRequest request);

	String verifyKudong(HttpServletRequest request);

	String verifyLetv(HttpServletRequest request);

	String verify19meng(HttpServletRequest request);

	String verifyKuwo(HttpServletRequest request);
	
	String verifyMumayi(HttpServletRequest request);
	
	String verifyPlaySession(PlaySession session);

	String verifyPlay(HttpServletRequest request);
	
	String verifyPlaySms(HttpServletRequest request);

	String verifyJiuduSession(JiuduSession session);

	String verifyJiudu(HttpServletRequest request);

	String verifyPaojiaoSession(PaojiaoSession session);

	String verifyPaojiao(HttpServletRequest request);

	String verifyQixiazi(HttpServletRequest request);

	String verifyKuaiyongSession(KuaiyongSession session);

	String verifyKuaiyong(HttpServletRequest request);

	String verifyHuaweiSession(HuaweiSession session);

	String verifyHuawei(HttpServletRequest request);

	String huaweiPaySign(HttpServletRequest request);

	String verifyFtnnSession(FtnnSession session);

	String verifyFtnn(HttpServletRequest request);

	String verifyTsSession(TsSession session);

	String verifyTs(HttpServletRequest request);

	String verifyMuzhiSession(MuzhiSession session);

	String verifyMuzhi(HttpServletRequest request);

	String verifyMuzhiwanSession(MuzhiwanSession session);

	String verifyMuzhiwan(HttpServletRequest request);

	String verifyKaopu(HttpServletRequest request);

	String verifyGametanzi(HttpServletRequest request);

	String verifyWeidongSession(WeidongSession session);

	String verifyWeidong(HttpServletRequest request);

	String verifyEdgSession(EdgSession session);
	
	String verifyEdg(HttpServletRequest request);

	String verifyGametanziSession(GametanziSession session);

	String verifyTencent(HttpServletRequest request);

	String verifyTencent2(HttpServletRequest request);
	
	String verifyKaopuSession(KaopuSession session);

	String verifyUucunSession(UucunSession session);

	String verifyUucun(HttpServletRequest request);

	String verifyKaiuc(HttpServletRequest request);

	String verifyLiebaoSession(LiebaoSession session);

	String verifyLiebao(HttpServletRequest request);

	String verifyLeshanSession(LeshanSession session);

	String verifyLeshan(HttpServletRequest request);

	String verifyAtet(HttpServletRequest request);

	String verifyShenqi(HttpServletRequest request);

	String atetPaypoing(HttpServletRequest request);

	String verifyHaimaSession(HaimaSession session);

	String verifyHaima(HttpServletRequest request);

	String pengyouwanPaypoing(HttpServletRequest request);
	
	String verifyPengyouwan(HttpServletRequest request);

	String verify3899Session(TennSession session);

	String verify3899(HttpServletRequest request);

	String verifyLiulianSession(LiulianSession session);

	String verifyLiulian(HttpServletRequest request);

	String verifyXunlei(HttpServletRequest request);

	String verifyGuopanSession(GuopanSession session);

	String verifyGuopan(HttpServletRequest request);

	String verifyQxfySession(QxfySession session);

	String verifyQxfy(HttpServletRequest request);

	String verify19game(HttpServletRequest request);

	String verifyLongxiangSession(LongxiangSession session);
	
	String verifyLongxiang(HttpServletRequest request);

	String verifyLehihiSession(LehihiSession session);

	String verifyLehihi(HttpServletRequest request);

	String verifyKoudai(HttpServletRequest request);

	String verifyYouleSession(YouleSession session);

	String verifyYoule(YouleCallback callback);

	String verifyQiutuSession(QiutuSession session);

	String verifyQiutu(HttpServletRequest request);

	String verifyYuewanSession(YuewanSession session);

	String verifyYuewan(HttpServletRequest request);
	String verifyWsx(HttpServletRequest request);

	String verifyIveryoneSession(IveryoneSession session);

	String verifyIveryone(HttpServletRequest request);

	String verifyDyhdSession(DyhdSession session);

	String verifyDyhd(HttpServletRequest request);

	String verify7723Session(SsttSession session);

	String verify7723(HttpServletRequest request);

	String verifyQiqileSession(QiqileSession session);

	String verifyQiqile(HttpServletRequest request);

	String verifyMogeSession(MogeSession session);

	String verifyMoge(HttpServletRequest request);

	String miguLoginNotify(HttpServletRequest request);

	String verifyMigu(HttpServletRequest request);

	String verifyTuuSession(TuuSession session);

	String verifyTuu(HttpServletRequest request);

	String verifyMoyoyo(HttpServletRequest request);

	String verifyDamai(HttpServletRequest request);

	String verifyShuowanSession(ShuowanSession session);

	String verifyShuowan(HttpServletRequest request);

	String verifyFirstappSession(FirstappSession session);

	String verifyFirstapp(HttpServletRequest request);

	String qbaoLogin(HttpServletRequest request);

	String qbaoPaySign(HttpServletRequest request);
	
	String rechargeCallBackQbao(HttpServletRequest request);

	String rechargeCallBackQipa(HttpServletRequest request);

	String verifyAipuSession(AipuSession session);

	String verifyAipu(HttpServletRequest request);
	
	String verifyShunwangSession(HttpServletRequest request);
	
	String verifyShunwang(HttpServletRequest request);
	
	String verifyZhuoyi(HttpServletRequest request);

	String verifyYunxiaotanSession(YunxiaotanSession session);
	String verifyYunxiaotan(HttpServletRequest request);
	String verifyGuangzhoupeidui(HttpServletRequest request);
	String verifyDianyoo(HttpServletRequest request);

	String verifyhongchongSession(ChongchongSession request);
	String verifyChongchong(HttpServletRequest request);
	String verifyQishi(HttpServletRequest request);
	String verifyTt(HttpServletRequest request);
	String verifyYeshen(HttpServletRequest request);
	String verifyLewanSession(LewanSession session);
	String verifyLewan(HttpServletRequest request);
	String verifyWanke(HttpServletRequest request);
	String verifyDaomen(HttpServletRequest request);
	String verifyWuxiandongli(HttpServletRequest request);
	String getWuxiandongliPaycode(HttpServletRequest request);
	String verifyXiao7Session(Xiao7Session session);
	String verifyXiao7(HttpServletRequest request);
	String verifyQuickSession(QuickSdkSession session);
	String verifyQuick(HttpServletRequest request);
	String verifyYijieSession(YijieSdkSession session);
	String verifyYijie(HttpServletRequest request);
	String verifyKuaifaSession(KuaifaSession session);
	String verifyKuaifa(HttpServletRequest request);
	String verifyFtxSession(FtxSession session);
	String verifyFtx(HttpServletRequest request);
	String verifyYihuanSession(YihuanSession session);
	String verifyYihuan(HttpServletRequest request);
	String getYihuanPayCode(HttpServletRequest request);
	String verifyHongshouzhiSession(HongshouzhiSession session);
	String verifyHongshouzhi(HttpServletRequest request);
	String submitHongshouzhiRole(HongShouZhiRole request);
	String verifyFansdkSession(FansdkSession session);
	String verifyFansdk(HttpServletRequest request);

	String verifyNiguangsdk(HttpServletRequest request);

    String verifyNiguangSession(NiguangSession request);

    String bingquBaowanLogin(HttpServletRequest request);

    String bingquBaowanPaySign(HttpServletRequest request);

    String rechargeCallBackbingquBaowan(HttpServletRequest request);

	String verifyTtSession(TtSession ttSession);

	String rechargeTt(HttpServletRequest request);

}
