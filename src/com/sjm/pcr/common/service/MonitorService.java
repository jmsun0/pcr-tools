package com.sjm.pcr.common.service;

import com.sjm.pcr.common.model.Picture;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;

public interface MonitorService {
    public RectSize getWindowSize();

    public Picture snapshot(Rect rect);

    public void click(int x, int y);

    public void input(String str);

    public void swipe(int fromx, int fromy, int tox, int toy, long time);
}
