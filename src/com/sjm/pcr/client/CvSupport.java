package com.sjm.pcr.client;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.pcr.common.model.MatchResult;
import com.sjm.pcr.common.model.Picture;
import com.sjm.pcr.common.model.Rect;

public abstract class CvSupport {
    static final Logger logger = LoggerFactory.getLogger(CvSupport.class);

    private static CvSupport inst;
    static {
        try {

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static CvSupport getInstance() {
        return inst;
    }

    public abstract Picture newPicture();

    public abstract void recycle(Picture pic);

    public abstract Picture read(String path);

    public abstract void write(String path, Picture pic);

    public abstract Picture resize(Picture pic, double widthResize, double heightResize);

    public abstract Picture gray(Picture pic);

    public abstract Picture bitNot(Picture pic);

    public abstract Picture threshold(Picture pic, double thresh, double maxval, int type);

    public abstract Picture binary(Picture pic);

    public abstract MatchResult match(Picture pic, Picture sub);

    public abstract void rectangle(Picture pic, Rect rect);

    public abstract String recognize(Picture pic, String lang, String whitelist);
}
