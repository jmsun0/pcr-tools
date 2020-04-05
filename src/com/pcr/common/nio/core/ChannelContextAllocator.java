package com.pcr.common.nio.core;

public interface ChannelContextAllocator<C extends ChannelContext> {
    public C allocate();
}
