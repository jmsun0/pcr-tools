package com.sjm.pcr.control;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Resource;
import com.sjm.core.util.Converters;
import com.sjm.pcr.client_control.cv.CvObject;
import com.sjm.pcr.client_control.cv.CvObjectManager;

@Component
public class CvRemoteFactory {

    @Resource(name = "CvObjectManagerToClient")
    private CvObjectManager cvObjectManager;

    @SuppressWarnings("unchecked")
    public <T> T getProxyObject(Class<T> clazz, int handle) {
        Map<String, MethodInfo[]> methodMap = getMethodInfoMap(clazz);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {clazz}, (proxy, method, args) -> processRemoteInvoke(proxy, method,
                        args, handle, clazz, methodMap));
    }

    private Object processRemoteInvoke(Object proxy, Method method, Object[] args, int handle,
            Class<?> clazz, Map<String, MethodInfo[]> methodMap) throws Throwable {
        String name = method.getName();
        switch (name) {
            case "getNativeObject":
                return handle;
            case "toString":
                return "Remote Proxy class[" + clazz + "] handle[" + handle + "]";
            default:
                break;
        }
        MethodInfo[] infos = methodMap.get(method.getName());
        MethodInfo info = null;
        if (infos.length == 1)
            info = infos[0];
        else {
            Class<?>[] types = method.getParameterTypes();
            for (MethodInfo methodInfo : infos) {
                if (Arrays.equals(types, methodInfo.paramTypes)) {
                    info = methodInfo;
                    break;
                }
            }
            if (info == null)
                throw new Error();
        }
        return info.returnConverter.convert(cvObjectManager.invoke(clazz, name, info.paramTypes,
                handle, convertParams(args, info.paramConverters)));
    }

    private Map<Class<?>, Map<String, MethodInfo[]>> methodInfoCache = new HashMap<>();

    private Map<String, MethodInfo[]> getMethodInfoMap(Class<?> clazz) {
        Map<String, MethodInfo[]> meta = methodInfoCache.get(clazz);
        if (meta == null)
            methodInfoCache.put(clazz, meta = getMethodInfoMapImpl(clazz));
        return meta;
    }

    private Map<String, MethodInfo[]> getMethodInfoMapImpl(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, List<MethodInfo>> methodMapTmp =
                groupby(methods, met -> true, Method::getName, met -> getMethodInfo(met));
        Map<String, MethodInfo[]> methodMap = new HashMap<>();
        for (Map.Entry<String, List<MethodInfo>> e : methodMapTmp.entrySet()) {
            List<MethodInfo> list = e.getValue();
            methodMap.put(e.getKey(), list.toArray(new MethodInfo[list.size()]));
        }
        return methodMap;
    }

    private MethodInfo getMethodInfo(Method method) {
        MethodInfo info = new MethodInfo();
        info.paramTypes = method.getParameterTypes();
        info.paramConverters = new Converter[info.paramTypes.length];
        for (int i = 0; i < info.paramConverters.length; i++) {
            Class<?> type = info.paramTypes[i];
            if (isMappingClass(type)) {
                info.paramConverters[i] = obj -> ((CvObject) obj).getNativeObject();
            } else {
                info.paramConverters[i] = obj -> Converters.convert(obj, type);
            }
        }
        Class<?> returnType = method.getReturnType();
        if (isMappingClass(returnType)) {
            info.returnConverter = obj -> getProxyObject(returnType, (int) obj);
        } else {
            info.returnConverter = obj -> Converters.convert(obj, returnType);
        }
        return info;
    }

    private boolean isMappingClass(Class<?> clazz) {
        return CvObject.class.isAssignableFrom(clazz);
    }

    static class MethodInfo {
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

    private static <T, K, V> Map<K, List<V>> groupby(T[] arr, Predicate<T> filter,
            Function<T, K> keyMapper, Function<T, V> valueMapper) {
        Map<K, List<V>> map = new HashMap<>();
        for (T value : arr) {
            if (filter.test(value)) {
                K key = keyMapper.apply(value);
                List<V> list = map.get(key);
                if (list == null)
                    map.put(key, list = new ArrayList<>());
                list.add(valueMapper.apply(value));
            }
        }
        return map;
    }
}
