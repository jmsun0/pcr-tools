package com.sjm.pcr.control;

import com.sjm.pcr.common.rpc.Remote;
import com.sjm.pcr.common.service.MonitorService;

@Remote(remote = RemoteCallClient.class, clazz = MonitorService.class)
public interface MonitorServiceRemote extends MonitorService {

}
