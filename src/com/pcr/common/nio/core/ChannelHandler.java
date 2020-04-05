package com.pcr.common.nio.core;

import java.io.IOException;

public interface ChannelHandler<C extends ChannelContext, P> {
    public void onStartup(Shutdownable sd);

    public void onConnect(C ctx) throws IOException;

    public void onRead(C ctx, P packet) throws IOException;

    public void onClose(C ctx) throws IOException;
}
