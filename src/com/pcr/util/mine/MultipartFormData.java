package com.pcr.util.mine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MultipartFormData {
    private MultipartInputStream mis;

    public MultipartFormData(InputStream is) {
        mis = new MultipartInputStream(is, 4096);
    }

    public boolean next() throws IOException {
        headerInfo = null;
        return mis.next();
    }

    public String getName() {
        return getHeaderInfo()[0];
    }

    public String getOriginalFilename() {
        return getHeaderInfo()[1];
    }

    public String getContentType() {
        return getHeaderInfo()[2];
    }

    public InputStream getInputStream() throws IOException {
        return mis;
    }

    private String[] headerInfo;

    private String[] getHeaderInfo() {
        if (headerInfo == null) {
            headerInfo = new String[3];
            Map<String, String> headers = mis.getHeaders();
            String content = headers.get("content-disposition");
            if (content != null) {
                for (String s : content.split(";")) {
                    s = s.trim();
                    if (s.startsWith("name")) {
                        headerInfo[0] = getValue(s, "name");
                    } else if (s.startsWith("filename")) {
                        headerInfo[1] = getValue(s, "filename");
                    }
                }
            }
            headerInfo[2] = headers.get("content-type");
            if (headerInfo[2] != null)
                headerInfo[2] = headerInfo[2].trim();
        }
        return headerInfo;
    }

    private static String getValue(String str, String key) {
        int i = str.indexOf('=', key.length());
        if (i != -1) {
            str = str.substring(i + 1).trim();
            if (!str.isEmpty()) {
                if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"')
                    str = new MyStringBuilder().appendUnEscape(str, null, 1, str.length() - 1)
                            .toString();
            }
            return str;
        }
        return null;
    }

    static class MultipartInputStream extends InputStream {
        private InputStream is;
        private byte[] boundary;
        private byte[] buffer;
        private int index, end, size, limit;
        private Map<String, String> headers;

        public MultipartInputStream(InputStream is, int bufferSize) {
            this.is = is;
            this.buffer = new byte[bufferSize];
        }

        public MultipartInputStream(InputStream is) {
            this(is, 4096);
        }

        @Override
        public int read() throws IOException {
            if (index == end && !fill())
                return -1;
            return buffer[index++] & 0xff;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int remain = len;
            while (remain != 0) {
                if (index == end && !fill())
                    if (remain == len)
                        return -1;
                    else
                        break;
                int n = Math.min(remain, end - index);
                System.arraycopy(buffer, index, b, off, n);
                remain -= n;
                index += n;
                off += n;
            }
            return len - remain;
        }

        private void readBoundary() throws IOException {
            int len = IOUtil.readLine(is, buffer);
            if (len <= 0)
                throw new IOException();
            boundary = new byte[len + 2];
            boundary[0] = 0xd;
            boundary[1] = 0xa;
            System.arraycopy(buffer, 0, boundary, 2, len);
            limit = buffer.length - boundary.length;
        }

        private void readHeaders() throws IOException {
            headers = new HashMap<>();
            while (true) {
                String str = IOUtil.readLine(this, buffer, Charset.defaultCharset());
                if (str == null)
                    throw new IOException();
                if (str.isEmpty())
                    break;
                String[] kv = str.split(":");
                if (kv.length != 2)
                    throw new IOException();
                headers.put(kv[0].trim().toLowerCase(), kv[1].trim());
            }
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public boolean next() throws IOException {
            if (boundary == null) {
                readBoundary();
                readFull(0);
                findEndPoint();
                readHeaders();
                return true;
            } else {
                while (read(buffer) != -1);
                index += boundary.length;
                if (size == buffer.length && index + 4 >= limit)
                    moveTailToHead();
                int c1 = buffer[index++], c2 = buffer[index++];
                if (c1 == 0x2d && c2 == 0x2d) {
                    c1 = buffer[index++];
                    c2 = buffer[index++];
                    if (c1 == 0xd && c2 == 0xa)
                        return false;
                    else
                        throw new IOException();
                } else if (c1 == 0xd && c2 == 0xa) {
                    findEndPoint();
                    readHeaders();
                    return true;
                } else
                    throw new IOException();
            }
        }

        private boolean fill() throws IOException {
            if (size == buffer.length && index == limit) {
                moveTailToHead();
                findEndPoint();
                return true;
            }
            return false;
        }

        private void moveTailToHead() throws IOException {
            int remain = buffer.length - index;
            System.arraycopy(buffer, index, buffer, 0, remain);
            index = 0;
            readFull(remain);
        }

        private void findEndPoint() throws IOException {
            if (size < buffer.length) {
                end = findEndPoint(index, size);
                if (end == size)
                    throw new IOException();
            } else {
                end = findEndPoint(index, limit);
            }
        }

        private int findEndPoint(int start, int end) {
            for (byte first = boundary[0]; start < end; start++)
                if (buffer[start] == first && startWith(buffer, start, boundary))
                    return start;
            return end;
        }

        private void readFull(int start) throws IOException {
            int remain = buffer.length - start;
            for (; remain != 0;) {
                int n = is.read(buffer, start, remain);
                if (n == -1)
                    break;
                remain -= n;
                start += n;
            }
            size = start;
        }

        private static boolean startWith(byte[] bytes, int offset, byte[] prefix) {
            for (int i = 0; i < prefix.length; i++)
                if (bytes[offset + i] != prefix[i])
                    return false;
            return true;
        }
    }
}
