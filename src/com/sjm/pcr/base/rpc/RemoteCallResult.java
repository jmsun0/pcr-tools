package com.sjm.pcr.base.rpc;

public class RemoteCallResult {
    private Object returnValue;// 返回值
    private Throwable error;// 抛出的异常，为空表示正常返回

    public RemoteCallResult() {}

    public RemoteCallResult(Object returnValue, Throwable error) {
        this.returnValue = returnValue;
        this.error = error;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
