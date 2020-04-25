package com.sjm.pcr;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.SpringApplication;
import com.sjm.core.mini.springboot.api.SpringBootApplication;
import com.sjm.pcr.common.util.JdkUtil;

@SpringBootApplication(scanBasePackages = {"com.sjm.pcr.client", "com.sjm.pcr.common_component",
        "com.sjm.pcr.client_control"})
public class PCRClient {
    static final Logger logger = LoggerFactory.getLogger(PCRClient.class);

    public static void main(String[] args) throws Exception {
        JdkUtil.loadJarByFile("/data/project/project_dev/javacv-package/javacv.jar");
        JdkUtil.loadJarByFile("/data/project/project_dev/javacv-package/javacv-linux-x86_64.jar");

        SpringApplication app = new SpringApplication(PCRClient.class);
        app.run(args);
    }
}
