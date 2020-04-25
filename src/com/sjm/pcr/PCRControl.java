package com.sjm.pcr;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.SpringApplication;
import com.sjm.core.mini.springboot.api.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sjm.pcr.control", "com.sjm.pcr.common_component",
        "com.sjm.pcr.client_control"})
public class PCRControl {
    static final Logger logger = LoggerFactory.getLogger(PCRControl.class);

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(PCRControl.class);
        app.run(args);
    }
}
