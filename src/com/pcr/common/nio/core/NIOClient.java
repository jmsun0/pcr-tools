package com.pcr.common.nio.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcr.common.nio.decode.ByteArrayPacketDecoder;
import com.pcr.util.mine.Misc;
import com.pcr.util.mine.Strings;


public class NIOClient<C extends ChannelContext, P> extends Thread implements Shutdownable {
    static final Logger logger = LoggerFactory.getLogger(NIOClient.class);

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ChannelContextAllocator<ChannelContext> allocator = ChannelContext::new;
        PacketDecoder<ChannelContext, byte[]> decoder = new ByteArrayPacketDecoder<>();
        ChannelHandler<ChannelContext, byte[]> handler =
                new ChannelHandler<ChannelContext, byte[]>() {
                    @Override
                    public void onStartup(Shutdownable sd) {
                        logger.info("client startup OK");
                    }

                    @Override
                    public void onRead(ChannelContext ctx, byte[] packet) throws IOException {
                        logger.info("channel {} read {} bytes", ctx, packet.length);
                        Strings.print(packet);
                    }

                    @Override
                    public void onConnect(ChannelContext ctx) throws IOException {
                        logger.info("channel {} has been connected", ctx);
                    }

                    @Override
                    public void onClose(ChannelContext ctx) throws IOException {
                        logger.info("channel {} has been closed", ctx);
                    }
                };
        NIOClient<ChannelContext, byte[]> client =
                new NIOClient<>("127.0.0.1", 9009, 4096, executor, allocator, decoder, handler);
        client.start();
        // client.shutdown();
    }

    private String host;
    private int port;
    private int bufferSize;
    private ExecutorService executor;
    private ChannelContextAllocator<C> allocator;
    private PacketDecoder<C, P> decoder;
    private ChannelHandler<C, P> handler;

    private boolean isOpen = true;
    private SocketChannel sc;
    private Selector selector;
    private ByteBufferPool bufferPool;
    private PacketProcesser<C, P> processer = this::processPacket;

    public NIOClient(String host, int port, int bufferSize, ExecutorService executor,
            ChannelContextAllocator<C> allocator, PacketDecoder<C, P> decoder,
            ChannelHandler<C, P> handler) {
        this.host = host;
        this.port = port;
        this.bufferSize = bufferSize;
        this.executor = executor;
        this.allocator = allocator;
        this.decoder = decoder;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            bufferPool = new ByteBufferPool(bufferSize);
            selector = Selector.open();
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress(host, port));
            sc.register(selector, SelectionKey.OP_CONNECT);
            handler.onStartup(this);
            while (isOpen) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isConnectable()) {
                        if (sc != key.channel())
                            throw new IOException();
                        if (sc.isConnectionPending()) {
                            sc.finishConnect();
                        }
                        sc.configureBlocking(false);
                        C ctx = allocator.allocate();
                        ctx.channel = sc;
                        ctx.bufferPool = bufferPool;
                        ctx.buffers.add(bufferPool.apply());
                        handler.onConnect(ctx);
                        if (sc.isOpen())
                            sc.register(selector, SelectionKey.OP_READ, ctx);
                        else
                            handler.onClose(ctx);
                    } else if (key.isReadable()) {
                        @SuppressWarnings("unchecked")
                        C ctx = (C) key.attachment();
                        if (ctx.channel != key.channel())
                            throw new IOException();
                        boolean result;
                        try {
                            result = decoder.handleRead(ctx, processer);
                        } catch (Exception e) {
                            result = false;
                            logger.error(e.getMessage(), e);
                        }
                        if (!result) {
                            handler.onClose(ctx);
                            Misc.close(ctx);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        shutdown();
    }

    private void processPacket(C ctx, P packet) {
        executor.execute(() -> {
            try {
                handler.onRead(ctx, packet);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void shutdown() {
        isOpen = false;
        Misc.close(selector);
        Misc.close(sc);
    }
}
