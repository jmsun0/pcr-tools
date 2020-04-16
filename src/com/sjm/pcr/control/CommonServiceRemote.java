package com.sjm.pcr.control;

import com.sjm.pcr.common.rpc.Remote;
import com.sjm.pcr.common.service.CommonService;

@Remote(remote = RemoteCallClient.class, clazz = CommonService.class)
public interface CommonServiceRemote extends CommonService {
}
