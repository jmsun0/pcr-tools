package com.sjm.pcr.service.impl;

import com.sjm.common.mini.springboot.api.Autowired;
import com.sjm.common.mini.springboot.api.Component;
import com.sjm.common.mini.springboot.api.PostConstruct;
import com.sjm.common.mini.springboot.api.Value;
import com.sjm.pcr.base.rpc.impl.RemoteCallServer;
import com.sjm.pcr.service.CommonService;
import com.sjm.pcr.service.remote.CommonServiceRemote;

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
