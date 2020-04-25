package com.sjm.pcr.control;

import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Bean;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.client_control.cv.CvFactory;
import com.sjm.pcr.client_control.cv.CvUtil;
import com.sjm.pcr.client_control.cv.opencv_core;
import com.sjm.pcr.client_control.cv.opencv_highgui;
import com.sjm.pcr.client_control.cv.opencv_imgcodecs;
import com.sjm.pcr.client_control.cv.opencv_imgproc;

@Component
public class CvConfiguration {

    @Autowired
    private CvRemoteFactory cvRemoteFactory;

    @Bean
    public CvFactory getCvFactory() {
        return cvRemoteFactory.getProxyObject(CvFactory.class, 0);
    }

    @Bean
    private opencv_imgcodecs getOpencv_imgcodecs() {
        return cvRemoteFactory.getProxyObject(opencv_imgcodecs.class, 0);
    }

    @Bean
    private opencv_core getOpencv_core() {
        return cvRemoteFactory.getProxyObject(opencv_core.class, 0);
    }

    @Bean
    private opencv_imgproc getOpencv_imgproc() {
        return cvRemoteFactory.getProxyObject(opencv_imgproc.class, 0);
    }

    @Bean
    private opencv_highgui getOpencv_highgui() {
        return cvRemoteFactory.getProxyObject(opencv_highgui.class, 0);
    }

    @Bean
    private CvUtil getCvUtil() {
        return cvRemoteFactory.getProxyObject(CvUtil.class, 0);
    }
}
