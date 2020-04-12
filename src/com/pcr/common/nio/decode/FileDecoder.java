package com.pcr.common.nio.decode;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.pcr.common.nio.core.ChannelContext;
import com.pcr.common.nio.core.ChannelHandler;
import com.pcr.common.nio.core.NIOTools;

/**
 * 报文类型： byte[]
 * 
 * 报文结构： [总长度，包括自己(固定4字节)] [body]
 * 
 * @author root
 *
 */
public class FileDecoder implements ChannelHandler {

    static final int STATE_HEADER = 0;
    static final int STATE_BODY = 1;

    static class DecodeContext {
        public int state;
        public byte[] header = new byte[4];
        public int headerIndex;
        public int total;
        public int position;
        public File file;
        public FileChannel fc;
    }

    @Override
    public int handle(ChannelContext ctx) throws IOException {
        DecodeContext dc = getDecodeContext(ctx);
        while (true) {
            if (dc.state == STATE_BODY) {
                while (true) {
                    int n = (int) dc.fc.transferFrom(ctx.channel, dc.position,
                            dc.total - dc.position);
                    if (n == 0 || n == -1) {
                        return n;
                    }
                    dc.position += n;
                    if (dc.position == dc.total) {
                        dc.fc.close();
                        ctx.processer.process(ctx, dc.file);
                        dc.headerIndex = 0;
                        dc.position = 0;
                        dc.fc = null;
                        dc.file = null;
                        dc.state = STATE_HEADER;
                        break;
                    }
                }
            }
            int n = ctx.channel.read(ctx.readBuffer);
            if (n == 0 || n == -1) {
                return n;
            }
            ctx.readBuffer.flip();
            while (true) {
                decode(ctx);
                if (!ctx.readBuffer.hasRemaining()) {
                    break;
                }
            }
            ctx.readBuffer.clear();
        }
    }

    public void decode(ChannelContext ctx) throws IOException {
        DecodeContext dc = getDecodeContext(ctx);
        switch (dc.state) {
            case STATE_HEADER: {
                int len = Math.min(ctx.readBuffer.remaining(), dc.header.length - dc.headerIndex);
                ctx.readBuffer.get(dc.header, dc.headerIndex, len);
                dc.headerIndex += len;
                if (dc.headerIndex == dc.header.length) {
                    dc.total = NIOTools.getInt(dc.header);
                    dc.file = Files.createTempFile(null, null).toFile();
                    dc.fc = FileChannel.open(dc.file.toPath(), StandardOpenOption.WRITE);
                    dc.state = STATE_BODY;
                }
                break;
            }
            case STATE_BODY: {
                int oldLimit = ctx.readBuffer.limit();
                int len = Math.min(ctx.readBuffer.remaining(), dc.total);
                ctx.readBuffer.limit(ctx.readBuffer.position() + len);
                int n = dc.fc.write(ctx.readBuffer);
                if (n != len)
                    throw new IOException();
                dc.position += len;
                ctx.readBuffer.limit(oldLimit);
                if (dc.position == dc.total) {
                    dc.fc.close();
                    ctx.processer.process(ctx, dc.file);
                    dc.headerIndex = 0;
                    dc.position = 0;
                    dc.fc = null;
                    dc.file = null;
                    dc.state = STATE_HEADER;
                }
                break;
            }
        }
    }

    private DecodeContext getDecodeContext(ChannelContext ctx) {
        DecodeContext dc = (DecodeContext) ctx.decodeCotext;
        if (dc == null)
            ctx.decodeCotext = dc = new DecodeContext();
        return dc;
    }
}
