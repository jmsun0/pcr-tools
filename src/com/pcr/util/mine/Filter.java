package com.pcr.util.mine;

public interface Filter<T> {
    public boolean accept(T data);
}
