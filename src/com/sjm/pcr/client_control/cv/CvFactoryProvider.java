package com.sjm.pcr.client_control.cv;

import java.util.Map;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.exception.ServiceException;
import com.sjm.pcr.common.util.ProxyFactoryProvider;


public class CvFactoryProvider extends ProxyFactoryProvider {
    static final Logger logger = LoggerFactory.getLogger(CvFactoryProvider.class);

    {
        try {
            registNativeToProxys();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
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

    @Override
    protected Object getNativeObject(Object proxyObject) {
        return ((CvObject) proxyObject).getNativeObject();
    }

    @Override
    protected void preRegisterInvokers(Class<?> proxyClass, Map<String, Invoker> invokerMap) {
        invokerMap.put("toString", (method, obj, args) -> "Proxy[" + obj + "]");
        invokerMap.put("getNativeObject", (method, obj, args) -> obj);
        invokerMap.put("close", (method, obj, args) -> {
            Misc.close((AutoCloseable) obj);
            return null;
        });
    }
}
