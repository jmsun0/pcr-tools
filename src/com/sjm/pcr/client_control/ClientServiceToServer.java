package com.sjm.pcr.client_control;

import com.sjm.pcr.common_component.rpc.Remote;
import com.sjm.pcr.common_component.service.ClientService;

@Remote(remote = RemoteCallServer.class, clazz = ClientService.class)
public interface ClientServiceToServer extends ClientService {

}
