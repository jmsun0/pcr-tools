package com.sjm.pcr.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.exception.ServiceException;
import com.sjm.pcr.common.model.Picture;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;
import com.sjm.pcr.common.model.ResInfo;
import com.sjm.pcr.common.service.MonitorService;
import com.sjm.pcr.common.util.SystemUtil;

public class MonitorServiceForAndroid implements MonitorService {
    static final Logger logger = LoggerFactory.getLogger(MonitorServiceForAndroid.class);

    public static void main(String[] args) {
        String[] group = Misc.getAllGroup(WM_SIZE_PATTERN, "Physical size: 1080x2340");
        System.out.println(Arrays.toString(group));
    }

    private String runCmd(String... cmdArray) {
        ResInfo res = SystemUtil.runCmd(null, null, cmdArray);
        if (res.code != 0)
            throw new ServiceException(res.code, res.err);
        return res.out;
    }

    private static final Pattern WM_SIZE_PATTERN = Pattern.compile("([0-9]+)x([0-9]+)");

    @Override
    public RectSize getWindowSize() {
        String res = runCmd("wm", "size");
        String[] group = Misc.getAllGroup(WM_SIZE_PATTERN, res);
        if (group == null)
            throw new ServiceException("wm size not matches:" + res);
        return new RectSize(Integer.parseInt(group[0]), Integer.parseInt(group[1]));
    }

    @Override
    public Picture snapshot(Rect rect) {
        File tmp;
        try {
            tmp = Files.createTempFile(null, ".png").toFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        try {
            runCmd("screencap", "-p", tmp.getPath());
            return CvSupport.read(tmp.getPath());
        } finally {
            tmp.delete();
        }
    }

    @Override
    public void click(int x, int y) {
        runCmd("input", "tap", String.valueOf(x), String.valueOf(y));
    }

    @Override
    public void input(String str) {
        runCmd("input", "text", str);
    }

    @Override
    public void swipe(int fromx, int fromy, int tox, int toy, long time) {
        runCmd("input", "swipe", String.valueOf(fromx), String.valueOf(fromy), String.valueOf(tox),
                String.valueOf(toy), String.valueOf(time));
    }
}
