package com.sjm.pcr.client_control;

import com.sjm.pcr.common.rpc.Remote;
import com.sjm.pcr.common.service.ClientService;

@Remote(remote = RemoteCallServer.class, clazz = ClientService.class)
public interface ClientServiceRemote extends ClientService {

}
