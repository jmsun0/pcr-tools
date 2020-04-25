package com.sjm.pcr.client_control.cv;

public interface CvFactory {

    public opencv_imgcodecs newOpencv_imgcodecs();

    public opencv_core newOpencv_core();

    public opencv_imgproc newOpencv_imgproc();

    public opencv_highgui newOpencv_highgui();

    public Mat newMat();

    public Mat newMat(byte[] buf);

    public Mat newMat(int rows, int cols, int type);

    public Size newSize(int _width, int _height);

    public Point newPoint();

    public Point newPoint(int _x, int _y);

    public DoublePointer newDoublePointer(long size);

    public TessBaseAPI newTessBaseAPI();
}
