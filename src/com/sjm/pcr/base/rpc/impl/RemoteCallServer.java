package com.sjm.pcr.base.rpc.impl;

import com.sjm.common.mini.springboot.api.Component;
import com.sjm.pcr.base.rpc.RemoteCall;

@Component
public class RemoteCallServer implements RemoteCall {

    @Override
    public Object call(String className, String beanName, String method, Class<?>[] types,
            Object... args) throws Throwable {
        return null;
    }

}
