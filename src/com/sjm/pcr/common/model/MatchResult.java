package com.sjm.pcr.common.model;

public class MatchResult {
    public double result;
    public Rect rect;

    @Override
    public String toString() {
        return "result=" + result + ",rect=" + rect;
    }
}
