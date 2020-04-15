package com.sjm.pcr;

import com.sjm.core.mini.springboot.api.SpringApplication;
import com.sjm.core.mini.springboot.api.SpringBootApplication;

@SpringBootApplication
public class PCRServer {
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(PCRServer.class);
        app.run(args);
    }
}
