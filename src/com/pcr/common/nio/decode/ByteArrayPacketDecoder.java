package com.pcr.common.nio.decode;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.pcr.common.nio.core.ChannelContext;
import com.pcr.common.nio.core.PacketDecoder;
import com.pcr.common.nio.core.PacketProcesser;

public class ByteArrayPacketDecoder<C extends ChannelContext> implements PacketDecoder<C, byte[]> {

    public static final int STATE_READ_HEADER = 0;
    public static final int STATE_READ_BODY = 1;
    public static final int STATE_CLOSED = 2;

    @Override
    public boolean handleRead(C ctx, PacketProcesser<C, byte[]> processer) throws IOException {
        ctx.continueRead = true;
        while (ctx.continueRead) {
            ctx.continueRead = true;
            switch (ctx.state) {
                case STATE_READ_HEADER:
                    handleReadHeader(ctx);
                    break;
                case STATE_READ_BODY:
                    handleReadBody(ctx, processer);
                    break;
                case STATE_CLOSED:
                    return false;
            }
        }
        return true;
    }

    private void handleReadHeader(C ctx) throws IOException {
        ByteBuffer buffer = ctx.buffers.get(0);
        if (ctx.headerIndex + 4 < buffer.position()) {
            ctx.currentPacketSize = buffer.getInt(ctx.headerIndex);
            ctx.state = STATE_READ_BODY;
        } else {
            if (buffer.hasRemaining()) {
                int n = ctx.channel.read(buffer);
                if (n == 0) {
                    ctx.continueRead = false;
                } else if (n == -1) {
                    ctx.state = STATE_CLOSED;
                } else {
                    if (ctx.headerIndex + 4 < buffer.position()) {
                        ctx.currentPacketSize = buffer.getInt(ctx.headerIndex);
                        ctx.state = STATE_READ_BODY;
                    } else {
                        if (buffer.hasRemaining()) {
                            ctx.continueRead = false;
                        }
                    }
                }
            }
            if (ctx.continueRead && ctx.state == STATE_READ_HEADER) {
                ByteBuffer nextBuffer;
                if (ctx.buffers.size() == 1) {
                    nextBuffer = ctx.bufferPool.apply();
                    ctx.buffers.add(nextBuffer);
                } else if (ctx.buffers.size() == 2) {
                    nextBuffer = ctx.buffers.get(1);
                } else
                    throw new IOException();
                int n = ctx.channel.read(nextBuffer);
                if (n == 0) {
                    ctx.continueRead = false;
                } else if (n == -1) {
                    ctx.state = STATE_CLOSED;
                } else {
                    if (ctx.headerIndex + 4 - buffer.position() < nextBuffer.position()) {
                        int currentPacketSize = 0;
                        for (int i = 0; i < 4; i++) {
                            int index = ctx.headerIndex + i;
                            byte b;
                            if (index < buffer.position())
                                b = buffer.get(index);
                            else
                                b = nextBuffer.get(index - buffer.position());
                            currentPacketSize += b << ((3 - i) * 8);
                        }
                        ctx.currentPacketSize = currentPacketSize;
                        ctx.state = STATE_READ_BODY;
                    } else {
                        if (!buffer.hasRemaining()) {
                            throw new IOException();
                        }
                    }
                }
            }
        }
    }

    private void handleReadBody(C ctx, PacketProcesser<C, byte[]> processer) throws IOException {
        int totalSize = 0;
        for (ByteBuffer buffer : ctx.buffers)
            totalSize += buffer.position();
        int endIndex = ctx.headerIndex + 4 + ctx.currentPacketSize;
        if (endIndex <= totalSize) {
            byte[] packet = new byte[ctx.currentPacketSize];
            int remainSize = packet.length;
            int fromIndex = ctx.headerIndex + 4;
            int from = 0;
            int lastEndIndex = 0;
            while (remainSize > 0) {
                ByteBuffer buffer = ctx.buffers.get(from++);
                if (fromIndex < buffer.position()) {
                    int limit = Math.min(buffer.position() - fromIndex, remainSize);
                    System.arraycopy(buffer.array(), fromIndex, packet, packet.length - remainSize,
                            limit);
                    lastEndIndex = fromIndex + limit;
                    remainSize -= limit;
                    fromIndex = 0;
                } else {
                    fromIndex = fromIndex - buffer.position();
                }
            }
            processer.process(ctx, packet);
            for (int i = 0, len = ctx.buffers.size() - 1; i < len; i++) {
                ctx.bufferPool.recycle(ctx.buffers.get(i));
            }
            ByteBuffer lastBuffer = ctx.buffers.get(ctx.buffers.size() - 1);
            ctx.buffers.clear();
            ctx.buffers.add(lastBuffer);
            ctx.headerIndex = lastEndIndex;
            ctx.state = STATE_READ_HEADER;
        } else {
            while (ctx.continueRead && ctx.state != STATE_CLOSED) {
                ByteBuffer lastBuffer = ctx.buffers.get(ctx.buffers.size() - 1);
                if (!lastBuffer.hasRemaining()) {
                    lastBuffer = ctx.bufferPool.apply();
                    ctx.buffers.add(lastBuffer);
                }
                int n = ctx.channel.read(lastBuffer);
                if (n == 0) {
                    ctx.continueRead = false;
                } else if (n == -1) {
                    ctx.state = STATE_CLOSED;
                } else {
                    totalSize += n;
                    if (endIndex <= totalSize) {
                        break;
                    }
                }
            }
        }
    }
}
