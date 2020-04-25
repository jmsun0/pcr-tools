package com.sjm.pcr.client;

import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Bean;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.client_control.cv.CvFactory;
import com.sjm.pcr.client_control.cv.opencv_core;
import com.sjm.pcr.client_control.cv.opencv_highgui;
import com.sjm.pcr.client_control.cv.opencv_imgcodecs;
import com.sjm.pcr.client_control.cv.opencv_imgproc;

@Component
public class CvConfiguration {

    @Autowired
    private CvFactory cvFactory;

    @Bean
    private opencv_imgcodecs getOpencv_imgcodecs() {
        return cvFactory.newOpencv_imgcodecs();
    }

    @Bean
    private opencv_core getOpencv_core() {
        return cvFactory.newOpencv_core();
    }

    @Bean
    private opencv_imgproc getOpencv_imgproc() {
        return cvFactory.newOpencv_imgproc();
    }

    @Bean
    private opencv_highgui getOpencv_highgui() {
        return cvFactory.newOpencv_highgui();
    }
}
