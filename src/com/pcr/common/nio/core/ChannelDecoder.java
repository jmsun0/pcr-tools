package com.pcr.common.nio.core;

import java.io.IOException;

public abstract class ChannelDecoder implements ChannelHandler {

    public abstract void decode(ChannelContext ctx) throws IOException;

    @Override
    public int handle(ChannelContext ctx) throws IOException {
        while (true) {
            int n = ctx.channel.read(ctx.readBuffer);
            if (n == 0 || n == -1) {
                return n;
            }
            ctx.readBuffer.flip();
            while (true) {
                decode(ctx);
                if (!ctx.readBuffer.hasRemaining()) {
                    break;
                }
            }
            ctx.readBuffer.clear();
        }
    }
}
