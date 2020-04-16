package com.sjm.pcr.client;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Value;
import com.sjm.pcr.common.service.ClientService;

@Component
public class ClientMain implements CommandLineRunner {
    static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    @Value("${pcr.client.name:test}")
    private String name;

    @Autowired
    private ClientService clientManager;

    @Override
    public void run(String... args) throws Exception {
        clientManager.register(name);
        logger.info("register client [{}] ok", name);
    }
}
