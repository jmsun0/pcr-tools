package com.sjm.pcr.client;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.sjm.common.core.Strings;
import com.sjm.common.logger.Logger;
import com.sjm.common.logger.LoggerFactory;
import com.sjm.common.nio.core.ByteBufferPool;
import com.sjm.common.nio.core.ChannelContext;
import com.sjm.common.nio.core.EventHandler;
import com.sjm.common.nio.core.NIOBase;
import com.sjm.common.nio.core.NIOClient;
import com.sjm.common.nio.decode.ByteArrayWithFiles;
import com.sjm.common.nio.decode.ByteArrayWithFilesDecoder;
import com.sjm.common.nio.decode.ByteArrayWithFilesEncoder;

public class PCRClientHandler extends EventHandler {
    public static void main(String[] args) {
        NIOClient client = new NIOClient("127.0.0.1", 9009, new ByteBufferPool(4096),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(),
                new PCRClientHandler());
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
        ctx.write(new ByteArrayWithFiles("hello world".getBytes(), Arrays
                .asList(new File("/data/centos6/CentOS6.vhd-v1.tar.gz"), new File("/root/a.txt"))));
        // ctx.write(new File("/data/centos6/CentOS6.vhd"));
        // ctx.write(new File("/data/centos6/CentOS6.vhd-v1.tar.gz"));
        // ctx.write("hello".getBytes());
        // ctx.write("world".getBytes());
        // Misc.sleep(3000);
        // ctx.close();
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        logger.info("channel {} has been closed", ctx);
    }
}
