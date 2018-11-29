package com.qinglan.sdk.server.data.infrastructure.commmand.impl;

import com.qinglan.sdk.server.data.infrastructure.commmand.CommandBus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class StandardCommandBus implements CommandBus {
    @Resource
    private CommandContext commandContext;

    public StandardCommandBus() {
    }

    public Object dispatch(Object command) {
        return this.commandContext.run(command);
    }
}
