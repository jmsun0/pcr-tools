package com.sjm.pcr.client;

import java.util.HashMap;
import java.util.Map;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.exception.ServiceException;
import com.sjm.pcr.common.util.AutoProxyFactory;
import com.sjm.pcr.common_component.cv.BytePointer;
import com.sjm.pcr.common_component.cv.CvFactory;
import com.sjm.pcr.common_component.cv.CvObject;
import com.sjm.pcr.common_component.cv.DoublePointer;
import com.sjm.pcr.common_component.cv.Mat;
import com.sjm.pcr.common_component.cv.Point;
import com.sjm.pcr.common_component.cv.Scalar;
import com.sjm.pcr.common_component.cv.Size;
import com.sjm.pcr.common_component.cv.TessBaseAPI;
import com.sjm.pcr.common_component.cv.opencv_core;
import com.sjm.pcr.common_component.cv.opencv_highgui;
import com.sjm.pcr.common_component.cv.opencv_imgcodecs;
import com.sjm.pcr.common_component.cv.opencv_imgproc;

@Component
public class CvProxyFactory extends AutoProxyFactory implements CvFactory {
    static final Logger logger = LoggerFactory.getLogger(CvProxyFactory.class);

    Map<Class<?>, Class<?>> nativeToProxyMap = new HashMap<>();
    Map<Class<?>, Class<?>> proxyToNativeMap = new HashMap<>();
    {
        try {
            registNativeToProxys();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        for (Map.Entry<Class<?>, Class<?>> e : nativeToProxyMap.entrySet()) {
            proxyToNativeMap.put(e.getValue(), e.getKey());
        }
    }

    private void registNativeToProxys() throws Exception {
        registNativeToProxy("org.bytedeco.opencv.global.opencv_core", opencv_core.class);
        registNativeToProxy("org.bytedeco.opencv.global.opencv_imgcodecs", opencv_imgcodecs.class);
        registNativeToProxy("org.bytedeco.opencv.global.opencv_imgproc", opencv_imgproc.class);
        registNativeToProxy("org.bytedeco.opencv.global.opencv_highgui", opencv_highgui.class);

        registNativeToProxy("org.bytedeco.javacpp.BytePointer", BytePointer.class);
        registNativeToProxy("org.bytedeco.javacpp.DoublePointer", DoublePointer.class);
        registNativeToProxy("org.bytedeco.opencv.opencv_core.Mat", Mat.class);
        registNativeToProxy("org.bytedeco.opencv.opencv_core.Point", Point.class);
        registNativeToProxy("org.bytedeco.opencv.opencv_core.Scalar", Scalar.class);
        registNativeToProxy("org.bytedeco.opencv.opencv_core.Size", Size.class);
        registNativeToProxy("org.bytedeco.tesseract.TessBaseAPI", TessBaseAPI.class);
    }

    private void registNativeToProxy(String nativeClassName, Class<?> proxyClass) throws Exception {
        nativeToProxyMap.put(Class.forName(nativeClassName, false,
                Thread.currentThread().getContextClassLoader()), proxyClass);
    }

    @Override
    protected Class<?> nativeClassToProxyClass(Class<?> nativeClass) {
        return nativeToProxyMap.get(nativeClass);
    }

    @Override
    protected Class<?> proxyClassToNativeClass(Class<?> proxyClass) {
        return proxyToNativeMap.get(proxyClass);
    }

    @Override
    protected Object getNativeObject(Object proxyObject) {
        return ((CvObject) proxyObject).getNativeObject();
    }

    @Override
    protected <T> T getProxyObject(Object nativeObject, Class<?> proxyClass) throws Exception {
        return super.getProxyObject(nativeObject, proxyClass);
    }

    @Override
    protected void preRegisterInvokers(Class<?> proxyClass, Map<String, Invoker> invokerMap) {
        invokerMap.put("toString", (nativeObject, args) -> "Proxy[" + nativeObject + "]");
        invokerMap.put("getNativeObject", (nativeObject, args) -> nativeObject);
        invokerMap.put("close", (nativeObject, args) -> {
            Misc.close((AutoCloseable) nativeObject);
            return null;
        });
    }
}
