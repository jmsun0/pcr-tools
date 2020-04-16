package com.sjm.pcr.common.service;

import java.io.File;
import java.util.List;

import com.sjm.pcr.common.rpc.RemoteCallRequest;
import com.sjm.pcr.common.rpc.SerializableRemoteCallResponse;

public interface ClientService {
    public void register(String name);

    public List<String> listClient();

    public SerializableRemoteCallResponse remoteCall(String name, RemoteCallRequest request,
            File[] files, long timeout) throws Exception;
}
