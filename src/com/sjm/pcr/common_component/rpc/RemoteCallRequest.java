package com.sjm.pcr.common_component.rpc;

public class RemoteCallRequest {
    private String className;
    private String beanName;
    private String method;
    private Object[] args;
    private Class<?>[] types;

    public RemoteCallRequest() {}

    public RemoteCallRequest(String className, String beanName, String method, Class<?>[] types,
            Object[] args) {
        this.className = className;
        this.beanName = beanName;
        this.method = method;
        this.args = args;
        this.types = types;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
