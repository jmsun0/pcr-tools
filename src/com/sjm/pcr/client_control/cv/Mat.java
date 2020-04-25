package com.sjm.pcr.client_control.cv;

public interface Mat extends CvObject {
    public int rows();

    public int cols();

    public int depth();

    public int channels();

    public long step1();

    public BytePointer data();
}
