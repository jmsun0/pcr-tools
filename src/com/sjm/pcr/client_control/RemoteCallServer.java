package com.sjm.pcr.client_control;

import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.common_component.rpc.RemoteCall;
import com.sjm.pcr.common_component.rpc.RemoteCallRequest;
import com.sjm.pcr.common_component.rpc.RemoteCallResponse;
import com.sjm.pcr.common_component.rpc.RemoteCallSocketProcessor;
import com.sjm.pcr.common_component.rpc.RemoteContext;

@Component
public class RemoteCallServer implements RemoteCall {

    @Autowired
    private BaseClientHandler handler;

    @Autowired
    private RemoteCallSocketProcessor remoteCallSocketProcessor;

    @Override
    public Object call(String className, String beanName, String method, Class<?>[] types,
            Object... args) throws Throwable {
        RemoteCallRequest req = new RemoteCallRequest(className, beanName, method, types, args);
        RemoteCallResponse res =
                remoteCallSocketProcessor.remoteCallSync(handler.getChannelContext(), req,
                        remoteCallSocketProcessor.getFiles(req), RemoteContext.get().getTimeout());
        if (res.getError() != null)
            throw res.getError();
        return res.getReturnValue();
    }
}
