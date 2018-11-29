package com.qinglan.sdk.server.data.infrastructure.event.disruptor;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisruptorEventHandler implements EventHandler<DisruptorEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DisruptorEventHandler.class);

    public DisruptorEventHandler() {
    }

    public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) {
        Object bean = event.getBeanFactory().getBean(event.getBeanName());

        try {
            event.getMethod().invoke(bean, event.getRealEvent());
        } catch (Exception var7) {
            logger.error("sequence: {}, beanName: {}, method: {}, realEvent: {}", new Object[]{sequence, event.getBeanName(), event.getMethod(), event.getRealEvent()});
            logger.error("exception: {}", var7);
        }

    }
}
