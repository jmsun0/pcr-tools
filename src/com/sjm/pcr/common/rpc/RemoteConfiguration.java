package com.sjm.pcr.common.rpc;


import com.sjm.core.mini.springboot.api.ApplicationContext;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.DependsOn;
import com.sjm.core.mini.springboot.api.FactoryBean;
import com.sjm.core.mini.springboot.support.AnnotationBeanDefinition;
import com.sjm.core.mini.springboot.support.AnnotationBeanRegister;

@Component
public class RemoteConfiguration implements AnnotationBeanRegister<Remote> {

    @Override
    public AnnotationBeanDefinition register(Remote ann, Class<?> clazz) {
        return new AnnotationBeanDefinition(RemoteFactoryBean.class, clazz,
                ann.className().isEmpty() ? null : ann.className(),
                ann.beanName().isEmpty() ? null : ann.beanName(), ann.remote());
    }

    @DependsOn("com.sjm.pcr.common.rpc.impl")
    public static class RemoteFactoryBean implements FactoryBean<Object> {
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
