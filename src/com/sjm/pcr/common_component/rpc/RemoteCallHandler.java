package com.sjm.pcr.common_component.rpc;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.ApplicationContext;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.util.Converters;
import com.sjm.core.util.Lists;
import com.sjm.core.util.Reflection;

@Component
public class RemoteCallHandler {
    static final Logger logger = LoggerFactory.getLogger(RemoteCallHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    public RemoteCallResponse hanleRemoteCall(String className, String beanName, String method,
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
            if (args == null)
                args = Lists.emptyObjectArray;
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
            return new RemoteCallResponse(returnValue, null);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return new RemoteCallResponse(null, e.getCause() != null ? e.getCause() : e);
        }
    }

    public RemoteCallResponse hanleRemoteCall(RemoteCallRequest request) {
        return hanleRemoteCall(request.getClassName(), request.getBeanName(), request.getMethod(),
                request.getTypes(), request.getArgs());
    }
}
