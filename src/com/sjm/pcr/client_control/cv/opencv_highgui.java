package com.sjm.pcr.client_control.cv;

public interface opencv_highgui extends CvObject {
    public void imshow(String winname, Mat mat);

    public int waitKey(int delay);
}
