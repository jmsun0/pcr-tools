package com.sjm.pcr.client_control;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.PostConstruct;
import com.sjm.core.mini.springboot.api.Value;
import com.sjm.core.nio.core.ByteBufferPool;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.core.EventHandler;
import com.sjm.core.nio.core.NIOBase;
import com.sjm.core.nio.core.NIOClient;
import com.sjm.core.nio.ext.ByteArrayWithFiles;
import com.sjm.core.nio.ext.ByteArrayWithFilesDecoder;
import com.sjm.core.nio.ext.ByteArrayWithFilesEncoder;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.rpc.RemoteCallSocketProcessor;

@Component
public class PCRClientHandler extends EventHandler {
    static final Logger logger = LoggerFactory.getLogger(PCRClientHandler.class);

    @Value("${pcr.server.host:127.0.0.1}")
    private String host;

    @Value("${pcr.server.port:9009}")
    private int port;

    @Value("${pcr.server.buffer-size:4096}")
    private int bufferSize;

    @Autowired
    private RemoteCallSocketProcessor remoteCallSocketProcessor;

    @Autowired
    private ExecutorService executor;

    private NIOClient client;
    private ChannelContext ctx;

    @PostConstruct
    private void init() {
        client = new NIOClient(host, port, new ByteBufferPool(bufferSize),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(), this);
        client.start();
    }

    @Override
    public void onStartup(NIOBase obj) {
        logger.info(obj.getClass().getSimpleName() + " startup OK");
    }

    @Override
    public void onRead(ChannelContext ctx, Object packet) throws IOException {
        executor.submit(() -> {
            try (ByteArrayWithFiles data = (ByteArrayWithFiles) packet;) {
                logger.debug("data.files={}", data.files);
                logger.debug("data.data.length={}", data.data.length);
                remoteCallSocketProcessor.processRead(ctx, data);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void onConnect(ChannelContext ctx) throws IOException {
        logger.debug("Channel {} has been connected", ctx);
        this.ctx = ctx;
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        logger.debug("Channel {} has been closed", ctx);
        this.ctx = null;
        executor.submit(() -> {
            Misc.sleep(3000);
            client.start();
        });
    }

    public ChannelContext getChannelContext() {
        while (ctx == null) {
            Misc.sleep(1000);
        }
        return ctx;
    }
}
