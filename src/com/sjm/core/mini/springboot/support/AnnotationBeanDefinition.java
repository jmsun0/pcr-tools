package com.sjm.core.mini.springboot.support;

import com.sjm.core.mini.springboot.api.FactoryBean;

public class AnnotationBeanDefinition {
    public Class<? extends FactoryBean<?>> factoryClass;
    public Object[] constructorArgs;

    public AnnotationBeanDefinition(Class<? extends FactoryBean<?>> factoryClass,
            Object... constructorArgs) {
        this.factoryClass = factoryClass;
        this.constructorArgs = constructorArgs;
    }

    public AnnotationBeanDefinition() {}
}
