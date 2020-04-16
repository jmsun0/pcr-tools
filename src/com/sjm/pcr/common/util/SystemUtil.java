package com.sjm.pcr.common.util;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

import com.sjm.core.util.IOUtil;
import com.sjm.pcr.common.model.ResInfo;


public abstract class SystemUtil {

    public static int runCmd(File dir, OutputStream out, OutputStream err, String... cmdArray)
            throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(cmdArray, null, dir);
        byte[] buffer = new byte[4096];
        if (out != null)
            try (InputStream is = p.getInputStream()) {
                IOUtil.copy(is, out, buffer, false);
                out.flush();
            }
        if (err != null)
            try (InputStream es = p.getErrorStream()) {
                IOUtil.copy(es, err, buffer, false);
            }
        return p.waitFor();
    }

    public static ResInfo runCmd(File dir, String charset, String... cmdArray) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            int code = runCmd(dir, out, err, cmdArray);
            Charset cs = IOUtil.getCharset(charset);
            return new ResInfo(code, new String(out.toByteArray(), cs),
                    new String(err.toByteArray(), cs));
        } catch (Exception e) {
            return new ResInfo(-1, "", e.getMessage());
        }
    }

    public static String getProcessId() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}

