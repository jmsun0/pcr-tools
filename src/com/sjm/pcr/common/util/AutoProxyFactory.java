package com.sjm.pcr.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.pcr.common.exception.ServiceException;

public abstract class AutoProxyFactory {
    static final Logger logger = LoggerFactory.getLogger(AutoProxyFactory.class);

    public <T> T allocate(Class<T> clazz, Object... args) {
        try {
            ProxyClassMetaData meta = getProxyClassMetaData(clazz);
            Object nativeObject = null;
            if (meta.needInstance) {
                Constructor<?> constructor = findConstructor(meta.constructorInfos, args);
                if (constructor == null)
                    throw new ServiceException("Can not find matched constructor "
                            + meta.nativeClass + Arrays.toString(args));
                nativeObject = constructor.newInstance(args);
            }
            return nativeToProxy(nativeObject, meta);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    protected abstract Class<?> nativeClassToProxyClass(Class<?> nativeClass);

    protected abstract Class<?> proxyClassToNativeClass(Class<?> proxyClass);

    protected abstract Object getNativeObject(Object proxyObject);

    protected abstract void preRegisterInvokers(Class<?> proxyClass,
            Map<String, Invoker> invokerMap);

    protected static class ProxyClassMetaData {
        public Class<?> proxyClass;
        public Class<?> nativeClass;
        public ClassLoader classLoader;
        public Map<String, Invoker> invokerMap;
        public ConstructorInfo[] constructorInfos;
        public boolean needInstance;
    }

    static class ConstructorInfo {
        public Constructor<?> constructor;
        public Class<?>[] args;
        public Class<?>[] wapperArgs;
    }

    protected interface Invoker {
        public Object invoke(Object nativeObject, Object[] args) throws Throwable;
    }

    protected interface Converter {
        public Object convert(Object obj) throws Throwable;
    }

    @SuppressWarnings("unchecked")
    private <T> T nativeToProxy(Object nativeObject, ProxyClassMetaData meta) {
        return (T) Proxy.newProxyInstance(meta.classLoader, new Class<?>[] {meta.proxyClass},
                (proxy, method, args) -> processInvoke(proxy, method, args, nativeObject,
                        meta.invokerMap));
    }

    protected <T> T getProxyObject(Object nativeObject, Class<?> proxyClass) throws Exception {
        return nativeToProxy(nativeObject, getProxyClassMetaData(proxyClass));
    }

    private Map<Class<?>, ProxyClassMetaData> metaCache = new HashMap<>();

    private ProxyClassMetaData getProxyClassMetaData(Class<?> proxyClass) throws Exception {
        ProxyClassMetaData meta = metaCache.get(proxyClass);
        if (meta == null) {
            metaCache.put(proxyClass, meta = getProxyClassMetaDataImpl(proxyClass));
        }
        return meta;
    }

    private ProxyClassMetaData getProxyClassMetaDataImpl(Class<?> proxyClass) throws Exception {
        Class<?> nativeClass = proxyClassToNativeClass(proxyClass);
        if (nativeClass == null)
            throw new ServiceException("Can not get native class [" + proxyClass + "]");
        ProxyClassMetaData meta = new ProxyClassMetaData();
        meta.proxyClass = proxyClass;
        meta.nativeClass = nativeClass;
        meta.classLoader = Thread.currentThread().getContextClassLoader();
        meta.constructorInfos = getConstructorInfos(nativeClass);
        getInvokerMap(meta);
        return meta;
    }

    private void getInvokerMap(ProxyClassMetaData meta) throws Exception {
        Map<String, Invoker> invokerMap = new HashMap<>();
        preRegisterInvokers(meta.proxyClass, invokerMap);
        for (Method proxyMethod : meta.proxyClass.getMethods()) {
            String name = proxyMethod.getName();
            if (invokerMap.containsKey(name))
                continue;
            Class<?>[] proxyTypes = proxyMethod.getParameterTypes();
            Class<?>[] nativeTypes = new Class<?>[proxyTypes.length];
            Converter[] paramConverters = new Converter[proxyTypes.length];
            for (int i = 0; i < proxyTypes.length; i++) {
                Class<?> newType = proxyClassToNativeClass(proxyTypes[i]);
                if (newType == null) {
                    nativeTypes[i] = proxyTypes[i];
                    paramConverters[i] = obj -> obj;
                } else {
                    nativeTypes[i] = newType;
                    paramConverters[i] = obj -> getNativeObject(obj);
                }
            }
            Method nativeMethod = meta.nativeClass.getMethod(name, nativeTypes);
            Class<?> nativeReturnType = nativeMethod.getReturnType();
            Class<?> proxyReturnType = nativeClassToProxyClass(nativeReturnType);
            Converter returnConverter;
            if (proxyReturnType == null)
                returnConverter = obj -> obj;
            else {
                if (proxyReturnType != proxyMethod.getReturnType())
                    throw new ServiceException("The method return type not match ["
                            + meta.proxyClass + "] [" + nativeMethod + "]");
                returnConverter = obj -> getProxyObject(obj, proxyReturnType);
            }
            boolean isStatis = Modifier.isStatic(nativeMethod.getModifiers());
            if (!isStatis) {
                meta.needInstance = true;
                invokerMap.put(name, (nativeObject, args) -> proxyMethodInvoke(nativeObject, args,
                        nativeMethod, paramConverters, returnConverter));
            } else {
                invokerMap.put(name, (nativeObject, args) -> proxyMethodInvoke(null, args,
                        nativeMethod, paramConverters, returnConverter));
            }
        }
        meta.invokerMap = invokerMap;
    }

    private ConstructorInfo[] getConstructorInfos(Class<?> proxyClass) throws Exception {
        Constructor<?>[] constructors = proxyClass.getConstructors();
        ConstructorInfo[] infos = new ConstructorInfo[constructors.length];
        for (int i = 0; i < constructors.length; i++) {
            Constructor<?> constructor = constructors[i];
            ConstructorInfo info = infos[i] = new ConstructorInfo();
            info.constructor = constructor;
            info.args = constructor.getParameterTypes();
            info.wapperArgs = new Class<?>[info.args.length];
            for (int j = 0; j < info.wapperArgs.length; j++)
                info.wapperArgs[j] = toWrapperClass(info.args[j]);
        }
        insertSort(infos, (c1, c2) -> compareConstructorInfo(c1, c2), 0, infos.length);
        return infos;
    }

    private static final Class<?>[] primitiveClasses = {int.class, long.class, char.class,
            short.class, byte.class, boolean.class, float.class, double.class};

    private static final Class<?>[] wrapperClasses = {Integer.class, Long.class, Character.class,
            Short.class, Byte.class, Boolean.class, Float.class, Double.class};

    private static Class<?> toWrapperClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            for (int i = 0; i < primitiveClasses.length; i++) {
                if (clazz == primitiveClasses[i])
                    return wrapperClasses[i];
            }
        }
        return clazz;
    }

    private static int compareClasses(Class<?>[] t1, Class<?>[] t2) {
        int len1 = t1.length, len2 = t2.length;
        if (len1 != len2)
            return len1 - len2;
        for (int i = 0; i < len1; i++) {
            if (t2[i].isAssignableFrom(t1[i]))
                return -1;
        }
        return 1;
    }

    private static int compareConstructorInfo(ConstructorInfo c1, ConstructorInfo c2) {
        return compareClasses(c1.wapperArgs, c2.wapperArgs);
    }

    private static <T, A> void insertSort(T[] array, Comparator<? super T> cmp, int begin,
            int end) {
        for (; begin < end; begin++) {
            T value = array[begin];
            for (int j = 0; j < begin; j++) {
                if (cmp.compare(value, array[j]) < 0) {
                    System.arraycopy(array, j, array, j + 1, begin - j);
                    array[j] = value;
                    break;
                }
            }
        }
    }

    private static Constructor<?> findConstructor(ConstructorInfo[] constructorInfos,
            Object[] args) {
        for (int i = 0; i < constructorInfos.length; i++) {
            ConstructorInfo constructorInfo = constructorInfos[i];
            if (matchArgs(constructorInfo.wapperArgs, args))
                return constructorInfo.constructor;
        }
        return null;
    }

    private static boolean matchArgs(Class<?>[] type, Object[] args) {
        if (type.length != args.length)
            return false;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null && !type[i].isInstance(arg)) {
                return false;
            }
        }
        return true;
    }

    private static Object processInvoke(Object proxy, Method method, Object[] args,
            Object nativeObject, Map<String, Invoker> invokerMap) throws Throwable {
        return invokerMap.get(method.getName()).invoke(nativeObject, args);
    }

    private static Object proxyMethodInvoke(Object nativeObject, Object[] args, Method nativeMethod,
            Converter[] paramConverters, Converter returnConverter) throws Throwable {
        return returnConverter
                .convert(nativeMethod.invoke(nativeObject, convertParams(args, paramConverters)));
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
