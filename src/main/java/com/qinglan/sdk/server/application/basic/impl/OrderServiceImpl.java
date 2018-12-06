package com.qinglan.sdk.server.application.basic.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.qinglan.sdk.server.common.RandomTool;
import com.qinglan.sdk.server.data.infrastructure.event.EventPublisher;
import com.qinglan.sdk.server.Constants;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.basic.Platform;
import com.qinglan.sdk.server.domain.basic.PlatformGame;
import com.qinglan.sdk.server.domain.basic.event.CPAsyncNotifyEvent;
import com.qinglan.sdk.server.domain.basic.event.OraderCountEvent;
import com.qinglan.sdk.server.domain.basic.event.OrderGenerateEvent;
import com.qinglan.sdk.server.presentation.basic.dto.OrderGeneratePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Resource
    private BasicRepository basicRepository;
    @Resource
    private EventPublisher publisher;

    @Override
    public String saveOrder(OrderGeneratePattern params) {
        for (int i = 0; i < 3; i++) {
            try {
                Order order = assembledOrder(params);
                if (basicRepository.saveOrder(order) == 1) {
                    return order.getOrderId();
                }
            } catch (Exception e) {
                logger.error("create order exception, try again", e);
            }
        }
        logger.error("create order failed, params: {}", params);
        return null;
    }

    private Order assembledOrder(OrderGeneratePattern params) {
        Order order = new Order();
        order.setGameId(params.getGameId());
        order.setPlatformId(params.getPlatformId());
        order.setUid(params.getUid());
        order.setZoneId(params.getZoneId());
        order.setRoleId(params.getRoleId());
        order.setRoleName(params.getRoleName());

        if (Constants.QBAO_PLATFORM_ID == params.getPlatformId()) {
            order.setOrderId(RandomTool.getOrderId(3));
        } else {
            order.setOrderId(RandomTool.getOrderId());
        }

        order.setCpOrderId(params.getCpOrderId());
        order.setExtInfo(params.getExtInfo());
        order.setAmount(params.getAmount());
        order.setNotifyUrl(params.getNotifyUrl());
        order.setFixed(params.getFixed());
        order.setDeviceId(params.getDeviceId());
        order.setClientType(params.getClientType());
        order.setStatus(Order.ORDER_STATUS_SUBMIT_SUCCESS);
        order.setNotifyStatus(Order.ORDER_NOTIFY_STATUS_DEFAULT);
        order.setCreateTime(new Date());
        order.setGold(params.getGold() == null ? 0 : params.getGold());
        order.setSelfpay(params.getSelfpay());
        return order;
    }

    @Override
    public Order getOrderByOrderId(String orderId) {
        return basicRepository.getOrderByOrderId(orderId);
    }

    @Override
    public int payFail(String orderId, String errorMsg) {
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null)
            return Order.INVALID;
        if (Order.ORDER_STATUS_PAYMENT_SUCCESS == order.getStatus())
            return Order.REPEAT;
        if (Order.ORDER_STATUS_SUBMIT_SUCCESS != order.getStatus())
            return Order.INVALID;

        order.setStatus(Order.ORDER_STATUS_PAYMENT_FAIL);
        order.setUpdateTime(new Date());
        order.setErrorMsg(errorMsg);
        if (basicRepository.updateStatusPay(order) < 1)
            return Order.FAIL;

        return Order.SUCCESS;
    }

    @Override
    public int paySuccess(String orderId) {
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null)
            return Order.INVALID;
        if (Order.ORDER_STATUS_PAYMENT_SUCCESS == order.getStatus())
            return Order.REPEAT;
        if (Order.ORDER_STATUS_SUBMIT_SUCCESS != order.getStatus())
            return Order.INVALID;
        order.setStatus(Order.ORDER_STATUS_PAYMENT_SUCCESS);
        order.setNotifyStatus(Order.ORDER_NOTIFY_STATUS_WAITING);
        order.setUpdateTime(new Date());
        order.setErrorMsg(null);
        if (basicRepository.updateStatusPay(order) < 1) {
            return Order.FAIL;
        }

        /**
         * 扣减渠道对应金额
         */
        PlatformGame platformGame = basicRepository.getByPlatformAndGameId(order.getPlatformId(), order.getGameId());
        logger.info("appId:" + platformGame.getGameId() + " platformid:" + platformGame.getPlatformId());
        int updateBalance = -1;
        int i = 0;
        while (i < 3) {
            updateBalance = this.updatePlatformBalance(order.getAmount(), platformGame);
            if (updateBalance != 2) {
                break;
            } else {
                i++;
            }
        }
        if (updateBalance == 0 || updateBalance == 2) {
            logger.info("扣减失败:" + updateBalance);
            return Order.BALANCE_ERROR;
        }
        //异步发送金币
        publisher.publish(new CPAsyncNotifyEvent(order.getOrderId()));
        //记录成功日志
        publisher.publish(new OrderGenerateEvent(changeOrder(order)));
        //统计消费
        publisher.publish(new OraderCountEvent(changeOrder(order)));

        return Order.SUCCESS;
    }


    /**
     * @param money
     * @return 0 ：余额不足 2：扣款失败  1：成功
     */
    public int updatePlatformBalance(int money, PlatformGame platformGame) {
        Platform platform = basicRepository.getPlatform(platformGame.getPlatformId());
        logger.info("pre Money:" + money + " discount:" + platformGame.getDiscount());
        money = money * platformGame.getDiscount() / 100;
        logger.info("after Money:" + money + " discount:" + platformGame.getDiscount());
        if (money > platform.getBalance()) {
            return 0;
        }
        /**
         * TODO 发余额报警信息
         */
        int balance = platform.getBalance() - money;
        platform.setBalance(balance);
        int newversion = platform.getVersion() + 1;
        platform.setNewversion(newversion);
        int result = basicRepository.updatePlatformBalance(platform);
        if (result > 0) {
            return 1;
        } else {
            return 2;
        }
    }

    private OrderGeneratePattern changeOrder(Order order) {
        OrderGeneratePattern params = new OrderGeneratePattern();
        params.setGameId(order.getGameId());
        params.setPlatformId(order.getPlatformId());
        params.setUid(order.getUid());
        params.setZoneId(order.getZoneId());
        params.setRoleId(order.getRoleId());
        params.setCpOrderId(order.getCpOrderId());
        params.setExtInfo(order.getExtInfo());
        params.setAmount(order.getAmount());
        params.setNotifyUrl(order.getNotifyUrl());
        params.setFixed(order.getFixed());
        params.setDeviceId(order.getDeviceId());
        params.setClientType(order.getClientType());
        params.setOrderId(order.getOrderId());
        return params;
    }

    @Override
    public int notifyResend(String orderId, String errorMsg) {
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null)
            return Order.INVALID;
        if (Order.ORDER_STATUS_PAYMENT_SUCCESS != order.getStatus())
            return Order.INVALID;

        if (Order.ORDER_NOTIFY_STATUS_SUCCESS == order.getNotifyStatus())
            return Order.REPEAT;

        order.setNotifyStatus(Order.ORDER_NOTIFY_STATUS_RESEND);
        order.setUpdateTime(new Date());
        order.setErrorMsg(errorMsg);

        if (basicRepository.updateStatusNotify(order) < 1)
            return Order.FAIL;
        return Order.SUCCESS;
    }

    @Override
    public int notifyFail(String orderId, String errorMsg) {
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null)
            return Order.INVALID;
        if (Order.ORDER_STATUS_PAYMENT_SUCCESS != order.getStatus())
            return Order.INVALID;

        if (Order.ORDER_NOTIFY_STATUS_SUCCESS == order.getNotifyStatus()
                || Order.ORDER_NOTIFY_STATUS_FAIL == order.getNotifyStatus())
            return Order.REPEAT;

        order.setNotifyStatus(Order.ORDER_NOTIFY_STATUS_FAIL);
        order.setUpdateTime(new Date());
        order.setErrorMsg(errorMsg);

        if (basicRepository.updateStatusNotify(order) < 1)
            return Order.FAIL;
        return Order.SUCCESS;
    }

    @Override
    public int notifySuccess(String orderId) {
        Order order = basicRepository.getOrderByOrderId(orderId);
        if (order == null)
            return Order.INVALID;
        if (Order.ORDER_STATUS_PAYMENT_SUCCESS != order.getStatus())
            return Order.INVALID;
        if (Order.ORDER_NOTIFY_STATUS_SUCCESS == order.getNotifyStatus()
                || Order.ORDER_NOTIFY_STATUS_FAIL == order.getNotifyStatus())
            return Order.REPEAT;

        order.setNotifyStatus(Order.ORDER_NOTIFY_STATUS_SUCCESS);
        order.setUpdateTime(new Date());
        order.setErrorMsg(null);

        if (basicRepository.updateStatusNotify(order) < 1)
            return Order.FAIL;
        return Order.SUCCESS;
    }

}
