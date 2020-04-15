package com.sjm.pcr.base.rpc;

import com.sjm.common.core.Converters;
import com.sjm.common.core.Reflection;
import com.sjm.common.mini.springboot.api.ApplicationContext;
import com.sjm.common.mini.springboot.api.Autowired;
import com.sjm.common.mini.springboot.api.Component;

@Component
public class RemoteCallHandler {

    @Autowired
    private ApplicationContext applicationContext;

    public RemoteCallResult hanleRemoteCall(String className, String beanName, String method,
            Class<?>[] types, Object... args) {
        try {
            Object obj;
            Reflection.IClass clazz;
            if (className == null) {// 通过名称获取对象
                obj = applicationContext.getBean(beanName);
                clazz = Reflection.forClass(obj.getClass());
            } else {// 通过类型获取对象
                clazz = Reflection.forName(className);
                obj = applicationContext.getBean(clazz.getClazz());
            }
            // 取得缓存的method对象
            Reflection.IMethod met = clazz.getSupersMethodMap().get(method);
            Object returnValue;
            if (types != null) {
                // 序列化JSON字符串时类型会丢失，所以根据传入的参数类型恢复为原本的参数
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg != null)
                        args[i] = Converters.convert(arg, types[i]);
                }
                // 执行调用
                returnValue = met.invoke(obj, args);

            } else {
                returnValue = Reflection.Util.invokeDynamic(obj, method, args);
            }
            return new RemoteCallResult(returnValue, null);
        } catch (Throwable e) {
            return new RemoteCallResult(null, e.getCause() != null ? e.getCause() : e);
        }
    }

    public RemoteCallResult hanleRemoteCall(RemoteCallRequest request) {
        return hanleRemoteCall(request.getClassName(), request.getBeanName(), request.getMethod(),
                request.getTypes(), request.getArgs());
    }
}
