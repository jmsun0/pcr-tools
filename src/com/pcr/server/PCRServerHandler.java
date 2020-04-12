package com.pcr.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pcr.common.core.Strings;
import com.pcr.common.logger.Logger;
import com.pcr.common.logger.LoggerFactory;
import com.pcr.common.nio.core.ChannelContext;
import com.pcr.common.nio.core.EventHandler;
import com.pcr.common.nio.core.NIOBase;
import com.pcr.common.pcr.PCRServer;

public class PCRServerHandler extends EventHandler {
    public static void main(String[] args) {
        PCRServer server = new PCRServer(9009, new PCRServerHandler());
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
        File data = (File) packet;
        byte[] bytes = Files.readAllBytes(data.toPath());
        logger.info("channel {} read {} bytes", ctx, bytes.length);
        Strings.print(bytes);
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
