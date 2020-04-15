package com.sjm.common.mini.springboot.support;

import java.io.File;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sjm.common.core.Platform;
import com.sjm.common.logger.Logger;
import com.sjm.common.logger.LoggerFactory;

public class ClassScaner {
    public static void main(String[] args) {
        ClassScaner.scan("com.sjm.common.json", true, clazz -> System.out.println(clazz));
    }

    static final Logger logger = LoggerFactory.getLogger(ClassScaner.class);

    public static void scan(String pkg, boolean recursive, Consumer<Class<?>> consumer) {
        try {
            if (Platform.isAndroid()) {
                scanAndroid(pkg, recursive, consumer);
            } else {
                scanPC(pkg, recursive, consumer);
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    private static void scanPC(String pkg, boolean recursive, Consumer<Class<?>> consumer)
            throws Exception {
        ClassLoader classLoader = ClassScaner.class.getClassLoader();
        Enumeration<URL> res = classLoader.getResources(pkg.replace('.', '/'));
        while (res.hasMoreElements()) {
            URL url = res.nextElement();
            try {
                switch (url.getProtocol()) {
                    case "jar":
                        scanJar(url, recursive, consumer, classLoader);
                        break;
                    case "file":
                        scanFile(new File(url.getPath()), recursive, consumer, pkg, classLoader);
                        break;
                    default:
                        throw new IllegalArgumentException(url.toString());
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }


    private static void scanAndroid(String pkg, boolean recursive, Consumer<Class<?>> consumer)
            throws Exception {
        pkg = pkg + ".";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Object pathList = getField(Class.forName("dalvik.system.BaseDexClassLoader"), classLoader,
                "pathList");
        Object[] dexElements = (Object[]) getField(pathList.getClass(), pathList, "dexElements");
        for (Object dexElement : dexElements) {
            try {
                Object dexFile = getField(dexElement.getClass(), dexElement, "dexFile");
                if (dexFile == null)
                    continue;
                @SuppressWarnings("unchecked")
                Enumeration<String> entries = (Enumeration<String>) dexFile.getClass()
                        .getDeclaredMethod("entries").invoke(dexFile);
                while (entries.hasMoreElements()) {
                    String entryName = entries.nextElement();
                    if (entryName.startsWith(pkg)
                            && (recursive || entryName.indexOf('.', pkg.length()) == -1)) {
                        try {
                            Class<?> clazz = Class.forName(entryName, false, classLoader);
                            consumer.accept(clazz);
                        } catch (Exception e) {
                            logger.debug(e.getMessage(), e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    private static void scanJar(URL url, boolean recursive, Consumer<Class<?>> consumer,
            ClassLoader classLoader) throws Exception {
        JarURLConnection con = (JarURLConnection) url.openConnection();
        String pkgPath = con.getEntryName();
        JarFile jarFile = con.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jar = jarEntries.nextElement();
            try {
                String entryName = jar.getName();
                if (entryName.startsWith(pkgPath) && entryName.endsWith(".class")
                        && (recursive || entryName.indexOf('/', pkgPath.length()) == -1)) {
                    int dotIndex = entryName.lastIndexOf('.');
                    String className = entryName.substring(0, dotIndex).replace("/", ".");
                    Class<?> clazz = Class.forName(className, false, classLoader);
                    consumer.accept(clazz);
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    private static void scanFile(File file, boolean recursive, Consumer<Class<?>> consumer,
            String packageOrClass, ClassLoader classLoader) {
        if (file.isFile()) {
            try {
                int dotIndex = packageOrClass.lastIndexOf('.');
                String className = packageOrClass.substring(0, dotIndex);
                Class<?> clazz = Class.forName(className, false, classLoader);
                consumer.accept(clazz);
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        } else if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (recursive || f.isFile()) {
                    scanFile(f, recursive, consumer, packageOrClass + "." + f.getName(),
                            classLoader);
                }
            }
        }
    }

    private static Object getField(Class<?> clazz, Object obj, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
