package com.pcr.common.core;

public interface Filter<T> {
    public boolean accept(T data);
}
