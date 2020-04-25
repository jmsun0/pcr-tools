package com.sjm.pcr;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.SpringApplication;
import com.sjm.core.mini.springboot.api.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sjm.pcr.server", "com.sjm.pcr.common_component"})
public class PCRServer {
    static final Logger logger = LoggerFactory.getLogger(PCRServer.class);

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(PCRServer.class);
        app.run(args);
    }
}
