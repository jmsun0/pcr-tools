package com.sjm.pcr;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.SpringApplication;
import com.sjm.core.mini.springboot.api.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sjm.pcr.client", "com.sjm.pcr.client_control",
        "com.sjm.pcr.common"})
public class PCRClient {
    static final Logger logger = LoggerFactory.getLogger(PCRClient.class);

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(PCRClient.class);
        app.run(args);
    }
}
