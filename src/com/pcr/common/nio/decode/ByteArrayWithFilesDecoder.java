package com.pcr.common.nio.decode;

/**
 * 
 * 报文类型： ByteArrayWithFiles
 * 
 * 报文结构： [header长度，包括自己(固定4字节)] [header(每个数据包结束位置偏移量，以4个字节为单位，变长)] [body][file1][file2]...
 * 
 * @author root
 *
 */
public class ByteArrayWithFilesDecoder /* extends ChannelDecoder */ {

    // private int maxBuffers;// 读取文件时最大缓存的buffer个数
    //
    // public ByteArrayWithFilesDecoder(int maxBuffers) {
    // this.maxBuffers = maxBuffers;
    // }
    //
    // static final int STATE_READ_HEADER_SIZE = 0;
    // static final int STATE_READ_HEADER = 1;
    // static final int STATE_READ_BYTE_ARRAY = 2;
    // static final int STATE_READ_FILES = 3;
    //
    // static class DecodeContext implements Closeable {
    // public int startIndex;// 开始索引
    // public int headerSize;// 数据头大小
    // public int[] bodyOffsets;// 每个数据体结束位置对应开始索引的偏移量
    // public int bodyIndex;// 当前正处理的数据体索引
    // public ByteArrayWithFiles packet;// 正在处理的数据包
    // public OutputStream out;// 正在写入的文件流
    // public int clearSize;// 已清空的数据量
    //
    // @Override
    // public void close() throws IOException {
    // Misc.close(out);
    // Misc.close(packet);
    // }
    // }
    //
    // @Override
    // public int move(ChannelContext ctx, PacketProcesser processer) throws IOException {
    // DecodeContext dc = (DecodeContext) ctx.decodeCotext;
    // if (dc == null)
    // ctx.decodeCotext = dc = new DecodeContext();
    // switch (ctx.state) {
    // case STATE_READ_HEADER_SIZE:
    // if (ctx.readSize >= 4) {
    // dc.headerSize = NIOTools.readInt(ctx.buffers, dc.startIndex);
    // return STATE_READ_HEADER;
    // }
    // break;
    // case STATE_READ_HEADER:
    // if (ctx.readSize >= dc.headerSize) {
    // if (dc.headerSize % 4 != 0)
    // throw new IOException();
    // int headerLen = dc.headerSize / 4 - 1;
    // if (headerLen < 1)
    // throw new IOException();
    // dc.bodyOffsets = new int[headerLen];
    // for (int i = 0; i < headerLen; i++) {
    // dc.bodyOffsets[i] =
    // NIOTools.readInt(ctx.buffers, dc.startIndex + 4 + i * 4);
    // }
    // dc.packet = new ByteArrayWithFiles();
    // if (headerLen > 1) {
    // dc.packet.files = new ArrayList<>();
    // }
    // return STATE_READ_BYTE_ARRAY;
    // }
    // break;
    // case STATE_READ_BYTE_ARRAY:
    // int bodyOffset = dc.bodyOffsets[dc.bodyIndex];
    // if (ctx.readSize >= bodyOffset) {
    // dc.packet.data = NIOTools.getBytes(ctx.buffers, dc.startIndex + dc.headerSize,
    // bodyOffset);
    // dc.bodyIndex++;
    // return STATE_READ_FILES;
    // }
    // break;
    // case STATE_READ_FILES:
    // int realStartIndex = dc.startIndex - dc.clearSize;
    // if (dc.bodyIndex == dc.bodyOffsets.length) {
    // processer.process(ctx, dc.packet);
    // int totalSize = dc.bodyOffsets[dc.bodyOffsets.length - 1];
    // ctx.readSize -= totalSize;
    // dc.startIndex = NIOTools.recycle(ctx.buffers, ctx.bufferPool,
    // totalSize + realStartIndex);
    // dc.bodyIndex = 0;
    // dc.clearSize = 0;
    // dc.packet = null;
    // return STATE_READ_HEADER_SIZE;
    // }
    // bodyOffset = dc.bodyOffsets[dc.bodyIndex];
    // if (ctx.readSize >= bodyOffset || ctx.buffers.size() > maxBuffers) {
    // if (dc.out == null) {
    // File file = Files.createTempFile(null, null).toFile();
    // dc.packet.files.add(file);
    // dc.out = new FileOutputStream(file);
    // }
    // int begin = Math.max(dc.bodyOffsets[dc.bodyIndex - 1] + realStartIndex, 0);
    // int end = Math.min(bodyOffset, ctx.readSize) + realStartIndex;
    // NIOTools.write(ctx.buffers, begin, end, dc.out);
    // dc.clearSize += end - NIOTools.recycle(ctx.buffers, ctx.bufferPool, end);
    // if (ctx.readSize >= bodyOffset) {
    // Misc.close(dc.out);
    // dc.out = null;
    // dc.bodyIndex++;
    // }
    // return STATE_READ_FILES;
    // }
    // break;
    // }
    // return -1;
    // }
}
