package com.sjm.common.core;

public interface Filter<T> {
    public boolean accept(T data);
}
