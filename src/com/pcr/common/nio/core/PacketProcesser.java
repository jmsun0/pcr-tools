package com.pcr.common.nio.core;

public interface PacketProcesser<C extends ChannelContext, P> {
    public void process(C ctx, P packet);
}
