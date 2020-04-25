package com.sjm.pcr.client_control.cv;

public class CvConstants {
    /**
     * 
     * Used by: new Mat
     * 
     * {@link org.opencv.core.CvType#CV_8UC1}
     */
    public static final int CV_8UC1 = 0, CV_8UC2 = 8, CV_8UC3 = 16, CV_8UC4 = 24;
    /**
     * 
     * Used by: imdecode
     * 
     * {@link org.bytedeco.opencv.global.opencv_imgcodecs#IMREAD_UNCHANGED}
     */
    public static final int IMREAD_UNCHANGED = -1;
    /**
     * 
     * Used by: adaptiveThreshold.adaptiveMethod
     * 
     * {@link org.bytedeco.opencv.global.opencv_imgproc#ADAPTIVE_THRESH_GAUSSIAN_C}
     */
    public static final int ADAPTIVE_THRESH_GAUSSIAN_C = 1;
    /**
     * 
     * Used by: cvtColor
     * 
     * {@link org.bytedeco.opencv.global.opencv_imgproc#COLOR_BGRA2GRAY}
     */
    public static final int COLOR_BGRA2GRAY = 10;
    /**
     * 
     * Used by: matchTemplate
     * 
     * {@link org.bytedeco.opencv.global.opencv_imgproc#CV_TM_CCORR_NORMED}
     */
    public static final int CV_TM_CCORR_NORMED = 3;
    /**
     * 
     * Used by: threshold|adaptiveThreshold.thresholdType
     * 
     * {@link org.bytedeco.opencv.global.opencv_imgproc#THRESH_BINARY}
     */
    public static final int THRESH_BINARY = 0;

    public static Scalar RED;
}
