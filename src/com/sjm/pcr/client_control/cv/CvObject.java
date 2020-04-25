package com.sjm.pcr.client_control.cv;

import java.io.Closeable;

public interface CvObject extends Closeable {

    @Override
    public void close();

    public Object getNativeObject();
}
