package com.sjm.pcr.client;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;

@Component
public class ClientMain implements CommandLineRunner {
    static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            Thread.sleep(60000);
        }
    }
}
