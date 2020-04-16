package com.sjm.pcr.common.service;

import com.sjm.pcr.common.model.ResInfo;

public interface CommonService {
    public ResInfo runCmd(String dir, String charset, String... cmdArray);
}
