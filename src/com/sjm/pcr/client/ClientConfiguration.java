package com.sjm.pcr.client;

import com.sjm.core.mini.springboot.api.Bean;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.util.Platform;
import com.sjm.pcr.common.service.MonitorService;

@Component
public class ClientConfiguration {

    @Bean
    private MonitorService getMonitorService() {
        if (Platform.isAndroid())
            return new MonitorServiceForAndroid();
        else
            return new MonitorServiceForLinux();
    }
}
