package com.sjm.pcr.common_component.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Bean;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Value;

@Component
public class CommonConfiguration {
    static final Logger logger = LoggerFactory.getLogger(CommonConfiguration.class);

    @Value("${pcr.thread.pool.size:10}")
    private int threadPoolSize;

    @Bean
    private ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
