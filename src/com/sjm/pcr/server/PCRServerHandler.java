package com.sjm.pcr.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Value;
import com.sjm.core.nio.core.ByteBufferPool;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.core.EventHandler;
import com.sjm.core.nio.core.NIOBase;
import com.sjm.core.nio.core.NIOServer;
import com.sjm.core.nio.ext.ByteArrayWithFiles;
import com.sjm.core.nio.ext.ByteArrayWithFilesDecoder;
import com.sjm.core.nio.ext.ByteArrayWithFilesEncoder;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.exception.ServiceException;
import com.sjm.pcr.common.misc.ChannelContextHolder;
import com.sjm.pcr.common_component.rpc.RemoteCallRequest;
import com.sjm.pcr.common_component.rpc.RemoteCallResponse;
import com.sjm.pcr.common_component.rpc.RemoteCallSocketProcessor;
import com.sjm.pcr.common_component.rpc.SerializableRemoteCallResponse;
import com.sjm.pcr.common_component.service.ClientService;

@Component
public class PCRServerHandler extends EventHandler implements CommandLineRunner, ClientService {
    static final Logger logger = LoggerFactory.getLogger(PCRServerHandler.class);

    @Value("${pcr.server.port:9009}")
    private int port;

    @Value("${pcr.server.buffer-size:4096}")
    private int bufferSize;

    @Autowired
    private RemoteCallSocketProcessor remoteCallSocketProcessor;

    @Autowired
    private ExecutorService executor;

    private Map<ChannelContext, String> ctxNameMap = new ConcurrentHashMap<>();
    private Map<String, ChannelContext> nameCtxMap = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        NIOServer server = new NIOServer(port, new ByteBufferPool(bufferSize),
                new ByteArrayWithFilesEncoder(), new ByteArrayWithFilesDecoder(), this);
        // server.start();
        server.run();
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
        logger.debug("channel {} has been connected", ctx);
        ctxNameMap.put(ctx, "");
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        logger.debug("channel {} has been closed", ctx);
        String name = ctxNameMap.remove(ctx);
        nameCtxMap.remove(name);
        if (Misc.isNotEmpty(name)) {
            logger.info("client [{}] has logout", name);
        }
    }

    @Override
    public List<String> listClient() {
        return new ArrayList<>(nameCtxMap.keySet());
    }

    @Override
    public void register(String name) {
        if (nameCtxMap.containsKey(name)) {
            throw new ServiceException("'" + name + "' is already exists");
        }
        ChannelContext ctx = nameCtxMap.get(name);
        if (ctx != null) {
            if (ctx == ChannelContextHolder.get())
                return;
            else
                throw new ServiceException("'" + name + "' is already exists");
        }
        ctx = ChannelContextHolder.get();
        ctxNameMap.put(ctx, name);
        nameCtxMap.put(name, ctx);
        logger.info("client [{}] has login", name);
    }

    @Override
    public SerializableRemoteCallResponse remoteCall(String name, RemoteCallRequest request,
            File[] files, long timeout) throws Exception {
        ChannelContext ctx = nameCtxMap.get(name);
        if (ctx == null)
            throw new ServiceException("client '" + name + "' not found");
        RemoteCallResponse res = remoteCallSocketProcessor.remoteCallSync(ctx, request,
                Arrays.asList(files), timeout);
        return remoteCallSocketProcessor.serializeResponse(res);
    }
}
