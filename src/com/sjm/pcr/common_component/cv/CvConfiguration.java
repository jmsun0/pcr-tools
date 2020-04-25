package com.sjm.pcr.common_component.cv;

import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Bean;
import com.sjm.core.mini.springboot.api.Component;

@Component
public class CvConfiguration {

    @Autowired
    private CvFactory cvFactory;

    @Bean
    private opencv_imgcodecs getOpencv_imgcodecs() {
        return cvFactory.allocate(opencv_imgcodecs.class);
    }

    @Bean
    private opencv_core getOpencv_core() {
        return cvFactory.allocate(opencv_core.class);
    }

    @Bean
    private opencv_imgproc getOpencv_imgproc() {
        return cvFactory.allocate(opencv_imgproc.class);
    }

    @Bean
    private opencv_highgui getOpencv_highgui() {
        return cvFactory.allocate(opencv_highgui.class);
    }
}
