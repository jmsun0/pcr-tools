package com.sjm.common.nio.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOServer extends NIOBase {
    // static final Logger logger = LoggerFactory.getLogger(NIOServer.class);
    //
    // public static void main(String[] args) throws Exception {
    // ByteBufferPool bufferPool = new ByteBufferPool(4096);
    // ExecutorService executor = Executors.newFixedThreadPool(10);
    // ChannelHandler encoder = new ByteArrayEncoder();
    // ChannelHandler decoder = new ByteArrayDecoder();
    // EventHandler handler = new EventHandler() {
    // @Override
    // public void onStartup(NIOBase obj) throws IOException {
    // logger.info("server startup OK");
    // }
    //
    // @Override
    // public void onRead(ChannelContext ctx, Object packet) throws IOException {
    // byte[] data = (byte[]) packet;
    // logger.info("channel {} read {} bytes", ctx, data.length);
    // Strings.print(data);
    // }
    //
    // @Override
    // public void onConnect(ChannelContext ctx) throws IOException {
    // logger.info("channel {} has been connected", ctx);
    // }
    //
    // @Override
    // public void onClose(ChannelContext ctx) throws IOException {
    // logger.info("channel {} has been closed", ctx);
    // }
    // };
    // NIOServer server = new NIOServer(9009, bufferPool, executor, encoder, decoder, handler);
    // server.start();
    // // server.shutdown();
    // }

    protected int port;

    public NIOServer(int port, ByteBufferPool bufferPool, ChannelHandler encoder,
            ChannelHandler decoder, EventHandler handler) {
        super(bufferPool, encoder, decoder, handler);
        this.port = port;
    }

    @Override
    protected Closeable openChannel() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        return ssc;
    }

    @Override
    protected SocketChannel processConnect(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel sc = server.accept();
            sc.configureBlocking(false);
            return sc;
        }
        return null;
    }

    @Override
    protected boolean shutdownOnClose() {
        return false;
    }
}
