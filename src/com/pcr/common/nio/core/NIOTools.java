package com.pcr.common.nio.core;

public class NIOTools {

    public static int getInt(byte[] bytes) {
        int val = 0;
        for (int i = 0; i < 4; i++) {
            val += (bytes[i] & 0xff) << (8 * (3 - i));
        }
        return val;
    }

    public static void putInt(byte[] bytes, int val) {
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((val >>> (8 * (3 - i))) & 0xff);
        }
    }
}
