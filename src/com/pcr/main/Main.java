package com.pcr.main;

import com.pcr.common.cmdline.CmdLineParser;

public class Main {
    public static void main(String[] args) {
        try {
            main(CmdLineParser.parse(Parameter.class, args));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void main(Parameter param) {
        System.out.println(param);
    }
}
