package com.sjm.pcr.common.model;

public class Rect {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rect(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect() {
        super();
    }


    @Override
    public String toString() {
        return "x=" + x + ",y=" + y + ",width=" + width + ",height=" + height;
    }
}
