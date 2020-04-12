package com.pcr.common.nio.core;

public interface PacketProcesser {
    public void process(ChannelContext ctx, Object packet);
}
