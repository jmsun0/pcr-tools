package com.sjm.pcr.common_component.cv;

import java.io.Closeable;

public interface CvObject extends Closeable {

    @Override
    public void close();

    public Object getNativeObject();
}
