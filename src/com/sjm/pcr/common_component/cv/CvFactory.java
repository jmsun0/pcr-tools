package com.sjm.pcr.common_component.cv;

public interface CvFactory {
    public <T> T allocate(Class<T> clazz, Object... args);
}
