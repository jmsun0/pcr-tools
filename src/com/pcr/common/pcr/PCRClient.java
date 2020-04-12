package com.pcr.common.pcr;

import java.util.concurrent.Executors;

import com.pcr.common.nio.core.ByteBufferPool;
import com.pcr.common.nio.core.EventHandler;
import com.pcr.common.nio.core.NIOClient;
import com.pcr.common.nio.decode.ByteArrayDecoder;
import com.pcr.common.nio.decode.ByteArrayEncoder;

public class PCRClient extends NIOClient {

    public PCRClient(String host, int port, EventHandler handler) {
        super(host, port, new ByteBufferPool(4096), Executors.newFixedThreadPool(10),
                new ByteArrayEncoder(), new ByteArrayDecoder(), handler);
    }

}
