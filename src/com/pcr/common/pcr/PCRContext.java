package com.pcr.common.pcr;

import java.io.IOException;

import com.pcr.common.nio.core.ChannelContext;
import com.pcr.common.nio.decode.ByteArrayPacketEncoder;

public class PCRContext extends ChannelContext {

    public void write(byte[] packet) throws IOException {
        ByteArrayPacketEncoder.encode(this, packet);
    }
}
