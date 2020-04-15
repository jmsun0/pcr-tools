package com.sjm.pcr.base.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.sjm.common.core.Converters;
import com.sjm.common.logger.Logger;
import com.sjm.common.logger.LoggerFactory;


public class RemoteCallFactory {

    static final Logger logger = LoggerFactory.getLogger(RemoteCallFactory.class);

    @SuppressWarnings("unchecked")
    public static <T> T forJava(RemoteCall remoteCall, Class<T> clazz, String remoteClassName,
            String remoteBeanName) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] {clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        if (method.getName().equals("toString"))
                            return "Remote Proxy[" + clazz + "]";
                        Object returnValue = remoteCall.call(remoteClassName, remoteBeanName,
                                method.getName(), method.getParameterTypes(), args);
                        return Converters.convert(returnValue, method.getReturnType());
                    }
                });
    }

    public static <T> T forJava(RemoteCall remoteCall, Class<T> clazz) {
        return forJava(remoteCall, clazz, clazz.getName(), null);
    }
}
