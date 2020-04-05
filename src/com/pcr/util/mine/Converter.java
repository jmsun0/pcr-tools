package com.pcr.util.mine;

public interface Converter<D, S> {
    public D convert(S data);
}
