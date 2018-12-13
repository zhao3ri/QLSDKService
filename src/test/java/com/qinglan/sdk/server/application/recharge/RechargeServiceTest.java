package com.qinglan.sdk.server.application.recharge;


import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import com.qinglan.sdk.server.application.ChannelUtilsService;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.RandomTool;
import com.qinglan.sdk.server.common.Sign;
import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;

public class RechargeServiceTest  extends BaseTestCase{

	@Resource
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private BasicRepository basicRepository;
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private ChannelUtilsService channelUtilsService;
	
	private static int i = 0;
	
	@Test
	public void testOrderId(){
		ExecutorService exec=Executors.newFixedThreadPool(10);
		while(true){
			Runnable runnable=new Runnable() {
				@Override
				public void run() {
					getRedisList(RandomTool.getOrderId());
					i++;
					if (i>500) {
						return;
					}
				}
			};
			exec.execute(runnable);
		}
		
	}
	
	
	public void getRedisList(String orderId){
		String temp=redisTemplate.opsForValue().get("orderId_"+orderId);
		if(StringUtils.isNotEmpty(temp)){
			System.out.println(temp+"重复生成ID"+orderId);
		}else{
			redisTemplate.opsForValue().set("orderId_"+orderId,"1");
		}
	}
	
	@Test
	public void gioneeCreateOrder() throws Exception{
		String orderId = "20150403164433893070961699562806";
		String goodsName = "砖石";
		String playerId = "869ECD4607314E34A2CC0F2DCE1BFB68";
		
		Order order = orderService.getOrderByOrderId(orderId);
		ChannelGameEntity platform = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
		
		String notifyUrl = platform.getConfigParamsList().get(0);
		String apiKey = platform.getConfigParamsList().get(1);
		String privateKey = platform.getConfigParamsList().get(4);
		String orderCreateUrl = platform.getConfigParamsList().get(6);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("player_id", playerId);
		params.put("api_key", apiKey);
		params.put("deal_price", String.valueOf(order.getAmount()));
		params.put("deliver_type", "1");
		params.put("out_order_no", orderId);
		params.put("notify_url", notifyUrl);
		params.put("subject", goodsName);
		params.put("submit_time", DateUtils.format(new Date(), DateUtils.yyyyMMddHHmmss).toString());
		params.put("total_fee", String.valueOf(order.getAmount()));
		
		System.out.println("params : " + params);
		System.out.println("privateKey : " + privateKey);
		
		params.put("sign", channelUtilsService.getOrderCreateSign(params, privateKey));
		try {
			String result = HttpUtils.post(orderCreateUrl, JsonMapper.toJson(params));
			logger.debug("gionee order create result : " + result);
		} catch (Exception e) {
			logger.error("gioneeOrderCreate error", e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String url = "http://rsservice.y6.cn/channel/damai";
		
		String appkey = "d7277513eb2898c0f09645c9401bc1ee";
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("orderid", "1457678941766110001");
		params.put("username", "1_10476");
		params.put("appid", "61845");
		params.put("roleid", "e733d1bc-feb0-47c1-9a36-2c2faa885924");
		params.put("serverid", "104");
		params.put("amount", "6");
		params.put("paytime", "1457678941");
		params.put("attach", "20160311144859477499282586816358");
		params.put("productname", "%E5%85%83%E5%AE%9D");
		params.put("appkey", appkey);
		String validSign = Sign.signByMD5Unsort(params, "");
		
		params.remove("appkey");
		params.put("sign", validSign);
		
		String result = HttpUtils.doPost(url, params);
		System.out.println(result);
	}
}
