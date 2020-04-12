package com.pcr.common.nio.decode;

import java.io.IOException;

import com.pcr.common.nio.core.ChannelContext;
import com.pcr.common.nio.core.ChannelEncoder;
import com.pcr.common.nio.core.NIOTools;

public class ByteArrayEncoder extends ChannelEncoder {

    static final int STATE_HEADER = 0;
    static final int STATE_BODY = 1;

    static class EncodeContext {
        public int state;
        public byte[] header = new byte[4];
        public int headerIndex;
        public byte[] data;
        public int dataIndex;
    }

    @Override
    public void encode(ChannelContext ctx) throws IOException {
        EncodeContext ec = (EncodeContext) ctx.encodeCotext;
        if (ec == null)
            ctx.encodeCotext = ec = new EncodeContext();
        switch (ec.state) {
            case STATE_HEADER: {
                if (ec.headerIndex == 0) {
                    ec.data = (byte[]) ctx.writeQueue.peek();
                    NIOTools.putInt(ec.header, ec.data.length);
                }
                int len = Math.min(ctx.writeBuffer.remaining(), ec.header.length - ec.headerIndex);
                ctx.writeBuffer.put(ec.header, ec.headerIndex, len);
                ec.headerIndex += len;
                if (ec.headerIndex == ec.header.length) {
                    ec.state = STATE_BODY;
                }
                break;
            }
            case STATE_BODY: {
                int len = Math.min(ctx.writeBuffer.remaining(), ec.data.length - ec.dataIndex);
                ctx.writeBuffer.put(ec.data, ec.dataIndex, len);
                ec.dataIndex += len;
                if (ec.dataIndex == ec.data.length) {
                    ctx.writeQueue.poll();
                    ec.headerIndex = 0;
                    ec.dataIndex = 0;
                    ec.state = STATE_HEADER;
                }
                break;
            }
        }
    }
}
