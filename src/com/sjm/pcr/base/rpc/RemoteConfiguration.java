package com.sjm.pcr.base.rpc;


import com.sjm.common.mini.springboot.api.ApplicationContext;
import com.sjm.common.mini.springboot.api.Autowired;
import com.sjm.common.mini.springboot.api.Component;
import com.sjm.common.mini.springboot.api.DependsOn;
import com.sjm.common.mini.springboot.api.FactoryBean;
import com.sjm.common.mini.springboot.support.AnnotationBeanDefinition;
import com.sjm.common.mini.springboot.support.AnnotationBeanRegister;

@Component
public class RemoteConfiguration implements AnnotationBeanRegister<Remote> {

    @Override
    public AnnotationBeanDefinition register(Remote ann, Class<?> clazz) {
        return new AnnotationBeanDefinition(RemoteFactoryBean.class, clazz,
                ann.className().isEmpty() ? null : ann.className(),
                ann.beanName().isEmpty() ? null : ann.beanName(), ann.remote());
    }

    @DependsOn("com.sjm.pcr.base.rpc.impl")
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
