package com.sjm.pcr.client;

import java.io.IOException;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Lazy;
import com.sjm.core.mini.springboot.api.Value;
import com.sjm.core.nio.core.ByteBufferPool;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.core.NIOClient;
import com.sjm.core.nio.ext.ByteArrayWithFilesDecoder;
import com.sjm.core.nio.ext.ByteArrayWithFilesEncoder;
import com.sjm.core.util.Misc;
import com.sjm.pcr.client_control.BaseClientHandler;
import com.sjm.pcr.common_component.service.ClientService;

@Component
public class PCRClientHandler extends BaseClientHandler implements CommandLineRunner {
    static final Logger logger = LoggerFactory.getLogger(PCRClientHandler.class);

    @Value("${pcr.client.name:test}")
    private String name;

    @Value("${pcr.client.reconnect.interval:2000}")
    private long interval;

    @Lazy
    @Autowired
    private ClientService clientManager;

    @Override
    public void run(String... args) throws Exception {
        client = new NIOClient(host, port, new ByteBufferPool(bufferSize),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(), this);
        // client.start();
        client.run();
    }

    @Override
    public void onConnect(ChannelContext ctx) throws IOException {
        super.onConnect(ctx);
        executor.submit(() -> {
            while (true) {
                if (clientManager != null) {
                    try {
                        clientManager.register(name);
                        logger.info("register client [{}] ok", name);
                        break;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                Misc.sleep(1000);
            }
        });
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        super.onClose(ctx);
        executor.submit(() -> {
            Misc.sleep(interval);
            client.start();
        });
    }
}
