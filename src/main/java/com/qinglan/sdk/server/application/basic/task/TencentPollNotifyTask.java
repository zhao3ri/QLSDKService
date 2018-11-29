package com.qinglan.sdk.server.application.basic.task;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.data.infrastructure.support.InstanceFactory;
import com.qinglan.sdk.server.platform.qq.OpenApiV3;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.application.basic.redis.RedisUtil;
import com.qinglan.sdk.server.presentation.platform.dto.TencentNotifyParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TencentPollNotifyTask {

	private static final Logger logger = LoggerFactory.getLogger(TencentPollNotifyTask.class);
	private static int TWENTY_MILLIS = 1000 * 10;
	private static int MAX_POLL_TIMES = 10;
	private Timer timer;
	
	public void init() {
		timer = new Timer();
		timer.schedule(new NoticeSchedule(), TWENTY_MILLIS, TWENTY_MILLIS);
		logger.debug("schedule initial success");
	}
	
	public void destroy() {
		if(null != timer) {
			timer.cancel();
			logger.debug("schedule cancel success");
		}
	}
	
	private class NoticeSchedule extends TimerTask  {
		@Override @SuppressWarnings({ "rawtypes", "deprecation" })
		public void run() {
			RedisUtil redisUtil = InstanceFactory.getInstance(RedisUtil.class);
			OrderService orderService = InstanceFactory.getInstance(OrderService.class);
			try {
				for (int i = 0; i < 2; i++) {
					String value = redisUtil.getBrpop("tencent_poll_order");
					
					if(StringUtils.isBlank(value))
						continue;
					
					logger.debug("tencent poll value: "+MAX_POLL_TIMES + value );
					
					TencentNotifyParams notifyParams = JsonMapper.toObject(value, TencentNotifyParams.class);
					if (notifyParams.getPollTimes() > MAX_POLL_TIMES) {
						continue;
					}
					
					String ts = Long.toString(System.currentTimeMillis() / 1000);
			        String scriptName = "/mpay/pay_m";
			        String protocol = "https";
					
					
					OpenApiV3 sdk = new OpenApiV3(notifyParams.getAppid(), notifyParams.getAppkey());
			        sdk.setServerName(notifyParams.getServerName());

			        HashMap<String,String> cookie = new HashMap<String, String>();
			        cookie.put("session_id", notifyParams.getSession_id());
			        cookie.put("session_type", notifyParams.getSession_type());
			        cookie.put("org_loc", URLEncoder.encode(scriptName));
			        
			        HashMap<String,String> params = new HashMap<String, String>();
			        params.put("appid", notifyParams.getAppid());
			        params.put("openid", notifyParams.getOpenid());
			        params.put("openkey", notifyParams.getOpenkey());
			        params.put("pay_token", StringUtils.equals("openid", notifyParams.getSession_id()) ? notifyParams.getPay_token() : "");
			        params.put("ts", ts);
			        params.put("pf", notifyParams.getPf());
			        params.put("pfkey", notifyParams.getPfkey());
			        params.put("zoneid", notifyParams.getZoneid());
			        params.put("amt", String.valueOf(notifyParams.getAmount()/notifyParams.getGameCoinRatio()));
					params.put("billno",notifyParams.getOrderId());
			        String resp = sdk.api_pay(scriptName, cookie, params, protocol);
					logger.debug("roll queque:"+resp+"--->"+notifyParams.getPollTimes());
					if (StringUtils.isBlank(resp)) {
						continue;
					}
					
					Map respMap = JsonMapper.toObject(resp, Map.class);
					String ret = respMap.get("ret").toString();
					if ("1004".equals(ret)) {
						notifyParams.setPollTimes(notifyParams.getPollTimes() + 1);
						String json = JsonMapper.toJson(notifyParams);
						redisUtil.setLpush("tencent_poll_order", json);
					}else if ("0".equals(ret)) {
						orderService.paySuccess(notifyParams.getOrderId());
					}else {
						logger.debug("tencent poll back code: " + ret);
					}
				}
			} catch (Exception e) {
				logger.error("tencent poll task error", e);
			}
		}
	}
}
