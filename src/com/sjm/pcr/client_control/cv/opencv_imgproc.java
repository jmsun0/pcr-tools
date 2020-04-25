package com.sjm.pcr.client_control.cv;

public interface opencv_imgproc extends CvObject {
    public void resize(Mat src, Mat dst, Size dsize);

    public void cvtColor(Mat src, Mat dst, int code);

    public void threshold(Mat src, Mat dst, double thresh, double maxval, int type);

    public void adaptiveThreshold(Mat src, Mat dst, double maxValue, int adaptiveMethod,
            int thresholdType, int blockSize, double C);

    public void matchTemplate(Mat image, Mat templ, Mat result, int method);

    public void rectangle(Mat img, Point pt1, Point pt2, Scalar color, int thickness, int lineType,
            int shift);
}
