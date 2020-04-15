package com.sjm.main;

import com.sjm.main.cmdline.Arg;

public class Parameter {
    @Arg(index = 0, desc = "Boot Mode [server|client|control]")
    public String mode;
    @Arg(index = 1, desc = "Server Port", defaultValue = "9009")
    public String port;
    @Arg(index = 2, desc = "Server Host", required = false)
    public String host;
}
