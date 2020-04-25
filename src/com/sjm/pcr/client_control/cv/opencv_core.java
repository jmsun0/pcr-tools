package com.sjm.pcr.client_control.cv;

public interface opencv_core extends CvObject {
    public void minMaxLoc(Mat src, DoublePointer minVal, DoublePointer maxVal, Point minLoc,
            Point maxLoc, Mat mask);

    public void bitwise_not(Mat src, Mat dst);
}
