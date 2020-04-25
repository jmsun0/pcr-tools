package com.sjm.pcr.common_component.service;

import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;
import com.sjm.pcr.common_component.cv.Mat;

public interface MonitorService {
    public RectSize getWindowSize();

    public Mat snapshot(Rect rect);

    public void click(int x, int y);

    public void input(String str);

    public void swipe(int fromx, int fromy, int tox, int toy, long time);
}
