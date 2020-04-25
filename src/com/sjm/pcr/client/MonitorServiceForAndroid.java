package com.sjm.pcr.client;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Condition;
import com.sjm.core.mini.springboot.api.Conditional;
import com.sjm.core.util.Misc;
import com.sjm.core.util.Platform;
import com.sjm.pcr.client_control.cv.Mat;
import com.sjm.pcr.common.exception.ServiceException;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;
import com.sjm.pcr.common.model.ResInfo;
import com.sjm.pcr.common.util.SystemUtil;
import com.sjm.pcr.common_component.service.MonitorService;

@Conditional({MonitorServiceForAndroid.IsAndroidCondition.class})
@Component
public class MonitorServiceForAndroid implements MonitorService {
    static final Logger logger = LoggerFactory.getLogger(MonitorServiceForAndroid.class);

    public static class IsAndroidCondition implements Condition {
        @Override
        public boolean matches(Class<?> clazz) {
            return Platform.isAndroid();
        }
    }

    @Autowired
    private CvUtilImpl cvUtil;

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
    public Mat snapshot(Rect rect) {
        try {
            Object bitmap = screenshot(rect.x, rect.y, rect.width, rect.height);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            compress(bitmap, "PNG", out);
            return cvUtil.imdecode(out.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    private static Object/* Bitmap */ screenshot(int x, int y, int w, int h) throws Exception {
        Class<?> surfaceClazz = Class.forName("android.view.SurfaceControl");
        Class<?> rectClazz = Class.forName("android.graphics.Rect");
        Method screenshotMethod = surfaceClazz.getDeclaredMethod("screenshot", rectClazz, int.class,
                int.class, int.class);
        Constructor<?> constructor =
                rectClazz.getConstructor(int.class, int.class, int.class, int.class);
        Object rect = constructor.newInstance(x, y, x + w, y + h);
        return screenshotMethod.invoke(null, new Object[] {rect, w, h, 0});
    }

    private static void compress(Object bitmap, String fmt/* PNG|JPEG */, OutputStream out)
            throws Exception {
        Class<?> bitmapClazz = Class.forName("android.graphics.Bitmap");
        Class<?> formatClazz = Class.forName("android.graphics.Bitmap$CompressFormat");
        Method compressMethod = bitmapClazz.getDeclaredMethod("compress", formatClazz, int.class,
                OutputStream.class);
        Object format = formatClazz.getDeclaredField(fmt).get(null);
        compressMethod.invoke(bitmap, format, 100, out);
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
