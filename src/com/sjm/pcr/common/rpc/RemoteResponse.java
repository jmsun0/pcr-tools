package com.sjm.pcr.common.rpc;

public class RemoteResponse {
    private byte[] error;
    private String returnValue;
    private Class<?> returnClass;

    public byte[] getError() {
        return error;
    }

    public void setError(byte[] error) {
        this.error = error;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class<?> returnClass) {
        this.returnClass = returnClass;
    }
}
