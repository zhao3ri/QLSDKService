package com.qinglan.sdk.server.application.basic.listener;


import javax.annotation.Resource;

import com.qinglan.sdk.server.data.annotation.event.EventListener;
import com.qinglan.sdk.server.data.infrastructure.event.EventPublisher;
import com.qinglan.sdk.server.application.basic.GameService;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.domain.basic.Game;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.basic.event.CPAsyncNotifyEvent;
import com.qinglan.sdk.server.domain.basic.event.CPNotifyEvent;
import com.qinglan.sdk.server.domain.basic.event.OrderPayEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component @EventListener
public class CPNotifyListener {
	private final static Logger log = LoggerFactory.getLogger(CPNotifyListener.class);
	
	@Resource
	private OrderService orderService;
	@Resource
	private GameService gameService;
	@Resource
	private EventPublisher publisher;
	
	@EventListener(asynchronous=true)
	public void handleCPAsyncNotifyEvent(CPAsyncNotifyEvent event){
		//通知游戏发金额
		if(!notice(event.getHelper())){
			orderService.notifyResend(event.getHelper(), "重发通知");
			//二次发送
			publisher.publish(new OrderPayEvent(event.getHelper()));
		}
	}
	
	@EventListener
	public void handleCPNotifyEvent(CPNotifyEvent event){
		notice(event.getHelper());
	}
	
	public boolean notice(String orderId){
		Order order=orderService.getOrderByOrderId(orderId);
		if(order == null){
			log.info("订单号："+orderId+"为无效订单！！！");
			return false;
		}
		try{
			if(order.getStatus() != Order.ORDER_STATUS_PAYMENT_SUCCESS ||
					!(order.getNotifyStatus() == Order.ORDER_NOTIFY_STATUS_WAITING || order.getNotifyStatus() == Order.ORDER_NOTIFY_STATUS_RESEND)){
				log.info("订单"+order.getOrderId()+"所处的状态不能完成通知到CP！！！");
				return false;
			}
			
			if(!isURL(order.getNotifyUrl())){
				orderService.notifyFail(order.getOrderId(), "通知地址有误");
				return false;
			}
			
			Game game=gameService.getGameById(order.getGameId());
			if(game == null){
				orderService.notifyFail(order.getOrderId(), "未知应用标识");
				return false;
			}
			boolean ret = gameService.notifyGame(order, game);
			if(ret){
				orderService.notifySuccess(order.getOrderId());
			}else{
				orderService.notifyFail(order.getOrderId(),"充值失败");
			}
			return true;
		}catch(Exception e){
			log.error("订单："+order.getOrderId()+",通知CP异常", e);
			orderService.notifyFail(order.getOrderId(), "通知异常");
			return false;
		}
	}
	
	/**
	 * URL验证
	 * @param str
	 * @return
	 */
	public static boolean isURL(String str){
		if(StringUtils.isBlank(str))
			return false;
		if(str.matches("[a-zA-z]+://[^\\s]+"))
			return true;
		return false;
	}
	
}
