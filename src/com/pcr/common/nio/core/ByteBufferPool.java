package com.pcr.common.nio.core;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class ByteBufferPool {
    private int bufferSize;
    private Queue<ByteBuffer> buffers;

    public ByteBufferPool(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffers = new LinkedList<>();
    }

    public ByteBuffer apply() {
        ByteBuffer buffer = buffers.poll();
        if (buffer == null)
            buffer = ByteBuffer.allocate(bufferSize);
        return buffer;
    }

    public void recycle(ByteBuffer buffer) {
        if (buffer.capacity() != bufferSize)
            throw new IllegalArgumentException();
        buffer.clear();
        buffers.offer(buffer);
    }
}
