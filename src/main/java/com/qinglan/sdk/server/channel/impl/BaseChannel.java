package com.qinglan.sdk.server.channel.impl;

import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.channel.IChannel;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.basic.ChannelEntity;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_CODE;
import static com.qinglan.sdk.server.Constants.RESPONSE_KEY_MESSAGE;

public abstract class BaseChannel implements IChannel {
    protected static final String CONNECTOR = "&";

    protected BasicRepository basicRepository;
    protected ChannelEntity channel;
    protected ChannelGameEntity channelGame;
    protected boolean isInit = false;

    @Override
    public void init(BasicRepository basicRepository) {
        this.basicRepository = basicRepository;
        isInit = true;
    }

    @Override
    public void init(BasicRepository basicRepository, long gameId, int channelId) {
        this.basicRepository = basicRepository;
        init(basicRepository.getChannel(channelId), basicRepository.getByChannelAndGameId(channelId, gameId));
    }

    @Override
    public void init(ChannelEntity channel, ChannelGameEntity channelGame) {
        this.channel = channel;
        this.channelGame = channelGame;
        isInit = true;
    }

    protected Map<String, Object> getResult(int code, String msg) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(RESPONSE_KEY_CODE, code);
        resp.put(RESPONSE_KEY_MESSAGE, msg);
        return resp;
    }

    protected void checkInit() {
        if (!isInit)
            throw new RuntimeException("Please must be init before using");
    }

    protected Order getOrder(OrderService service, String orderId, String channelOrderId) {
        Order order = service.getOrderByOrderId(orderId);
        if (order != null)
            order.setChannelOrderId(channelOrderId);
        return order;
    }

    protected void updateOrder(Order order, Number amount, OrderService service) {
        if (amount.doubleValue() >= order.getAmount()) {
            service.paySuccess(order.getOrderId());
        } else {
            service.payFail(order.getOrderId(), "order amount error");
        }
    }

    protected String getRequestString(HttpServletRequest request) {
        String line = null;
        StringBuffer sb = new StringBuffer();
        try {
            request.setCharacterEncoding("UTF-8");
            InputStream stream = request.getInputStream();
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            System.out.println("The original data is : " + sb.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    protected Map<String, Object> getResponseParams(String result) throws IOException {
        Map<String, Object> resp = JsonMapper.getMapper().readValue(result, new TypeReference<Map<String, Object>>() {
        });
        return resp;
    }

    protected void addData(Map<String, Object> data, String key, Object value) {
        if (value != null) {
            data.put(key, value);
        }
    }
}
