package com.sjm.pcr.common_component.cv;

public interface opencv_highgui extends CvObject {
    public void imshow(String winname, Mat mat);

    public int waitKey(int delay);
}
