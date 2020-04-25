package com.sjm.pcr.common_component.service;

import com.sjm.pcr.common.model.ResInfo;

public interface CommonService {
    public ResInfo runCmd(String dir, String charset, String... cmdArray);
}
