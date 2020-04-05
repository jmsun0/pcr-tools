package com.pcr.common.nio.decode;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.pcr.common.nio.core.ChannelContext;

public class ByteArrayPacketEncoder {
    public static void encode(ChannelContext ctx, byte[] packet) throws IOException {
        ByteBuffer buffer = ctx.bufferPool.apply();
        try {
            buffer.putInt(packet.length);
            buffer.flip();
            ByteBuffer[] buffers = new ByteBuffer[] {buffer, ByteBuffer.wrap(packet)};
            long writed = ctx.channel.write(buffers);
            if (writed != packet.length + 4)
                throw new IOException("writed " + writed + " bytes");
        } finally {
            ctx.bufferPool.recycle(buffer);
        }
    }
}
