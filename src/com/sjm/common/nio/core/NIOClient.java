package com.sjm.common.nio.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class NIOClient extends NIOBase {
    // static final Logger logger = LoggerFactory.getLogger(NIOClient.class);
    //
    // public static void main(String[] args) throws Exception {
    // ByteBufferPool bufferPool = new ByteBufferPool(4096);
    // ExecutorService executor = Executors.newFixedThreadPool(10);
    // ChannelHandler encoder = new ByteArrayEncoder();
    // ChannelHandler decoder = new ByteArrayDecoder();
    // EventHandler handler = new EventHandler() {
    // @Override
    // public void onStartup(NIOBase obj) throws IOException {
    // logger.info("client startup OK");
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
    //
    // new Thread() {
    // public void run() {
    // try {
    // Misc.sleep(1000);
    // ctx.write(ByteData.valueOf(new File("/root/a.txt")).toByteArray());
    // Misc.sleep(5000);
    // ctx.write(ByteData.valueOf(new File("/root/a.txt")).toByteArray());
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // };
    // }.start();
    // }
    //
    // @Override
    // public void onClose(ChannelContext ctx) throws IOException {
    // logger.info("channel {} has been closed", ctx);
    // }
    // };
    // NIOClient client =
    // new NIOClient("127.0.0.1", 9009, bufferPool, executor, encoder, decoder, handler);
    // client.start();
    // // client.shutdown();
    // }

    private String host;
    private int port;

    public NIOClient(String host, int port, ByteBufferPool bufferPool, ChannelHandler encoder,
            ChannelHandler decoder, EventHandler handler) {
        super(bufferPool, encoder, decoder, handler);
        this.host = host;
        this.port = port;
    }

    @Override
    protected Closeable openChannel() throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(host, port));
        sc.register(selector, SelectionKey.OP_CONNECT);
        return sc;
    }

    @Override
    protected SocketChannel processConnect(SelectionKey key) throws IOException {
        if (key.isConnectable()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
            SocketChannel sc = (SocketChannel) key.channel();
            if (sc.isConnectionPending()) {
                sc.finishConnect();
            }
            sc.configureBlocking(false);
            return sc;
        }
        return null;
    }

    @Override
    protected boolean shutdownOnClose() {
        return true;
    }
}
