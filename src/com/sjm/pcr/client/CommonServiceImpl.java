package com.sjm.pcr.client;

import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.common.service.CommonService;

@Component
public class CommonServiceImpl implements CommonService {

    @Override
    public int getValue() {
        return 12345;
    }

}
