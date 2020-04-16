package com.sjm.pcr.client;

import java.io.File;

import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.common.model.ResInfo;
import com.sjm.pcr.common.service.CommonService;
import com.sjm.pcr.common.util.SystemUtil;

@Component
public class CommonServiceImpl implements CommonService {

    public ResInfo runCmd(String dir, String charset, String... cmdArray) {
        return SystemUtil.runCmd(dir == null ? null : new File(dir), charset, cmdArray);
    }


}
