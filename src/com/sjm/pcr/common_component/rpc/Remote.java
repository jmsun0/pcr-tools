package com.sjm.pcr.common_component.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {
    public String value() default "";

    public String className() default "";

    public Class<?> clazz() default Object.class;

    public String beanName() default "";

    public Class<? extends RemoteCall> remote() default RemoteCall.class;
}
