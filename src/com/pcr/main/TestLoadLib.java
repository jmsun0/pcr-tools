package com.pcr.main;

public class TestLoadLib {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.bytedeco.javacpp.cachedir.nosubdir", "true");
        System.setProperty("org.bytedeco.javacpp.logger.debug", "true");
        Class.forName("org.bytedeco.openblas.global.openblas_nolapack");
    }
}
