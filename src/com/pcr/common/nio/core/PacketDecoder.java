package com.pcr.common.nio.core;

import java.io.IOException;

public interface PacketDecoder<C extends ChannelContext, P> {
    public boolean handleRead(C ctx, PacketProcesser<C, P> processer) throws IOException;
}
