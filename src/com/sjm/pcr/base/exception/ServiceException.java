package com.sjm.pcr.base.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private Object data;

    public ServiceException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public ServiceException(int code, String message) {
        this(code, message, null);
    }

    public ServiceException(String message) {
        this(-1, message);
    }

    public ServiceException() {
        this("");
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
