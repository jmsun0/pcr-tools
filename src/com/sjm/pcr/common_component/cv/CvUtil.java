package com.sjm.pcr.common_component.cv;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.client.CvProxyFactory;
import com.sjm.pcr.common.model.MatchResult;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.util.JdkUtil;

@Component
public class CvUtil {
    static final Logger logger = LoggerFactory.getLogger(CvUtil.class);

    @Autowired
    private CvAllocator cvAllocator;

    @Autowired
    private opencv_core opencv_core;

    @Autowired
    private opencv_imgcodecs opencv_imgcodecs;

    @Autowired
    private opencv_imgproc opencv_imgproc;

    @Autowired
    private opencv_highgui opencv_highgui;

    public Mat imdecode(byte[] buf) {
        try (Mat matBuf = cvAllocator.newMat(buf)) {
            return opencv_imgcodecs.imdecode(matBuf, CvConstants.IMREAD_UNCHANGED);
        }
    }

    public void resize(Mat src, Mat dst, double widthResize, double heightResize) {
        try (Size size = cvAllocator.newSize((int) (src.rows() * widthResize),
                (int) (src.cols() * heightResize))) {
            opencv_imgproc.resize(src, dst, size);
        }
    }

    public void gray(Mat src, Mat dst) {
        opencv_imgproc.cvtColor(src, dst, CvConstants.COLOR_BGRA2GRAY);
    }

    public void thresholdBinary(Mat src, Mat dst, double thresh, double maxval) {
        opencv_imgproc.threshold(src, dst, thresh, maxval, CvConstants.THRESH_BINARY);
    }

    public void adaptiveThresholdBinary(Mat src, Mat dst, double maxValue, int blockSize,
            double C) {
        opencv_imgproc.adaptiveThreshold(src, dst, maxValue, CvConstants.ADAPTIVE_THRESH_GAUSSIAN_C,
                CvConstants.THRESH_BINARY, blockSize, C);
    }

    public MatchResult match(Mat img, Mat image) {
        Mat dst = cvAllocator.newMat();
        opencv_imgproc.matchTemplate(img, image, dst, CvConstants.CV_TM_CCORR_NORMED);

        Point maxLoc = cvAllocator.newPoint();
        Point minLoc = cvAllocator.newPoint();
        DoublePointer dp1 = cvAllocator.newDoublePointer(2);
        DoublePointer dp2 = cvAllocator.newDoublePointer(2);

        opencv_core.minMaxLoc(dst, dp1, dp2, minLoc, maxLoc, null);
        dst.close();
        MatchResult result = new MatchResult();
        result.result = dp2.get();
        result.rect = new Rect(maxLoc.x(), maxLoc.y(), image.cols(), image.rows());
        return result;
    }

    public void drawRect(Mat img, Rect rect) {
        Point pt1 = cvAllocator.newPoint(rect.x, rect.y);
        Point pt2 = cvAllocator.newPoint(rect.x + rect.width, rect.y + rect.height);
        opencv_imgproc.rectangle(img, pt1, pt2, CvConstants.RED, 2, 8, 0);
    }

    public String recognize(Mat mat, String lang, String whitelist) {
        TessBaseAPI api = cvAllocator.newTessBaseAPI();
        if (api.Init("tessdata", lang) != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        api.SetImage(mat.data(), mat.cols(), mat.rows(), mat.channels(), (int) mat.step1());
        if (whitelist != null)
            api.SetVariable("tessedit_char_whitelist", whitelist);
        BytePointer outText = api.GetUTF8Text();
        String result = outText.getString().replace(" ", "").replace("\n", "");
        api.End();
        outText.close();
        api.close();
        return result;
    }

    public static void main(String[] args) throws Exception {
        JdkUtil.loadJarByFile("/data/project/project_dev/javacv-package/javacv.jar");
        JdkUtil.loadJarByFile("/data/project/project_dev/javacv-package/javacv-linux-x86_64.jar");

        CvFactory factory = new CvProxyFactory();
        CvAllocator cvAllocator = new CvAllocator(factory);
        opencv_highgui opencv_highgui = factory.allocate(opencv_highgui.class);

        Mat mYuv = cvAllocator.newMat(1280, 720, CvConstants.CV_8UC4);
        byte[] array = Files.readAllBytes(Paths.get("/root/texture.raw"));
        mYuv.data().put(array);
        opencv_highgui.imshow("hello", mYuv);
        opencv_highgui.waitKey(0);
    }
}
