package com.sjm.pcr.common.model;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Point() {
        super();
    }

    @Override
    public String toString() {
        return "x=" + x + ",y=" + y;
    }
}
