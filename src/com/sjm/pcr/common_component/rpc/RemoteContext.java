package com.sjm.pcr.common_component.rpc;

import java.util.concurrent.TimeUnit;

public class RemoteContext {
    private static ThreadLocal<RemoteContext> holder = new ThreadLocal<RemoteContext>();

    public static RemoteContext get() {
        RemoteContext ctx = holder.get();
        if (ctx == null) {
            holder.set(ctx = new RemoteContext());
        }
        return ctx;
    }

    public static void remove() {
        holder.remove();
    }

    private String remoteName;
    private long timeout = TimeUnit.MINUTES.toMillis(5);

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
