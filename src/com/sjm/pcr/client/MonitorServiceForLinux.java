package com.sjm.pcr.client;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.exception.ServiceException;
import com.sjm.pcr.common.model.Picture;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;
import com.sjm.pcr.common.service.MonitorService;

public class MonitorServiceForLinux implements MonitorService {
    static final Logger logger = LoggerFactory.getLogger(MonitorServiceForLinux.class);

    public static void main(String[] args) {}

    private Robot robot;
    {
        try {
            robot = new Robot();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public RectSize getWindowSize() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        return new RectSize(d.width, d.height);
    }

    @Override
    public Picture snapshot(Rect rect) {
        try {
            BufferedImage image = robot
                    .createScreenCapture(new Rectangle(rect.x, rect.y, rect.width, rect.height));
            File tmp = Files.createTempFile(null, ".png").toFile();
            try {
                ImageIO.write(image, "png", tmp);
                return CvSupport.read(tmp.getPath());
            } finally {
                tmp.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Misc.sleep(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    @Override
    public void input(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int key = KeyEvent.getExtendedKeyCodeForChar(ch);
            if (Character.isUpperCase(ch)) {
                combineKey(KeyEvent.VK_SHIFT, key);
            } else {
                key(key);
            }
        }
    }

    private void key(int key) {
        robot.keyPress(key);
        robot.delay(50);
        robot.keyRelease(key);
        robot.delay(100);
    }

    private void combineKey(int key1, int key2) {
        robot.keyPress(key1);
        robot.delay(50);
        robot.keyPress(key2);
        robot.delay(100);
        robot.keyRelease(key1);
        robot.delay(50);
        robot.keyRelease(key2);
        robot.delay(100);
    }

    @Override
    public void swipe(int fromx, int fromy, int tox, int toy, long time) {
        robot.mouseMove(fromx, fromy);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        for (int i = 0; i < 100; i++) {
            int x = ((tox * i) / 100) + (fromx * (100 - i) / 100);
            int y = ((toy * i) / 100) + (fromy * (100 - i) / 100);
            robot.mouseMove(x, y);
            Misc.sleep(time / 100);
        }
        robot.mouseMove(tox, toy);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
