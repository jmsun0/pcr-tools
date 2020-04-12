package com.pcr.common.pcr;

import java.util.concurrent.Executors;

import com.pcr.common.nio.core.ByteBufferPool;
import com.pcr.common.nio.core.EventHandler;
import com.pcr.common.nio.core.NIOServer;
import com.pcr.common.nio.decode.ByteArrayEncoder;
import com.pcr.common.nio.decode.FileDecoder;

public class PCRServer extends NIOServer {

    public PCRServer(int port, EventHandler handler) {
        super(port, new ByteBufferPool(4096), Executors.newFixedThreadPool(10),
                new ByteArrayEncoder(), new FileDecoder(), handler);
    }

}
