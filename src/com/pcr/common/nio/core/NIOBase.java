package com.pcr.common.nio.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.pcr.common.core.Misc;
import com.pcr.common.logger.Logger;
import com.pcr.common.logger.LoggerFactory;

public abstract class NIOBase extends Thread implements PacketProcesser {
    static final Logger logger = LoggerFactory.getLogger(NIOBase.class);

    protected Closeable channel;
    protected ByteBufferPool bufferPool;
    protected ExecutorService executor;
    protected ChannelHandler encoder, decoder;
    protected EventHandler handler;
    protected boolean isOpen = true;
    protected Selector selector;

    public NIOBase(ByteBufferPool bufferPool, ExecutorService executor, ChannelHandler encoder,
            ChannelHandler decoder, EventHandler handler) {
        this.bufferPool = bufferPool;
        this.executor = executor;
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
    }

    protected abstract Closeable openChannel() throws IOException;

    protected abstract SocketChannel processConnect(SelectionKey key) throws IOException;

    @Override
    public void run() {
        try {
            selector = Selector.open();
            channel = openChannel();
            handler.onStartup(this);
            while (isOpen) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    ChannelContext ctx = null;
                    int n = 0;
                    try {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        SocketChannel sc = processConnect(key);
                        if (sc != null) {
                            SelectionKey k = sc.register(selector, SelectionKey.OP_READ);
                            ctx = new ChannelContext(sc, selector, k, encoder, decoder, bufferPool,
                                    this);
                            k.attach(ctx);
                            handler.onConnect(ctx);
                        }
                        if (key.isReadable() || key.isWritable()) {
                            ctx = (ChannelContext) key.attachment();
                            if (key.isWritable())
                                n = ctx.encoder.handle(ctx);
                            if (key.isReadable())
                                n = ctx.decoder.handle(ctx);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (n == -1) {
                        handler.onClose(ctx);
                        Misc.close(ctx);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        shutdown();
    }

    @Override
    public void process(ChannelContext ctx, Object packet) {
        executor.execute(() -> {
            try {
                handler.onRead(ctx, packet);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    public void shutdown() {
        isOpen = false;
        Misc.close(selector);
        Misc.close(channel);
        executor.shutdown();
    }
}
