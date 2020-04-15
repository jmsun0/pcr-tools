package com.sjm.pcr.common.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {
    public String value() default "";

    public String className() default "";

    public String beanName() default "";

    public Class<? extends RemoteCall> remote();
}