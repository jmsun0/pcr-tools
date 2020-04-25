package com.sjm.pcr.common_component.cv;

public interface TessBaseAPI extends CvObject {
    public int Init(String datapath, String language);

    public void SetImage(BytePointer imagedata, int width, int height, int bytes_per_pixel,
            int bytes_per_line);

    public boolean SetVariable(String name, String value);

    public BytePointer GetUTF8Text();

    public void End();
}
