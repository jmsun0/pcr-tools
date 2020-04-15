package com.sjm.pcr.common.rpc;

public interface RemoteCall {
    public Object call(String className, String beanName, String method, Class<?>[] types,
            Object... args) throws Throwable;
}
