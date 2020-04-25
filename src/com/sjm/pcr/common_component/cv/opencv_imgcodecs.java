package com.sjm.pcr.common_component.cv;

public interface opencv_imgcodecs extends CvObject {
    public Mat imread(String path);

    public Mat imdecode(Mat buf, int flags);

    public void imwrite(String path, Mat mat);
}
