package com.sjm.pcr.common.misc;

import com.sjm.core.util.Misc;

public class Promise<T> {
    private T result;
    private Exception error;

    public synchronized T get(long timeout) {
        try {
            if (timeout == -1) {
                while (result == null && error == null) {
                    wait();
                }
            } else {
                long endTime = System.currentTimeMillis() + timeout;
                while (result == null && error == null && System.currentTimeMillis() < endTime) {
                    if (System.currentTimeMillis() > endTime)
                        this.error = new Exception("Promise get timeout");
                    wait(1000);
                }
            }
        } catch (InterruptedException e) {
            this.error = e;
        }
        return result;
    }

    public Exception getError() {
        return error;
    }

    public synchronized void set(T result, Exception error) {
        this.result = result;
        this.error = error;
        notifyAll();
    }

    public static void main(String[] args) {
        Promise<Integer> pro = new Promise<>();
        new Thread() {
            public void run() {
                Misc.sleep(3000);
                pro.set(1234, null);
            }
        }.start();
        System.out.println(pro.get(-1));
    }
}
