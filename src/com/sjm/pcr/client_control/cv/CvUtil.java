package com.sjm.pcr.client_control.cv;

import com.sjm.pcr.common.model.MatchResult;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common_component.service.MonitorService;

public interface CvUtil extends MonitorService {
    public Mat imdecode(byte[] buf);

    public void resize(Mat src, Mat dst, double widthResize, double heightResize);

    public void gray(Mat src, Mat dst);

    public void thresholdBinary(Mat src, Mat dst, double thresh, double maxval);

    public void adaptiveThresholdBinary(Mat src, Mat dst, double maxValue, int blockSize, double C);

    public MatchResult match(Mat image, Mat templ);

    public void drawRect(Mat img, Rect rect);

    public String recognize(Mat mat, String lang, String whitelist);

    public void test();
}
