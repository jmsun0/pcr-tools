package com.pcr.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcr.common.nio.core.ChannelHandler;
import com.pcr.common.nio.core.Shutdownable;
import com.pcr.common.pcr.PCRContext;
import com.pcr.common.pcr.PCRServer;
import com.pcr.util.mine.Strings;

public class PCRServerHandler implements ChannelHandler<PCRContext, byte[]> {
    public static void main(String[] args) {
        PCRServer server = new PCRServer(9009, new PCRServerHandler());
        server.start();
    }

    static final Logger logger = LoggerFactory.getLogger(PCRServerHandler.class);

    protected Map<String, PCRContext> channelContextMap = new ConcurrentHashMap<>();

    @Override
    public void onStartup(Shutdownable sd) {
        logger.info(sd.getClass().getSimpleName() + " startup OK");
    }

    @Override
    public void onRead(PCRContext ctx, byte[] packet) throws IOException {
        logger.info("channel {} read {} bytes", ctx, packet.length);
        Strings.print(packet);
    }

    @Override
    public void onConnect(PCRContext ctx) throws IOException {
        logger.info("channel {} has been connected", ctx);
    }

    @Override
    public void onClose(PCRContext ctx) throws IOException {
        logger.info("channel {} has been closed", ctx);
    }
}
