package com.pcr.common.nio.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.pcr.util.mine.Misc;

public class ChannelContext implements Closeable {
    public SocketChannel channel;
    public List<ByteBuffer> buffers = new ArrayList<>();
    public ByteBufferPool bufferPool;
    public int state = 0;
    public int currentPacketSize = -1;
    public int headerIndex = 0;
    public boolean continueRead;

    @Override
    public void close() throws IOException {
        for (ByteBuffer buffer : buffers) {
            bufferPool.recycle(buffer);
        }
        buffers.clear();
        Misc.close(channel);
    }

    @Override
    public String toString() {
        try {
            return getClass().getSimpleName() + "["
                    + (channel.isOpen()
                            ? ("local=" + channel.getLocalAddress() + " remote="
                                    + channel.getRemoteAddress())
                            : "closed")
                    + "]";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
