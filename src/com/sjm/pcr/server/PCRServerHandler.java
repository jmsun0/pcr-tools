package com.sjm.pcr.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.PostConstruct;
import com.sjm.core.nio.core.ByteBufferPool;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.core.EventHandler;
import com.sjm.core.nio.core.NIOBase;
import com.sjm.core.nio.core.NIOServer;
import com.sjm.core.nio.impl.ByteArrayWithFiles;
import com.sjm.core.nio.impl.ByteArrayWithFilesDecoder;
import com.sjm.core.nio.impl.ByteArrayWithFilesEncoder;

@Component
public class PCRServerHandler extends EventHandler {
    static final Logger logger = LoggerFactory.getLogger(PCRServerHandler.class);

    protected Map<String, ChannelContext> channelContextMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        NIOServer server = new NIOServer(9009, new ByteBufferPool(4096),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(), this);
        server.start();
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
