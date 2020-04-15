package com.sjm.pcr.common.rpc.impl;

import com.sjm.core.mini.springboot.api.Component;
import com.sjm.pcr.common.rpc.RemoteCall;

@Component
public class RemoteCallServer implements RemoteCall {

    @Override
    public Object call(String className, String beanName, String method, Class<?>[] types,
            Object... args) throws Throwable {
        return null;
    }

}
