package com.sjm.pcr.client;

import com.sjm.core.mini.springboot.api.Bean;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.client_control.cv.CvFactory;
import com.sjm.pcr.client_control.cv.CvFactoryProvider;

@Component
public class CvLocalFactoryConfiguration {

    @Bean
    private CvFactory getCvFactory() {
        CvFactoryProvider provider = new CvFactoryProvider();
        return provider.getFactory(CvFactory.class);
    }
}
