package com.sjm.pcr.client_control.cv;

import java.util.List;

public interface CvObjectManager {
    public Object invoke(Class<?> clazz, String method, Class<?>[] types, int handle,
            Object... args) throws Throwable;

    public List<Integer> listHandles();

    public void removeHandle(int handle);

    public void clearHandles();
}
