package com.sjm.pcr.common_component.service.impl;

import java.io.File;

import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.common.model.ResInfo;
import com.sjm.pcr.common.util.SystemUtil;
import com.sjm.pcr.common_component.service.CommonService;

@Component
public class CommonServiceImpl implements CommonService {

    public ResInfo runCmd(String dir, String charset, String... cmdArray) {
        return SystemUtil.runCmd(dir == null ? null : new File(dir), charset, cmdArray);
    }


}
