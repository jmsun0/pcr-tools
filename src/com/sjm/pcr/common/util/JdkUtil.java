package com.sjm.pcr.common.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class JdkUtil {

    public static void loadJarByURL(URL jarURL) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(Thread.currentThread().getContextClassLoader(), jarURL);
    }

    public static void loadJarByFile(String file) throws Exception {
        loadJarByURL(new URL("file://" + file));
    }

    public static File getJavaHome() {
        File javaHome = new File(System.getProperty("java.home"));
        if (javaHome.getName().equals("jre"))
            javaHome = javaHome.getParentFile();
        return javaHome;
    }
}
