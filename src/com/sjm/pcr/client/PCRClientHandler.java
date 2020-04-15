package com.sjm.pcr.client;

import java.io.IOException;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.PostConstruct;
import com.sjm.core.nio.core.ByteBufferPool;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.core.EventHandler;
import com.sjm.core.nio.core.NIOBase;
import com.sjm.core.nio.core.NIOClient;
import com.sjm.core.nio.impl.ByteArrayWithFiles;
import com.sjm.core.nio.impl.ByteArrayWithFilesDecoder;
import com.sjm.core.nio.impl.ByteArrayWithFilesEncoder;

@Component
public class PCRClientHandler extends EventHandler {
    static final Logger logger = LoggerFactory.getLogger(PCRClientHandler.class);

    @PostConstruct
    private void init() {
        NIOClient client = new NIOClient("127.0.0.1", 9009, new ByteBufferPool(4096),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(), this);
        client.start();
    }

    @Override
    public void onStartup(NIOBase obj) {
        logger.info(obj.getClass().getSimpleName() + " startup OK");
    }

    @Override
    public void onRead(ChannelContext ctx, Object packet) throws IOException {
        ByteArrayWithFiles data = (ByteArrayWithFiles) packet;
        logger.info("data.files={}", data.files);
        logger.info("data.data.length={}", data.data.length);
    }

    @Override
    public void onConnect(ChannelContext ctx) throws IOException {
        logger.info("channel {} has been connected", ctx);
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        logger.info("channel {} has been closed", ctx);
    }
}
