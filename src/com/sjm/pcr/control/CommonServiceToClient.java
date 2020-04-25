package com.sjm.pcr.control;

import com.sjm.pcr.common_component.rpc.Remote;
import com.sjm.pcr.common_component.service.CommonService;

@Remote(value = "CommonServiceRemote", remote = RemoteCallClient.class, clazz = CommonService.class)
public interface CommonServiceToClient extends CommonService {
}
