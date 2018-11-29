package com.qinglan.sdk.server.data.infrastructure.event.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DisruptorManager implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(DisruptorManager.class);
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private Disruptor<DisruptorEvent> disruptor;

    public DisruptorManager() {
    }

    public Disruptor<DisruptorEvent> getDisruptor() {
        return this.disruptor;
    }

    public void afterPropertiesSet() throws Exception {
        DisruptorEventFactory factory = new DisruptorEventFactory();
        int bufferSize = 2048;
        this.disruptor = new Disruptor(factory, bufferSize, this.executor, ProducerType.MULTI, new BlockingWaitStrategy());
        this.disruptor.handleEventsWith(new EventHandler[]{new DisruptorEventHandler()});
        this.disruptor.start();
        logger.debug("{} initial success", this.getClass().getSimpleName());
    }

    public void destroy() throws Exception {
        if (this.disruptor != null) {
            this.disruptor.shutdown();
        }

        if (this.executor != null) {
            this.executor.shutdown();
        }

        logger.debug("{} shutdown success", this.getClass().getSimpleName());
    }
}

