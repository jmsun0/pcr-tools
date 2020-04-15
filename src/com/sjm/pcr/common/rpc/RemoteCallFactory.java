package com.sjm.pcr.common.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.util.Converters;


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
