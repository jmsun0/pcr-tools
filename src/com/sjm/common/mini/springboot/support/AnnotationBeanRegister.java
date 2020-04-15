package com.sjm.common.mini.springboot.support;

import java.lang.annotation.Annotation;

public interface AnnotationBeanRegister<A extends Annotation> {
    public AnnotationBeanDefinition register(A ann, Class<?> clazz);
}
