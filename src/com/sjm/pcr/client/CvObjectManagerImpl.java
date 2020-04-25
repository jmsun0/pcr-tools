package com.sjm.pcr.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.ApplicationContext;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.util.Converters;
import com.sjm.pcr.client_control.cv.CvObject;
import com.sjm.pcr.client_control.cv.CvObjectManager;
import com.sjm.pcr.common.exception.ServiceException;

@Component
public class CvObjectManagerImpl implements CvObjectManager {
    static final Logger logger = LoggerFactory.getLogger(CvObjectManagerImpl.class);

    @Autowired
    private ApplicationContext applicationContext;


    private Map<Integer, Object> mappingObjects = new HashMap<>();
    private AtomicInteger handleIncrement = new AtomicInteger(0);

    @Override
    public Object invoke(Class<?> clazz, String method, Class<?>[] types, int handle,
            Object... args) throws Throwable {
        Object obj;
        if (handle == 0) {
            obj = applicationContext.getBean(clazz);
        } else {
            obj = mappingObjects.get(handle);
        }
        if (obj == null)
            throw new ServiceException(
                    "The object for class[" + clazz + "],handle[" + handle + "] not found");
        if (method.equals("close")) {
            removeHandle(handle);
        }
        MethodInfo methodInfo = getMethodInfo(clazz, method, types);
        Object result =
                methodInfo.method.invoke(obj, convertParams(args, methodInfo.paramConverters));
        return methodInfo.returnConverter.convert(result);
    }

    @Override
    public List<Integer> listHandles() {
        return new ArrayList<>(mappingObjects.keySet());
    }

    @Override
    public void removeHandle(int handle) {
        mappingObjects.remove(handle);
    }

    @Override
    public void clearHandles() {
        mappingObjects.clear();
    }

    private Map<MethodKey, MethodInfo> methodCacheMap = new HashMap<>();

    private MethodInfo getMethodInfo(Class<?> clazz, String method, Class<?>[] types) {
        MethodKey key = new MethodKey(clazz, method, types);
        MethodInfo methodInfo = methodCacheMap.get(key);
        if (methodInfo == null)
            methodCacheMap.put(key, methodInfo = getMethodInfoImpl(clazz, method, types));
        return methodInfo;
    }

    private MethodInfo getMethodInfoImpl(Class<?> clazz, String method, Class<?>[] types) {
        try {
            Method met = clazz.getMethod(method, types);
            MethodInfo info = new MethodInfo();
            info.method = met;
            info.paramTypes = met.getParameterTypes();
            info.paramConverters = new Converter[info.paramTypes.length];
            for (int i = 0; i < info.paramConverters.length; i++) {
                Class<?> type = info.paramTypes[i];
                if (isMappingClass(type)) {
                    info.paramConverters[i] = obj -> mappingObjects.get((int) obj);
                } else {
                    info.paramConverters[i] = obj -> Converters.convert(obj, type);
                }
            }
            Class<?> returnType = met.getReturnType();
            if (isMappingClass(returnType)) {
                info.returnConverter = obj -> {
                    int handle = handleIncrement.incrementAndGet();
                    mappingObjects.put(handle, obj);
                    return handle;
                };
            } else {
                info.returnConverter = obj -> Converters.convert(obj, returnType);
            }
            return info;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("The method for class[" + clazz + "],name[" + method
                    + "],type" + Arrays.toString(types) + " not found");
        }
    }

    private boolean isMappingClass(Class<?> clazz) {
        return CvObject.class.isAssignableFrom(clazz);
    }

    static class MethodKey {
        public Class<?> clazz;
        public String method;
        public Class<?>[] types;

        public MethodKey(Class<?> clazz, String method, Class<?>[] types) {
            this.clazz = clazz;
            this.method = method;
            this.types = types;
        }

        @Override
        public int hashCode() {
            return clazz.hashCode() ^ method.hashCode() ^ Arrays.hashCode(types);
        }

        @Override
        public boolean equals(Object obj) {
            MethodKey key = (MethodKey) obj;
            return clazz == key.clazz && method.equals(key.method)
                    && Arrays.equals(types, key.types);
        }
    }

    static class MethodInfo {
        public Method method;
        public Class<?>[] paramTypes;
        public Converter[] paramConverters;
        public Converter returnConverter;
    }

    protected interface Converter {
        public Object convert(Object obj) throws Throwable;
    }

    private static Object[] convertParams(Object[] args, Converter[] paramConverters)
            throws Throwable {
        if (args == null || args.length == 0)
            return args;
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = paramConverters[i].convert(args[i]);
        }
        return newArgs;
    }
}
