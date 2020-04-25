package com.sjm.pcr.control;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Resource;
import com.sjm.pcr.common_component.service.ClientService;
import com.sjm.pcr.common_component.service.CommonService;
import com.sjm.pcr.common_component.service.MonitorService;

@Component
public class ControlMain implements CommandLineRunner {
    static final Logger logger = LoggerFactory.getLogger(ControlMain.class);

    @Resource(name = "CommonServiceRemote")
    private CommonService commonService;

    @Autowired
    private ClientService clientManager;

    @Autowired
    private MonitorService monitorService;

    @Override
    public void run(String... args) throws Exception {
        // System.out.println(clientManager.listClient());
        // RemoteContext.get().setRemoteName("xxx");
        // ResInfo res = commonService.runCmd(null, null, "ps");
        // System.out.println(res);

        // System.out.println(monitorService.getWindowSize());
        // monitorService.input("123456");

        // System.exit(0);
    }
}
