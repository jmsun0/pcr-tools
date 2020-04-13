package com.sjm.pcr.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sjm.common.logger.Logger;
import com.sjm.common.logger.LoggerFactory;
import com.sjm.common.nio.core.ByteBufferPool;
import com.sjm.common.nio.core.ChannelContext;
import com.sjm.common.nio.core.EventHandler;
import com.sjm.common.nio.core.NIOBase;
import com.sjm.common.nio.core.NIOServer;
import com.sjm.common.nio.decode.ByteArrayWithFiles;
import com.sjm.common.nio.decode.ByteArrayWithFilesDecoder;
import com.sjm.common.nio.decode.ByteArrayWithFilesEncoder;

public class PCRServerHandler extends EventHandler {
    public static void main(String[] args) {
        NIOServer server =
                new NIOServer(9009, new ByteBufferPool(4096), new ByteArrayWithFilesEncoder(),
                        new ByteArrayWithFilesDecoder(), new PCRServerHandler());
        server.start();
    }

    static final Logger logger = LoggerFactory.getLogger(PCRServerHandler.class);

    protected Map<String, ChannelContext> channelContextMap = new ConcurrentHashMap<>();

    @Override
    public void onStartup(NIOBase obj) {
        logger.info(obj.getClass().getSimpleName() + " startup OK");
    }

    @Override
    public void onRead(ChannelContext ctx, Object packet) throws IOException {
        ByteArrayWithFiles data = (ByteArrayWithFiles) packet;
        // byte[] bytes = Files.readAllBytes(data.toPath());
        logger.info("data={}", data);
        logger.info("data.data={}", data.data);
        logger.info("data.files={}", data.files);
        logger.info("data.data.length={}", data.data.length);
        logger.info("data.files.size()={}", data.files.size());
        // logger.info("channel {} read {} bytes", ctx, bytes.length);
        // Strings.print(bytes);
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
