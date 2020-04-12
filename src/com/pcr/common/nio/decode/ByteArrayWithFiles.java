package com.pcr.common.nio.decode;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.pcr.common.core.Lists;

public class ByteArrayWithFiles implements Closeable {
    public byte[] data;
    public List<File> files = Lists.emptyList();

    @Override
    public void close() throws IOException {
        for (File file : files)
            file.delete();
    }
}
