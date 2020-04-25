package com.sjm.pcr.client;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.util.Misc;
import com.sjm.pcr.client_control.cv.BytePointer;
import com.sjm.pcr.client_control.cv.CvConstants;
import com.sjm.pcr.client_control.cv.CvFactory;
import com.sjm.pcr.client_control.cv.CvFactoryProvider;
import com.sjm.pcr.client_control.cv.CvUtil;
import com.sjm.pcr.client_control.cv.DoublePointer;
import com.sjm.pcr.client_control.cv.Mat;
import com.sjm.pcr.client_control.cv.Point;
import com.sjm.pcr.client_control.cv.Size;
import com.sjm.pcr.client_control.cv.TessBaseAPI;
import com.sjm.pcr.client_control.cv.opencv_core;
import com.sjm.pcr.client_control.cv.opencv_highgui;
import com.sjm.pcr.client_control.cv.opencv_imgcodecs;
import com.sjm.pcr.client_control.cv.opencv_imgproc;
import com.sjm.pcr.common.model.MatchResult;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;
import com.sjm.pcr.common.util.JdkUtil;
import com.sjm.pcr.common_component.service.MonitorService;

@Component
public class CvUtilImpl implements CvUtil {
    static final Logger logger = LoggerFactory.getLogger(CvUtilImpl.class);

    @Autowired
    private CvFactory cvFactory;

    @Autowired
    private opencv_core opencv_core;

    @Autowired
    private opencv_imgcodecs opencv_imgcodecs;

    @Autowired
    private opencv_imgproc opencv_imgproc;

    @Autowired
    private opencv_highgui opencv_highgui;

    @Autowired
    private MonitorService monitorService;

    public Mat imdecode(byte[] buf) {
        try (Mat matBuf = cvFactory.newMat(buf)) {
            return opencv_imgcodecs.imdecode(matBuf, CvConstants.IMREAD_UNCHANGED);
        }
    }

    public void resize(Mat src, Mat dst, double widthResize, double heightResize) {
        try (Size size = cvFactory.newSize((int) (src.rows() * widthResize),
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

    public MatchResult match(Mat image, Mat templ) {
        try (Mat dst = cvFactory.newMat();) {
            opencv_imgproc.matchTemplate(image, templ, dst, CvConstants.CV_TM_CCORR_NORMED);
            try (Point maxLoc = cvFactory.newPoint();
                    Point minLoc = cvFactory.newPoint();
                    DoublePointer dp1 = cvFactory.newDoublePointer(2);
                    DoublePointer dp2 = cvFactory.newDoublePointer(2);) {
                opencv_core.minMaxLoc(dst, dp1, dp2, minLoc, maxLoc, null);

                MatchResult result = new MatchResult();
                result.result = dp2.get();
                result.rect = new Rect(maxLoc.x(), maxLoc.y(), templ.cols(), templ.rows());
                return result;
            }
        }
    }

    public void drawRect(Mat img, Rect rect) {
        try (Point pt1 = cvFactory.newPoint(rect.x, rect.y);
                Point pt2 = cvFactory.newPoint(rect.x + rect.width, rect.y + rect.height);) {
            opencv_imgproc.rectangle(img, pt1, pt2, CvConstants.RED, 2, 8, 0);
        }
    }

    public String recognize(Mat mat, String lang, String whitelist) {
        try (TessBaseAPI api = cvFactory.newTessBaseAPI();) {
            if (api.Init("tessdata", lang) != 0) {
                System.err.println("Could not initialize tesseract.");
                System.exit(1);
            }
            api.SetImage(mat.data(), mat.cols(), mat.rows(), mat.channels(), (int) mat.step1());
            if (whitelist != null)
                api.SetVariable("tessedit_char_whitelist", whitelist);
            try (BytePointer outText = api.GetUTF8Text();) {
                String result = outText.getString().replace(" ", "").replace("\n", "");
                api.End();
                return result;
            }
        }
    }

    @Override
    public void click(int x, int y) {
        monitorService.click(x, y);
    }

    @Override
    public RectSize getWindowSize() {
        return monitorService.getWindowSize();
    }

    @Override
    public void input(String str) {
        monitorService.input(str);
    }

    @Override
    public Mat snapshot(Rect rect) {
        return monitorService.snapshot(rect);
    }

    @Override
    public void swipe(int fromx, int fromy, int tox, int toy, long time) {
        monitorService.swipe(fromx, fromy, tox, toy, time);
    }

    public void test() {
        RectSize size = getWindowSize();
        for (int i = 0; i < 10; i++) {
            Misc.startRecordTime();
            try (Mat mat = snapshot(new Rect(0, 0, size.width, size.height));) {
                System.out.println(mat);
                opencv_imgproc.cvtColor(mat, mat, CvConstants.COLOR_BGRA2GRAY);
                opencv_imgcodecs.imwrite("/root/aaa.png", mat);
            }
            Misc.showRecordTime();
        }
    }

    public static void main(String[] args) throws Exception {
        JdkUtil.loadJarByFile("/data/project/project_dev/javacv-package/javacv.jar");
        JdkUtil.loadJarByFile("/data/project/project_dev/javacv-package/javacv-linux-x86_64.jar");

        CvFactoryProvider provider = new CvFactoryProvider();
        CvFactory factory = provider.getFactory(CvFactory.class);

        opencv_highgui opencv_highgui = factory.newOpencv_highgui();

        Mat mYuv = factory.newMat(1280, 720, CvConstants.CV_8UC4);
        byte[] array = Files.readAllBytes(Paths.get("/root/texture.raw"));
        mYuv.data().put(array);
        opencv_highgui.imshow("hello", mYuv);
        opencv_highgui.waitKey(0);
    }
}
