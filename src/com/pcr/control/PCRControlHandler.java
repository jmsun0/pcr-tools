package com.pcr.control;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcr.common.nio.core.ChannelHandler;
import com.pcr.common.nio.core.Shutdownable;
import com.pcr.common.pcr.PCRClient;
import com.pcr.common.pcr.PCRContext;
import com.pcr.util.mine.ByteData;
import com.pcr.util.mine.Strings;

public class PCRControlHandler implements ChannelHandler<PCRContext, byte[]> {
    public static void main(String[] args) {
        PCRClient client = new PCRClient("127.0.0.1", 9009, new PCRControlHandler());
        client.start();
    }

    static final Logger logger = LoggerFactory.getLogger(PCRControlHandler.class);

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
        ctx.write(ByteData.valueOf(new File("/root/tmp.txt")).toByteArray());
        // ctx.close();
    }

    @Override
    public void onClose(PCRContext ctx) throws IOException {
        logger.info("channel {} has been closed", ctx);
    }
}
