package com.sjm.common.core;

public interface Converter<D, S> {
    public D convert(S data);
}
