package com.pcr.client;

import java.io.File;
import java.io.IOException;

import com.pcr.common.core.ByteData;
import com.pcr.common.core.Strings;
import com.pcr.common.logger.Logger;
import com.pcr.common.logger.LoggerFactory;
import com.pcr.common.nio.core.ChannelContext;
import com.pcr.common.nio.core.EventHandler;
import com.pcr.common.nio.core.NIOBase;
import com.pcr.common.pcr.PCRClient;

public class PCRClientHandler extends EventHandler {
    public static void main(String[] args) {
        PCRClient client = new PCRClient("127.0.0.1", 9009, new PCRClientHandler());
        client.start();
    }

    static final Logger logger = LoggerFactory.getLogger(PCRClientHandler.class);

    @Override
    public void onStartup(NIOBase obj) {
        logger.info(obj.getClass().getSimpleName() + " startup OK");
    }

    @Override
    public void onRead(ChannelContext ctx, Object packet) throws IOException {
        byte[] data = (byte[]) packet;
        logger.info("channel {} read {} bytes", ctx, data.length);
        Strings.print(data);
    }

    @Override
    public void onConnect(ChannelContext ctx) throws IOException {
        logger.info("channel {} has been connected", ctx);
        ctx.write(ByteData.valueOf(new File("/root/tmp.txt")).toByteArray());
        ctx.write("hello".getBytes());
        // ctx.write("world".getBytes());

        // ctx.close();
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        logger.info("channel {} has been closed", ctx);
    }
}
