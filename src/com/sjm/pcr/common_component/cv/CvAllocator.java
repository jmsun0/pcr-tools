package com.sjm.pcr.common_component.cv;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;

@Component
public class CvAllocator {
    static final Logger logger = LoggerFactory.getLogger(CvAllocator.class);

    public CvAllocator(CvFactory cvFactory) {
        this.cvFactory = cvFactory;
    }

    public CvAllocator() {}

    @Autowired
    private CvFactory cvFactory;

    public Mat newMat() {
        return cvFactory.allocate(Mat.class);
    }

    public Mat newMat(byte[] buf) {
        return cvFactory.allocate(Mat.class, buf);
    }

    public Mat newMat(int rows, int cols, int type) {
        return cvFactory.allocate(Mat.class, rows, cols, type);
    }

    public Size newSize(int _width, int _height) {
        return cvFactory.allocate(Size.class, _width, _height);
    }

    public Point newPoint() {
        return cvFactory.allocate(Point.class);
    }

    public Point newPoint(int _x, int _y) {
        return cvFactory.allocate(Point.class, _x, _y);
    }

    public DoublePointer newDoublePointer(long size) {
        return cvFactory.allocate(DoublePointer.class, size);
    }

    public TessBaseAPI newTessBaseAPI() {
        return cvFactory.allocate(TessBaseAPI.class);
    }
}
