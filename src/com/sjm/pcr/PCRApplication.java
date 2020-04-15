package com.sjm.pcr;

import com.sjm.common.mini.springboot.api.SpringApplication;
import com.sjm.common.mini.springboot.api.SpringBootApplication;

@SpringBootApplication
public class PCRApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(PCRApplication.class);
        app.run(args);
    }
}
