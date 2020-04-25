package com.sjm.pcr.client_control.cv;

public interface BytePointer extends CvObject {
    public String getString();

    public BytePointer put(byte... array);
}
