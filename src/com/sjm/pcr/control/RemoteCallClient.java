package com.sjm.pcr.control;

import java.io.File;
import java.util.List;

import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Lazy;
import com.sjm.pcr.common_component.rpc.RemoteCall;
import com.sjm.pcr.common_component.rpc.RemoteCallRequest;
import com.sjm.pcr.common_component.rpc.RemoteCallResponse;
import com.sjm.pcr.common_component.rpc.RemoteCallSocketProcessor;
import com.sjm.pcr.common_component.rpc.RemoteContext;
import com.sjm.pcr.common_component.rpc.SerializableRemoteCallResponse;
import com.sjm.pcr.common_component.service.ClientService;

@Component
public class RemoteCallClient implements RemoteCall {

    @Lazy
    @Autowired
    private ClientService clientService;

    @Autowired
    private RemoteCallSocketProcessor remoteCallSocketProcessor;

    @Override
    public Object call(String className, String beanName, String method, Class<?>[] types,
            Object... args) throws Throwable {
        RemoteCallRequest req = new RemoteCallRequest(className, beanName, method, types, args);
        List<File> files = remoteCallSocketProcessor.getFiles(req);
        SerializableRemoteCallResponse sres =
                clientService.remoteCall(RemoteContext.get().getRemoteName(), req,
                        files.toArray(new File[files.size()]), RemoteContext.get().getTimeout());
        RemoteCallResponse res = remoteCallSocketProcessor.deserializeResponse(sres);
        if (res.getError() != null)
            throw res.getError();
        return res.getReturnValue();
    }
}
