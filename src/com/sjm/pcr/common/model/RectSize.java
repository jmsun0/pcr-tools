package com.sjm.pcr.common.model;

public class RectSize {
    public int width;
    public int height;

    public RectSize(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public RectSize() {
        super();
    }

    @Override
    public String toString() {
        return "width=" + width + ",height=" + height;
    }
}
