package com.sjm.pcr.common.model;

public class ResInfo {
    public int code;
    public String out;
    public String err;

    public ResInfo(int code, String out, String err) {
        super();
        this.code = code;
        this.out = out;
        this.err = err;
    }

    public ResInfo() {
        super();
    }

    @Override
    public String toString() {
        return "code=" + code + "\nout=[\n" + out + "\n]\nerr=[\n" + err + "\n]";
    }
}
