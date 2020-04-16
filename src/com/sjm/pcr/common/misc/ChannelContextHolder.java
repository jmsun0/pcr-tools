package com.sjm.pcr.common.misc;

import com.sjm.core.nio.core.ChannelContext;

public class ChannelContextHolder {
    private static ThreadLocal<ChannelContext> holder = new ThreadLocal<ChannelContext>();

    public static ChannelContext get() {
        return holder.get();
    }

    public static void set(ChannelContext ctx) {
        holder.set(ctx);
    }

    public static void remove() {
        holder.remove();
    }
}
