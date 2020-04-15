package com.sjm.core.mini.springboot.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.support.AnnotationBeanDefinition;
import com.sjm.core.mini.springboot.support.AnnotationBeanRegister;
import com.sjm.core.mini.springboot.support.ClassScaner;
import com.sjm.core.mini.springboot.support.SpringException;
import com.sjm.core.util.Converters;
import com.sjm.core.util.Lists;
import com.sjm.core.util.Misc;

public class SpringApplication {
    static final Logger logger = LoggerFactory.getLogger(SpringApplication.class);

    static {
        try {
            loadLoggerProperties();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void loadLoggerProperties() {
        ResourceBundle res = ResourceBundle.getBundle("application");
        Enumeration<String> keys = res.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = System.getProperty(key);
            if (Misc.isEmpty(value))
                System.setProperty(key, res.getString(key));
        }
    }

    private Class<?> clazz;
    private String[] args;
    private Map<Object, Object> beanMap = new HashMap<>();
    private List<BeanDefinition> beanList = new ArrayList<>();
    private Map<Class<?>, List<Class<?>>> annotationClassesMap = new HashMap<>();
    private Map<Class<?>, ReflectionCacheData> reflectionCacheDataMap = new HashMap<>();

    public SpringApplication(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void run(String... args) throws Exception {
        this.args = args;
        SpringBootApplication app = clazz.getAnnotation(SpringBootApplication.class);
        if (app == null) {
            logger.error("{} is not a SpringBootApplication,stopped", clazz);
            System.exit(1);
        }
        String[] packaes = app.scanBasePackages();
        if (packaes.length == 0)
            packaes = new String[] {clazz.getPackage().getName()};
        ClassScaner.scan(packaes, true, this::scanAnnotationClasses);
        List<Class<?>> componentClassList = annotationClassesMap.get(Component.class);
        if (componentClassList.isEmpty()) {
            logger.warn("no component was found");
            System.exit(1);
        }

        for (Class<?> componentClass : componentClassList) {
            registerComponentBean(componentClass);
            if (AnnotationBeanRegister.class.isAssignableFrom(componentClass)) {
                registerAnnotationBean(componentClass);
            }
            ReflectionCacheData data = getReflectionCacheData(componentClass);
            for (int i = 0; i < data.beanMethods.size(); i++) {
                Method method = data.beanMethods.get(i);
                Bean ann = data.beanAnnotations.get(i);
                registerMethodBean(componentClass, method, ann);
            }
        }

        assignDependsOn();

        BeanDefinition appDef = new BeanDefinition();
        appDef.bean = new ApplicationContext(this);
        appDef.type = ApplicationContext.class;
        putBeanDefinition(appDef);

        for (BeanDefinition def : beanList) {
            initBean(def);
        }

        beanList = null;
        annotationClassesMap = null;
        reflectionCacheDataMap = null;
        logger.info("{} started OK", clazz.getSimpleName());
    }

    private void assignDependsOn() {
        for (BeanDefinition def : beanList) {
            if (def.dependsOn != Lists.emptyObjectArray)
                continue;
            ReflectionCacheData data = getReflectionCacheData(def.type);
            List<Field> autowiredFields = data.autowiredFields;
            List<Resource> resourceAnnotations = data.resourceAnnotations;
            DependsOn dep = def.type.getDeclaredAnnotation(DependsOn.class);
            String[] depValues = dep == null ? Lists.emptyStringArray : dep.value();
            if (autowiredFields.isEmpty() && resourceAnnotations.isEmpty()
                    && depValues.length == 0) {
                def.dependsOn = null;
                continue;
            }
            Set<Object> depClasses = new HashSet<>();
            for (Field field : autowiredFields) {
                depClasses.add(field.getType());
            }
            for (Resource res : resourceAnnotations) {
                depClasses.add(res.name());
            }
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (String depValue : depValues) {
                try {
                    Class<?> cls = Class.forName(depValue, false, loader);
                    if (beanMap.containsKey(cls))
                        depClasses.add(cls);
                } catch (ClassNotFoundException ex) {
                    depValue += ".";
                    for (BeanDefinition bd : beanList) {
                        if (bd.type.getName().startsWith(depValue)) {
                            depClasses.add(bd.type);
                        }
                    }
                }
            }
            def.dependsOn = depClasses.toArray();
        }
    }

    private void scanAnnotationClasses(Class<?> clazz) {
        Annotation[] anns = clazz.getDeclaredAnnotations();
        if (anns != null && anns.length != 0) {
            for (Annotation ann : anns) {
                Class<?> annType = ann.annotationType();
                List<Class<?>> classes = annotationClassesMap.get(annType);
                if (classes == null)
                    annotationClassesMap.put(annType, classes = new ArrayList<>());
                classes.add(clazz);
            }
        }
    }

    private void registerComponentBean(Class<?> componentClass) {
        BeanDefinition factoryDef = new BeanDefinition();
        String factoryDefName = UUID.randomUUID().toString();
        factoryDef.bean = new SimpleFactoryBean(componentClass);
        putBeanDefinition(factoryDefName, factoryDef);

        BeanDefinition def = new BeanDefinition();
        def.factoryName = factoryDefName;
        def.dependsOn = Lists.emptyObjectArray;
        def.type = componentClass;
        putBeanDefinition(def);

        Component component = componentClass.getAnnotation(Component.class);
        String name = component.value();
        if (Misc.isNotEmpty(name)) {
            putBeanDefinition(name, def);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerAnnotationBean(Class<?> componentClass) throws Exception {
        Class<? extends Annotation> annType = null;
        Type[] inters = componentClass.getGenericInterfaces();
        if (inters.length != 0 && inters[0] instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) inters[0];
            Type[] types = pt.getActualTypeArguments();
            if (types.length == 1 && types[0] instanceof Class)
                annType = (Class<? extends Annotation>) types[0];
        }
        if (annType == null)
            throw new SpringException();
        List<Class<?>> beanClasses = annotationClassesMap.get(annType);
        if (beanClasses == null)
            return;
        AnnotationBeanRegister<Annotation> register =
                (AnnotationBeanRegister<Annotation>) componentClass.newInstance();
        for (Class<?> beanClass : beanClasses) {
            Annotation ann = beanClass.getAnnotation(annType);
            AnnotationBeanDefinition annDef = register.register(ann, beanClass);

            BeanDefinition factoryFactoryDef = new BeanDefinition();
            factoryFactoryDef.bean = new AnnotationFactoryBean(componentClass, annDef);
            factoryFactoryDef.type = AnnotationFactoryBean.class;
            putBeanDefinition(factoryFactoryDef);

            String factoryFactoryDefName = UUID.randomUUID().toString();
            putBeanDefinition(factoryFactoryDefName, factoryFactoryDef);

            BeanDefinition factoryDef = new BeanDefinition();
            factoryDef.factoryName = factoryFactoryDefName;
            factoryDef.dependsOn = Lists.emptyObjectArray;
            factoryDef.type = annDef.factoryClass;
            putBeanDefinition(factoryDef);

            String factoryDefName = UUID.randomUUID().toString();
            putBeanDefinition(factoryDefName, factoryDef);

            BeanDefinition def = new BeanDefinition();
            def.factoryName = factoryDefName;
            def.type = beanClass;
            putBeanDefinition(def);
        }
    }

    private void registerMethodBean(Class<?> componentClass, Method method, Bean ann) {
        BeanDefinition factoryDef = new BeanDefinition();
        String factoryDefName = UUID.randomUUID().toString();
        factoryDef.bean = new MethodFactoryBean(this, componentClass, method);
        factoryDef.dependsOn = new Object[] {componentClass};
        factoryDef.type = MethodFactoryBean.class;
        putBeanDefinition(factoryDefName, factoryDef);

        BeanDefinition def = new BeanDefinition();
        def.factoryName = factoryDefName;
        def.type = method.getReturnType();
        putBeanDefinition(def);

        String name = ann.value();
        if (Misc.isNotEmpty(name)) {
            putBeanDefinition(name, def);
        }
    }

    private void initBean(BeanDefinition def) throws Exception {
        if (def.dependsOn != null) {
            for (Object dep : def.dependsOn) {
                initBean(getBeanDefinition(dep));
            }
        }
        if (def.bean == null) {
            BeanDefinition factoryDef = getBeanDefinition(def.factoryName);
            initBean(factoryDef);
            FactoryBean<?> factory = (FactoryBean<?>) factoryDef.bean;
            def.bean = factory.getObject();

            ReflectionCacheData data = getReflectionCacheData(def.type);
            for (Field field : data.autowiredFields) {
                field.setAccessible(true);
                field.set(def.bean, getBean(field.getType()));
            }
            for (int i = 0; i < data.resourceFields.size(); i++) {
                Field field = data.resourceFields.get(i);
                Resource res = data.resourceAnnotations.get(i);
                field.setAccessible(true);
                field.set(def.bean, getBean(res.name()));
            }
            for (int i = 0; i < data.valueFields.size(); i++) {
                Field field = data.valueFields.get(i);
                Value val = data.valueAnnotations.get(i);
                field.setAccessible(true);
                String exp = val.value();
                int start = exp.indexOf('{');
                int end = exp.lastIndexOf('}');
                if (start == -1 || end <= start)
                    throw new SpringException();
                int colon = exp.indexOf(':');
                String value;
                if (colon == -1)
                    value = System.getProperty(exp.substring(start + 1, end));
                else
                    value = System.getProperty(exp.substring(start + 1, colon),
                            exp.substring(colon + 1, end));
                field.set(def.bean, Converters.convert(value, field.getType()));
            }
            for (Method method : data.postConstructMethods) {
                method.setAccessible(true);
                method.invoke(def.bean);
            }
            if (def.bean instanceof CommandLineRunner) {
                ((CommandLineRunner) def.bean).run(args);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private BeanDefinition getBeanDefinition(Object nameOrClass) {
        Object defOrList = beanMap.get(nameOrClass);
        if (defOrList == null)
            throw new SpringException("BeanDefinition [" + nameOrClass + "] not found");
        if (defOrList instanceof List)
            return ((List<BeanDefinition>) defOrList).get(0);
        else
            return (BeanDefinition) defOrList;
    }

    public Object getBean(Object nameOrClass) {
        Object bean = getBeanDefinition(nameOrClass).bean;
        if (bean == null)
            throw new SpringException("bean [" + nameOrClass + "] not found");
        return bean;
    }

    private void putBeanDefinition(BeanDefinition def) {
        Class<?> clazz = def.type;
        Set<Class<?>> superClasses = new HashSet<>();
        do {
            superClasses.add(clazz);
            Class<?>[] inters = clazz.getInterfaces();
            for (Class<?> inter : inters) {
                superClasses.add(inter);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);
        beanList.add(def);
        for (Class<?> superClass : superClasses) {
            Object defOrList = beanMap.get(superClass);
            if (defOrList == null)
                beanMap.put(superClass, def);
            else if (defOrList instanceof List) {
                @SuppressWarnings("unchecked")
                List<BeanDefinition> list = (List<BeanDefinition>) defOrList;
                list.add(def);
            } else {
                List<BeanDefinition> list = new ArrayList<>();
                list.add((BeanDefinition) defOrList);
                list.add(def);
                beanMap.put(superClass, list);
            }
        }
    }

    private void putBeanDefinition(String name, BeanDefinition def) {
        Object old = beanMap.put(name, def);
        if (old != null)
            throw new SpringException("duplicate bean name '" + name + "'");
    }

    static class ReflectionCacheData {
        public List<Field> valueFields = new ArrayList<>();
        public List<Value> valueAnnotations = new ArrayList<>();
        public List<Field> autowiredFields = new ArrayList<>();
        public List<Autowired> autowiredAnnotations = new ArrayList<>();
        public List<Field> resourceFields = new ArrayList<>();
        public List<Resource> resourceAnnotations = new ArrayList<>();
        public List<Method> beanMethods = new ArrayList<>();
        public List<Bean> beanAnnotations = new ArrayList<>();
        public List<Method> postConstructMethods = new ArrayList<>();
    }

    private ReflectionCacheData getReflectionCacheData(Class<?> clazz) {
        ReflectionCacheData data = reflectionCacheDataMap.get(clazz);
        if (data == null) {
            reflectionCacheDataMap.put(clazz, data = new ReflectionCacheData());
            Class<?> classTmp = clazz;
            do {
                Field[] fields = classTmp.getDeclaredFields();
                for (Field field : fields) {
                    Annotation[] anns = field.getDeclaredAnnotations();
                    for (Annotation ann : anns) {
                        Class<? extends Annotation> annType = ann.annotationType();
                        if (annType == Value.class) {
                            data.valueFields.add(field);
                            data.valueAnnotations.add((Value) ann);
                        } else if (annType == Autowired.class) {
                            data.autowiredFields.add(field);
                            data.autowiredAnnotations.add((Autowired) ann);
                        } else if (annType == Resource.class) {
                            data.resourceFields.add(field);
                            data.resourceAnnotations.add((Resource) ann);
                        }
                    }
                }
                Method[] methods = classTmp.getDeclaredMethods();
                for (Method method : methods) {
                    Annotation[] anns = method.getDeclaredAnnotations();
                    for (Annotation ann : anns) {
                        Class<? extends Annotation> annType = ann.annotationType();
                        if (classTmp == clazz && annType == Bean.class) {
                            data.beanMethods.add(method);
                            data.beanAnnotations.add((Bean) ann);
                        } else if (annType == PostConstruct.class) {
                            data.postConstructMethods.add(method);
                        }
                    }
                }
                classTmp = classTmp.getSuperclass();
            } while (classTmp != null && classTmp != Object.class);
        }
        return data;
    }

    static abstract class AbstractFactoryBean<T> implements FactoryBean<T> {
        protected Class<?> clazz;

        public AbstractFactoryBean(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class<?> getObjectType() {
            return clazz;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

    }
    static class SimpleFactoryBean extends AbstractFactoryBean<Object> {
        public SimpleFactoryBean(Class<?> clazz) {
            super(clazz);
        }

        @Override
        public Object getObject() throws Exception {
            return clazz.newInstance();
        }
    }

    static class MethodFactoryBean extends AbstractFactoryBean<Object> {
        private SpringApplication app;
        private Class<?> beanClass;
        private Method method;

        public MethodFactoryBean(SpringApplication app, Class<?> beanClass, Method method) {
            super(method.getReturnType());
            this.app = app;
            this.beanClass = beanClass;
            this.method = method;
        }

        @Override
        public Object getObject() throws Exception {
            return method.invoke(app.getBean(beanClass));
        }
    }

    static class AnnotationFactoryBean extends AbstractFactoryBean<FactoryBean<?>> {
        private AnnotationBeanDefinition annDef;

        public AnnotationFactoryBean(Class<?> clazz, AnnotationBeanDefinition annDef) {
            super(clazz);
            this.annDef = annDef;
        }

        @Override
        public FactoryBean<?> getObject() throws Exception {
            return (FactoryBean<?>) annDef.factoryClass.getConstructors()[0]
                    .newInstance(annDef.constructorArgs);
        }
    }

    static class BeanDefinition {
        public Object bean;
        public Class<?> type;
        public Object[] dependsOn;
        public String factoryName;
    }
}
