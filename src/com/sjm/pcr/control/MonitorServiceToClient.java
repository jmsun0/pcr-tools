package com.sjm.pcr.control;

import com.sjm.pcr.common_component.rpc.Remote;
import com.sjm.pcr.common_component.service.MonitorService;

@Remote(value = "MonitorServiceRemote", remote = RemoteCallClient.class,
        clazz = MonitorService.class)
public interface MonitorServiceToClient extends MonitorService {

}
