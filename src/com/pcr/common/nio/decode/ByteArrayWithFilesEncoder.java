package com.pcr.common.nio.decode;

public class ByteArrayWithFilesEncoder {

    // static final int STATE_WRITE_HEADER_SIZE = 0;
    // static final int STATE_WRITE_DATA_SIZE = 1;
    // static final int STATE_WRITE_FILES_SIZE = 2;
    // static final int STATE_WRITE_BYTE_ARRAY = 3;
    // static final int STATE_WRITE_FILES = 4;
    //
    // public static void encode(ChannelContext ctx, ByteArrayWithFiles packet) throws IOException {
    // ByteBufferPool bufferPool = ctx.bufferPool;
    // SocketChannel channel = ctx.channel;
    // ByteBuffer[] buffers = bufferPool.applyArray();
    // for (int i = 0; i < buffers.length; i++) {
    // buffers[i] = bufferPool.apply();
    // }
    // {
    // byte[] data = packet.data;
    // List<File> files = packet.files;
    // int state = STATE_WRITE_HEADER_SIZE;
    // int bufferIndex = 0;
    // int writedBytes = 0;
    // boolean flush = false;
    // L0: while (true) {
    // switch (state) {
    // case STATE_WRITE_HEADER_SIZE:
    // int headerSize = (files.size() + 2) * 4;
    // writedBytes += writeInt(buffers, bufferIndex, headerSize, 4 - writedBytes);
    // if (writedBytes == 4) {
    // state = STATE_WRITE_DATA_SIZE;
    // } else {
    // flush = true;
    // }
    // break;
    // case STATE_WRITE_DATA_SIZE:
    //
    // break;
    // case STATE_WRITE_FILES_SIZE:
    //
    // break;
    // case STATE_WRITE_BYTE_ARRAY:
    //
    // break;
    // case STATE_WRITE_FILES:
    //
    // break L0;
    // }
    // if (flush) {
    //
    // }
    // }
    // }
    // try {
    // ByteBuffer first = buffers[0];
    // byte[] data = packet.data;
    // List<File> files = packet.files;
    // int headerSize = (files.size() + 2) * 4;
    // if (headerSize > first.limit())
    // throw new IOException();
    // first.putInt(headerSize);
    // int index = headerSize + data.length;
    // first.putInt(index);
    // for (File file : files) {
    // index += (int) file.length();
    // first.putInt(index);
    // }
    // for (File file : files) {
    // try (FileChannel fc = FileChannel.open(file.toPath())) {
    // while (true) {
    // long n = fc.read(buffers);
    // if (n == -1) {
    // break;
    // } else if (n == 0) {
    // int total = 0;
    // for (ByteBuffer buffer : buffers) {
    // buffer.flip();
    // total += buffer.position();
    // }
    // long writed = channel.write(buffers);
    // if (writed != total)
    // throw new IOException("writed " + writed + " bytes");
    // }
    // }
    // }
    // }
    // } finally {
    // for (ByteBuffer buffer : buffers) {
    // bufferPool.recycle(buffer);
    // }
    // bufferPool.recycle(buffers);
    // }
    // }
    //
    // public static int writeInt(ByteBuffer[] buffers, int bufferIndex, int value, int remain) {
    // ByteBuffer buffer = buffers[bufferIndex];
    // if (buffer.remaining() >= 4) {
    // buffer.putInt(value);
    // return 4;
    // } else {
    //
    // }
    // }
}
