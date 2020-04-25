package com.sjm.pcr.common_component.cv;

public interface BytePointer extends CvObject {
    public String getString();

    public BytePointer put(byte... array);
}
