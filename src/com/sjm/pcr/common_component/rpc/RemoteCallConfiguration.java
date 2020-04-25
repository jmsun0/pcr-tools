package com.sjm.pcr.common_component.rpc;


import com.sjm.core.mini.springboot.api.ApplicationContext;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.FactoryBean;
import com.sjm.core.mini.springboot.ext.AnnotationBeanDefinition;
import com.sjm.core.mini.springboot.ext.AnnotationBeanRegister;

@Component
public class RemoteCallConfiguration implements AnnotationBeanRegister<Remote> {

    @Override
    public AnnotationBeanDefinition register(Remote ann, Class<?> clazz) {
        String remoteClassName = null;
        if (ann.clazz() != Object.class)
            remoteClassName = ann.clazz().getName();
        else if (!ann.className().isEmpty())
            remoteClassName = ann.className();
        String remoteBeanName = null;
        if (!ann.beanName().isEmpty())
            remoteBeanName = ann.beanName();
        return new AnnotationBeanDefinition(ann.value(), RemoteFactoryBean.class, clazz,
                remoteClassName, remoteBeanName, ann.remote());
    }

    public static class RemoteFactoryBean implements FactoryBean<Object> {
        @Autowired
        private RemoteCall remoteCall;
        @Autowired
        private ApplicationContext applicationContext;

        private Class<?> clazz;
        private String remoteClassName;
        private String remoteBeanName;
        private Class<? extends RemoteCall> remote;


        public RemoteFactoryBean(Class<?> clazz, String remoteClassName, String remoteBeanName,
                Class<? extends RemoteCall> remote) {
            this.clazz = clazz;
            this.remoteClassName = remoteClassName;
            this.remoteBeanName = remoteBeanName;
            this.remote = remote;
        }

        @Override
        public Object getObject() throws Exception {
            RemoteCall remoteCall = applicationContext.getBean(remote);
            if (remoteClassName == null && remoteBeanName == null)
                remoteClassName = clazz.getName();
            return RemoteCallFactory.forJava(remoteCall, clazz, remoteClassName, remoteBeanName);
        }

        @Override
        public Class<?> getObjectType() {
            return clazz;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}
