package com.sjm.pcr.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.pcr.common.exception.ServiceException;

public abstract class ProxyFactoryProvider {
    static final Logger logger = LoggerFactory.getLogger(ProxyFactoryProvider.class);

    @SuppressWarnings("unchecked")
    public <T> T getFactory(Class<T> clazz) {
        Method[] methods = clazz.getMethods();
        Map<Class<?>, List<ConstructorInfo>> methodMap = groupby(methods, met -> true,
                Method::getReturnType, met -> getConstructorInfo(met));
        Map<Class<?>, Invoker> invokerMap = new HashMap<>();
        for (Map.Entry<Class<?>, List<ConstructorInfo>> e : methodMap.entrySet()) {
            Class<?> proxyClass = e.getKey();
            List<ConstructorInfo> constructorInfos = e.getValue();
            if (constructorInfos.size() == 1) {
                ConstructorInfo info = constructorInfos.get(0);
                invokerMap.put(proxyClass, (method, obj,
                        args) -> proxySingleConstructorInvoke(method, obj, args, info));
            } else {
                ConstructorInfo[] infos =
                        constructorInfos.toArray(new ConstructorInfo[constructorInfos.size()]);
                invokerMap.put(proxyClass, (method, obj,
                        args) -> proxyMultiConstructorInvoke(method, obj, args, infos));
            }
        }
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {clazz},
                (proxy, method, args) -> processConstructorInvoke(proxy, method, args, invokerMap));
    }

    protected abstract Object getNativeObject(Object proxyObject);

    protected abstract void preRegisterInvokers(Class<?> proxyClass,
            Map<String, Invoker> invokerMap);

    protected static class ProxyClassMetaData {
        public Class<?> proxyClass;
        public Class<?> nativeClass;
        public Map<String, Invoker> invokerMap;
        public boolean needInstance;
    }

    static class MethodOrConstructorInfo {
        public Class<?>[] proxyParamTypes;
        public Class<?>[] nativeParamTypes;
        public Converter[] paramConverters;
    }

    static class MethodInfo extends MethodOrConstructorInfo {
        public Method nativeMethod;
        public Converter returnConverter;
        public boolean isStatic;
    }

    static class ConstructorInfo extends MethodOrConstructorInfo {
        public Constructor<?> nativeConstructor;
        public ProxyClassMetaData targetMeta;
    }

    protected interface Invoker {
        public Object invoke(Method method, Object obj, Object[] args) throws Throwable;
    }

    protected interface Converter {
        public Object convert(Object obj) throws Throwable;
    }

    private Map<Class<?>, Class<?>> nativeToProxyMap = new HashMap<>();
    private Map<Class<?>, Class<?>> proxyToNativeMap = new HashMap<>();

    public void registNativeToProxy(String nativeClassName, Class<?> proxyClass) throws Exception {
        Class<?> nativeClass = Class.forName(nativeClassName, false,
                Thread.currentThread().getContextClassLoader());
        nativeToProxyMap.put(nativeClass, proxyClass);
        proxyToNativeMap.put(proxyClass, nativeClass);
    }

    private Class<?> nativeClassToProxyClass(Class<?> nativeClass) {
        return nativeToProxyMap.get(nativeClass);
    }

    private Class<?> proxyClassToNativeClass(Class<?> proxyClass) {
        return proxyToNativeMap.get(proxyClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T nativeToProxy(Object nativeObject, ProxyClassMetaData meta) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {meta.proxyClass},
                (proxy, method, args) -> processMethodInvoke(proxy, method, args, nativeObject,
                        meta.invokerMap));
    }

    protected <T> T getProxyObject(Object nativeObject, Class<?> proxyClass) throws Exception {
        return nativeToProxy(nativeObject, getProxyClassMetaData(proxyClass));
    }

    private Map<Class<?>, ProxyClassMetaData> metaCache = new HashMap<>();

    private ProxyClassMetaData getProxyClassMetaData(Class<?> proxyClass) {
        ProxyClassMetaData meta = metaCache.get(proxyClass);
        if (meta == null)
            metaCache.put(proxyClass, meta = getProxyClassMetaDataImpl(proxyClass));
        return meta;
    }

    private ProxyClassMetaData getProxyClassMetaDataImpl(Class<?> proxyClass) {
        Class<?> nativeClass = proxyClassToNativeClass(proxyClass);
        if (nativeClass == null)
            throw new ServiceException("Can not get native class [" + proxyClass + "]");
        ProxyClassMetaData meta = new ProxyClassMetaData();
        meta.proxyClass = proxyClass;
        meta.nativeClass = nativeClass;
        meta.invokerMap = new HashMap<>();
        preRegisterInvokers(proxyClass, meta.invokerMap);
        Method[] methods = proxyClass.getMethods();
        Map<String, List<MethodInfo>> methodMap =
                groupby(methods, met -> !meta.invokerMap.containsKey(met.getName()),
                        Method::getName, met -> getMethodInfo(met, nativeClass));
        for (Map.Entry<String, List<MethodInfo>> e : methodMap.entrySet()) {
            String name = e.getKey();
            if (meta.invokerMap.containsKey(name))
                continue;
            List<MethodInfo> methodInfos = e.getValue();
            if (methodInfos.size() == 1) {
                MethodInfo info = methodInfos.get(0);
                meta.invokerMap.put(name,
                        (method, obj, args) -> proxySingleMethodInvoke(method, obj, args, info));
            } else {
                MethodInfo[] infos = methodInfos.toArray(new MethodInfo[methodInfos.size()]);
                meta.invokerMap.put(name,
                        (method, obj, args) -> proxyMultiMethodInvoke(method, obj, args, infos));
            }
            for (MethodInfo methodInfo : methodInfos) {
                if (!methodInfo.isStatic) {
                    meta.needInstance = true;
                    break;
                }
            }
        }
        return meta;
    }

    private void getMethodOrConstructorInfo(MethodOrConstructorInfo info) {
        boolean needConvert = false;
        info.paramConverters = new Converter[info.proxyParamTypes.length];
        info.nativeParamTypes = new Class<?>[info.proxyParamTypes.length];
        for (int i = 0; i < info.paramConverters.length; i++) {
            Class<?> type = info.proxyParamTypes[i];
            Class<?> newType = proxyClassToNativeClass(type);
            if (newType != null) {
                needConvert = true;
                info.paramConverters[i] = obj -> getNativeObject(obj);
                info.nativeParamTypes[i] = newType;
            } else {
                info.paramConverters[i] = obj -> obj;
                info.nativeParamTypes[i] = type;
            }
        }
        if (!needConvert)
            info.paramConverters = null;
    }

    private MethodInfo getMethodInfo(Method proxyMethod, Class<?> nativeClass) {
        MethodInfo info = new MethodInfo();
        info.proxyParamTypes = proxyMethod.getParameterTypes();
        getMethodOrConstructorInfo(info);
        try {
            info.nativeMethod = nativeClass.getMethod(proxyMethod.getName(), info.nativeParamTypes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(
                    "The native method for proxy[" + proxyMethod + "] not found");
        }
        Class<?> nativeReturnType = info.nativeMethod.getReturnType();
        Class<?> proxyReturnType = nativeClassToProxyClass(nativeReturnType);
        if (proxyReturnType == null)
            info.returnConverter = obj -> obj;
        else {
            if (proxyReturnType != proxyMethod.getReturnType())
                throw new ServiceException("The method return type not match [" + proxyMethod
                        + "] [" + info.nativeMethod + "]");
            info.returnConverter = obj -> getProxyObject(obj, proxyReturnType);
        }
        info.isStatic = Modifier.isStatic(info.nativeMethod.getModifiers());
        return info;
    }

    private ConstructorInfo getConstructorInfo(Method proxyMethod) {
        Class<?> proxyClass = proxyMethod.getReturnType();
        Class<?> nativeClass = proxyClassToNativeClass(proxyClass);
        if (nativeClass == null)
            throw new ServiceException(
                    "Proxy class [" + proxyClass + "] does not have native class");
        ConstructorInfo info = new ConstructorInfo();
        info.targetMeta = getProxyClassMetaData(proxyClass);
        info.proxyParamTypes = proxyMethod.getParameterTypes();
        if (info.targetMeta.needInstance) {
            getMethodOrConstructorInfo(info);
            try {
                info.nativeConstructor = nativeClass.getConstructor(info.nativeParamTypes);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ServiceException(
                        "The native constructor for proxy[" + proxyMethod + "] not found");
            }
        }
        return info;
    }

    private static Object processMethodInvoke(Object proxy, Method method, Object[] args,
            Object obj, Map<String, Invoker> invokerMap) throws Throwable {
        return invokerMap.get(method.getName()).invoke(method, obj, args);
    }

    private static Object processConstructorInvoke(Object proxy, Method method, Object[] args,
            Map<Class<?>, Invoker> invokerMap) throws Throwable {
        return invokerMap.get(method.getReturnType()).invoke(method, null, args);
    }

    private static Object proxySingleMethodInvoke(Method method, Object obj, Object[] args,
            MethodInfo info) throws Throwable {
        return info.returnConverter.convert(info.nativeMethod.invoke(info.isStatic ? null : obj,
                convertParams(args, info.paramConverters)));
    }

    private static Object proxyMultiMethodInvoke(Method method, Object obj, Object[] args,
            MethodInfo[] infos) throws Throwable {
        Class<?>[] types = method.getParameterTypes();
        for (MethodInfo info : infos) {
            if (Arrays.equals(types, info.proxyParamTypes)) {
                return proxySingleMethodInvoke(method, obj, args, info);
            }
        }
        throw new Error();
    }

    private Object proxySingleConstructorInvoke(Method method, Object obj, Object[] args,
            ConstructorInfo info) throws Throwable {
        Object nativeObject = info.targetMeta.needInstance
                ? info.nativeConstructor.newInstance(convertParams(args, info.paramConverters))
                : null;
        return nativeToProxy(nativeObject, info.targetMeta);
    }

    private Object proxyMultiConstructorInvoke(Method method, Object obj, Object[] args,
            ConstructorInfo[] infos) throws Throwable {
        Class<?>[] types = method.getParameterTypes();
        for (ConstructorInfo info : infos) {
            if (Arrays.equals(types, info.proxyParamTypes)) {
                return proxySingleConstructorInvoke(method, obj, args, info);
            }
        }
        throw new Error();
    }

    private static Object[] convertParams(Object[] args, Converter[] paramConverters)
            throws Throwable {
        if (args == null || args.length == 0 || paramConverters == null)
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
