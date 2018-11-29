package com.qinglan.sdk.server.data.infrastructure.commmand.impl;

import com.qinglan.sdk.server.data.infrastructure.commmand.ICommandHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CommandContext {
    @Resource
    private HandlersProvider handlersProfiver;

    public CommandContext() {
    }

    public Object run(Object command) {
        ICommandHandler<Object, Object> handler = this.handlersProfiver.getHandler(command);
        return handler.handle(command);
    }

    public interface HandlersProvider {
        ICommandHandler<Object, Object> getHandler(Object var1);
    }
}
