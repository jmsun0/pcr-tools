package com.sjm.pcr.client;

import com.sjm.pcr.common.model.MatchResult;
import com.sjm.pcr.common.model.Picture;
import com.sjm.pcr.common.model.Rect;

public class CvSupport {

    public static Picture newPicture() {
        return null;
    }

    public static void recycle(Picture pic) {

    }

    public static Picture read(String path) {
        return null;
    }

    public static void write(String path, Picture pic) {

    }

    public static Picture resize(Picture pic, double widthResize, double heightResize) {
        return null;
    }

    public static Picture gray(Picture pic) {
        return null;
    }

    public static Picture bitNot(Picture pic) {
        return null;
    }

    public static Picture threshold(Picture pic, double thresh, double maxval, int type) {
        return null;
    }

    public static Picture binary(Picture pic) {
        return null;
    }

    public static MatchResult match(Picture pic, Picture sub) {
        return null;
    }

    public static void rectangle(Picture pic, Rect rect) {

    }

    public static String recognize(Picture pic, String lang, String whitelist) {
        return null;
    }
}
