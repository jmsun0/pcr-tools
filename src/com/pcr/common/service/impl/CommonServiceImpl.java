package com.pcr.common.service.impl;

import static org.bytedeco.opencv.global.opencv_core.bitwise_not;
import static org.bytedeco.opencv.global.opencv_core.minMaxLoc;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_TM_CCORR_NORMED;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.adaptiveThreshold;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.matchTemplate;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.tesseract.TessBaseAPI;

public class CommonServiceImpl {
    private Robot robot;
    {
        try {
            robot = new Robot();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Mat read(String path) {
        return imread(path);
    }

    public void write(String path, Mat mat) {
        imwrite(path, mat);
    }

    public Mat resize(Mat source, double resize) {
        Mat dst = new Mat((int) (source.rows() * resize), (int) (source.cols() * resize),
                source.type());
        opencv_imgproc.resize(source, dst, dst.size());
        return dst;
    }

    public void gray(Mat mat) {
        cvtColor(mat, mat, COLOR_BGRA2GRAY);
    }

    public void bitNot(Mat mat) {
        bitwise_not(mat, mat);
    }

    public void thresholdBinary(Mat mat, int threshold) {
        threshold(mat, mat, threshold, 255, THRESH_BINARY);
    }

    public void binary(Mat mat) {
        adaptiveThreshold(mat, mat, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 25, 10);
    }

    private final Rectangle SCREEN_RECT =
            new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                    Toolkit.getDefaultToolkit().getScreenSize().height);

    public Rectangle getScreenRect() {
        return SCREEN_RECT;
    }

    private Rectangle TASK_BAR_RECT;
    {
        Insets screenInsets = Toolkit.getDefaultToolkit()
                .getScreenInsets(new JFrame().getGraphicsConfiguration());
        int taskBarHeight = screenInsets.bottom;
        int screenWidth = getScreenRect().width;
        int screenHeight = getScreenRect().height;
        TASK_BAR_RECT = new Rectangle(0, screenHeight - taskBarHeight, screenWidth, taskBarHeight);
    }

    public Rectangle getTaskBarRect() {
        return TASK_BAR_RECT;
    }

    public Mat snapshot(Rectangle rect) {
        try {
            BufferedImage image = robot.createScreenCapture(rect);
            File tmp = new File("D:/tmp/" + System.currentTimeMillis() + ".png");
            try {
                ImageIO.write(image, "png", tmp);
                return read(tmp.getPath());
            } finally {
                tmp.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class MatchResult {
        public double result;
        public Rectangle rect;

        @Override
        public String toString() {
            return "result=" + result + ",rect=" + rect;
        }
    }

    public MatchResult match(Mat img, Mat image) {
        Mat dst = new Mat();
        matchTemplate(img, image, dst, CV_TM_CCORR_NORMED);

        Point maxLoc = new Point();
        Point minLoc = new Point();
        DoublePointer dp1 = new DoublePointer(2);
        DoublePointer dp2 = new DoublePointer(2);

        minMaxLoc(dst, dp1, dp2, minLoc, maxLoc, null);
        MatchResult result = new MatchResult();
        result.result = dp2.get();
        result.rect = new Rectangle(maxLoc.x(), maxLoc.y(), image.cols(), image.rows());
        return result;
    }

    public void drawRect(Mat img, Rectangle rect) {
        Point pt1 = new Point(rect.x, rect.y);
        Point pt2 = new Point(rect.x + rect.width, rect.y + rect.height);
        rectangle(img, pt1, pt2, Scalar.RED, 2, 8, 0);
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickMouse(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        sleep(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void doubleClickMouse(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        sleep(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        sleep(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void clickKey(int key) {
        robot.keyPress(key);
        robot.delay(50);
        robot.keyRelease(key);
        robot.delay(100);
    }

    public void clickCombineKey(int key1, int key2) {
        robot.keyPress(key1);
        robot.delay(50);
        robot.keyPress(key2);
        robot.delay(100);
        robot.keyRelease(key1);
        robot.delay(50);
        robot.keyRelease(key2);
        robot.delay(100);
    }

    public void input(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int key = KeyEvent.getExtendedKeyCodeForChar(ch);
            if (Character.isUpperCase(ch)) {
                clickCombineKey(KeyEvent.VK_SHIFT, key);
            } else {
                clickKey(key);
            }
        }
    }

    public void drag(int fromx, int fromy, int tox, int toy, long time) {
        robot.mouseMove(fromx, fromy);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        for (int i = 0; i < 100; i++) {
            int x = ((tox * i) / 100) + (fromx * (100 - i) / 100);
            int y = ((toy * i) / 100) + (fromy * (100 - i) / 100);
            robot.mouseMove(x, y);
            sleep(time / 100);
        }
        robot.mouseMove(tox, toy);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public String recognize(Mat mat, String lang, String whitelist) {
        TessBaseAPI api = new TessBaseAPI();
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
        outText.deallocate();
        api.close();
        return result;
    }

    public int getNumber(String img) {
        return Integer.parseInt(recognize(read(img), "eng", "0123456789"));
    }

    public int getNumber(Mat mat) {
        try {
            File tmp = File.createTempFile("ocr_img_", ".png");
            try {
                write(tmp.getPath(), mat);
                return getNumber(tmp.getPath());
            } finally {
                tmp.delete();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
