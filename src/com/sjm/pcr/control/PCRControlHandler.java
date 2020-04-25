package com.sjm.pcr.control;

import java.io.IOException;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.nio.core.ByteBufferPool;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.core.NIOClient;
import com.sjm.core.nio.ext.ByteArrayWithFilesDecoder;
import com.sjm.core.nio.ext.ByteArrayWithFilesEncoder;
import com.sjm.pcr.client_control.BaseClientHandler;

@Component
public class PCRControlHandler extends BaseClientHandler implements CommandLineRunner {
    static final Logger logger = LoggerFactory.getLogger(PCRControlHandler.class);

    @Override
    public void run(String... args) throws Exception {
        client = new NIOClient(host, port, new ByteBufferPool(bufferSize),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(), this);
        client.start();
    }

    @Override
    public void onConnect(ChannelContext ctx) throws IOException {
        super.onConnect(ctx);
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        super.onClose(ctx);
        // System.exit(1);
    }
}
