package com.pcr.common.pcr;

import java.util.concurrent.Executors;

import com.pcr.common.nio.core.ChannelHandler;
import com.pcr.common.nio.core.NIOServer;
import com.pcr.common.nio.decode.ByteArrayPacketDecoder;

public class PCRServer extends NIOServer<PCRContext, byte[]> {

    public PCRServer(int port, ChannelHandler<PCRContext, byte[]> handler) {
        super(port, 4096, Executors.newFixedThreadPool(10), PCRContext::new,
                new ByteArrayPacketDecoder<>(), handler);
    }

}
