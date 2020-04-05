package com.pcr.common.pcr;

import java.util.concurrent.Executors;

import com.pcr.common.nio.core.ChannelHandler;
import com.pcr.common.nio.core.NIOClient;
import com.pcr.common.nio.decode.ByteArrayPacketDecoder;

public class PCRClient extends NIOClient<PCRContext, byte[]> {

    public PCRClient(String host, int port, ChannelHandler<PCRContext, byte[]> handler) {
        super(host, port, 4096, Executors.newFixedThreadPool(10), PCRContext::new,
                new ByteArrayPacketDecoder<>(), handler);
    }

}
