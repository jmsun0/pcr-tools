package com.sjm.pcr.common.service.impl;



import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.PostConstruct;
import com.sjm.core.mini.springboot.api.Value;
import com.sjm.pcr.common.rpc.impl.RemoteCallServer;
import com.sjm.pcr.common.service.CommonService;
import com.sjm.pcr.common.service.remote.CommonServiceRemote;

@Component
public class TestService {

    @Autowired
    private CommonService commonService;

    @Autowired
    private RemoteCallServer callServer;

    @Autowired
    private CommonServiceRemote commonServiceRemote;

    @Value("${aaa.aaa}")
    private int abc;

    @PostConstruct
    private void init() {
        System.out.println(abc);
        System.out.println(callServer);
        System.out.println(commonService);
        System.out.println(commonServiceRemote);
    }
}
