package com.pcr.common.nio.core;

import java.io.IOException;

public interface ChannelHandler {
    public int handle(ChannelContext ctx) throws IOException;
}
