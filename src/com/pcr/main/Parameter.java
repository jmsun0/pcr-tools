package com.pcr.main;

import com.pcr.util.cmdline.Desc;

public class Parameter {
    @Desc(index = 0, desc = "Boot Mode [server|client|control]")
    public String mode;
    @Desc(index = 1, desc = "Server Port", defaultValue = "9009")
    public String port;
    @Desc(index = 2, desc = "Server Host", required = false)
    public String host;
}
