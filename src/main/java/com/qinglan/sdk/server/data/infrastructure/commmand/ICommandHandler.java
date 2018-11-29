package com.qinglan.sdk.server.data.infrastructure.commmand;

public interface ICommandHandler<C, R> {
    R handle(C var1);
}
