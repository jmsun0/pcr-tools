package com.pcr.util.json;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pcr.util.mine.ArrayController;
import com.pcr.util.mine.MyStringBuilder;
import com.pcr.util.mine.Reflection;


public class JSONWriter {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void writeObject(Object value, MyStringBuilder sb) {
        if (value == null) {
            sb.appendNull();
            return;
        }
        Class<?> cls = value.getClass();
        Appender h = writeHandlers.get(cls);
        if (h != null) {
            h.write(value, sb);
            return;
        }
        if (cls.isArray())
            writeArray(value, sb);
        else if (value instanceof Collection)
            writeCollection((Collection<Object>) value, sb);
        else if (value instanceof Map)
            writeMap((Map<String, Object>) value, sb);
        else
            writeJavaObject(value, sb);
    }

    public static void writeList(List<Object> value, MyStringBuilder sb) {
        sb.append('[');
        int size = value.size();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                writeObject(value.get(i), sb);
                sb.append(',');
            }
            sb.deleteEnd();
        }
        sb.append(']');
    }

    public static void writeMap(Map<String, Object> value, MyStringBuilder sb) {
        sb.append('{');
        if (!value.isEmpty()) {
            for (Map.Entry<String, Object> e : value.entrySet()) {
                Object v = e.getValue();
                if (v != null) {
                    sb.append('\"').appendEscape(e.getKey(), null, -1, -1).append('\"');
                    sb.append(':');
                    writeObject(v, sb);
                    sb.append(',');
                }
            }
            sb.deleteEnd();
        }
        sb.append('}');
    }

    public static void writeCollection(Collection<Object> value, MyStringBuilder sb) {
        sb.append('[');
        if (!value.isEmpty()) {
            for (Object obj : value) {
                writeObject(obj, sb);
                sb.append(',');
            }
            sb.deleteEnd();
        }
        sb.append(']');
    }

    public static void writeArray(Object value, MyStringBuilder sb) {
        ArrayController<Object, Object> ctr = ArrayController.valueOf(value);
        int size = ctr.getLength(value);
        sb.append('[');
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                writeObject(ctr.get(value, i), sb);
                sb.append(',');
            }
            sb.deleteEnd();
        }
        sb.append(']');
    }

    public static void writeJavaObject(Object value, MyStringBuilder sb) {
        sb.append('{');
        Collection<Reflection.Getter> gs =
                Reflection.forClass(value.getClass()).getGetterMap().values();
        if (!gs.isEmpty()) {
            for (Reflection.Getter g : gs) {
                Object v = g.get(value);
                if (v != null) {
                    sb.append('\"').appendEscape(g.getName(), null, -1, -1).append('\"');
                    sb.append(':');
                    writeObject(v, sb);
                    sb.append(',');
                }
            }
            sb.deleteEnd();
        }
        sb.append('}');
    }

    static HashMap<Class<?>, Appender<?>> writeHandlers = new HashMap<Class<?>, Appender<?>>();

    public static void setAppender(Class<?> clazz, Appender<?> appender) {
        setAppender(clazz, appender);
    }

    public static Appender<JSONObject> ForJSONObject = new Appender<JSONObject>() {
        @Override
        public void write(JSONObject value, MyStringBuilder sb) {
            writeMap(value, sb);
        }
    };
    public static Appender<JSONArray> ForJSONArray = new Appender<JSONArray>() {
        @Override
        public void write(JSONArray value, MyStringBuilder sb) {
            writeList(value, sb);
        }
    };

    static {
        setAppender(String.class, Appender.ForDoubleQuotation);
        setAppender(Integer.class, Appender.ForInteger);
        setAppender(Boolean.class, Appender.ForBoolean);
        setAppender(Long.class, Appender.ForLong);
        setAppender(BigDecimal.class, Appender.ForBigDecimal);
        setAppender(BigInteger.class, Appender.ForBigInteger);
        setAppender(Byte.class, Appender.ForByte);
        setAppender(Short.class, Appender.ForShort);
        setAppender(Character.class, Appender.ForCharacter);
        setAppender(Double.class, Appender.ForDouble);
        setAppender(Float.class, Appender.ForFloat);
        setAppender(byte[].class, Appender.ForBytes);
        setAppender(char[].class, Appender.ForDoubleQuotation);
        setAppender(Date.class, Appender.ForDate);
        setAppender(java.sql.Date.class, Appender.ForDate);
        setAppender(Timestamp.class, Appender.ForDate);
        setAppender(Calendar.class, Appender.ForCalendar);
        setAppender(File.class, Appender.ForFile);
        setAppender(JSONObject.class, ForJSONObject);
        setAppender(JSONArray.class, ForJSONArray);
    }

    public interface Appender<T> {
        public void write(T value, MyStringBuilder sb);

        public static Appender<Integer> ForInteger = new Appender<Integer>() {
            @Override
            public void write(Integer value, MyStringBuilder sb) {
                sb.append((int) value);
            }
        };
        public static Appender<Long> ForLong = new Appender<Long>() {
            @Override
            public void write(Long value, MyStringBuilder sb) {
                sb.append((long) value);
            }
        };
        public static Appender<Character> ForCharacter = new Appender<Character>() {
            @Override
            public void write(Character value, MyStringBuilder sb) {
                sb.append((char) value);
            }
        };
        public static Appender<Short> ForShort = new Appender<Short>() {
            @Override
            public void write(Short value, MyStringBuilder sb) {
                sb.append((int) value);
            }
        };
        public static Appender<Byte> ForByte = new Appender<Byte>() {
            @Override
            public void write(Byte value, MyStringBuilder sb) {
                sb.append((int) value);
            }
        };
        public static Appender<Float> ForFloat = new Appender<Float>() {
            @Override
            public void write(Float value, MyStringBuilder sb) {
                sb.append(new BigDecimal(value.toString()).toString());// TODO
            }
        };
        public static Appender<Double> ForDouble = new Appender<Double>() {
            @Override
            public void write(Double value, MyStringBuilder sb) {
                sb.append(new BigDecimal(value.toString()).toString());// TODO
            }
        };
        public static Appender<Boolean> ForBoolean = new Appender<Boolean>() {
            @Override
            public void write(Boolean value, MyStringBuilder sb) {
                sb.append((boolean) value);
            }
        };
        public static Appender<BigDecimal> ForBigDecimal = new Appender<BigDecimal>() {
            @Override
            public void write(BigDecimal value, MyStringBuilder sb) {
                sb.append(value.toString());// TODO
            }
        };
        public static Appender<BigInteger> ForBigInteger = new Appender<BigInteger>() {
            @Override
            public void write(BigInteger value, MyStringBuilder sb) {
                sb.append(value.toString());// TODO
            }
        };
        public static Appender<String> ForString = new Appender<String>() {
            @Override
            public void write(String value, MyStringBuilder sb) {
                sb.append(value);
            }
        };
        public static Appender<byte[]> ForBytes = new Appender<byte[]>() {
            @Override
            public void write(byte[] value, MyStringBuilder sb) {
                sb.appendBase64(value);
            }
        };
        public static Appender<char[]> ForChars = new Appender<char[]>() {
            @Override
            public void write(char[] value, MyStringBuilder sb) {
                sb.append(value);
            }
        };
        public static Appender<Object> ForDoubleQuotation = new Appender<Object>() {
            @Override
            public void write(Object value, MyStringBuilder sb) {
                sb.append('\"').appendEscape(value, null, -1, -1).append('\"');
            }
        };
        public static Appender<Object> ForSingleQuotation = new Appender<Object>() {
            @Override
            public void write(Object value, MyStringBuilder sb) {
                sb.append('\'').appendEscape(value, null, -1, -1).append('\'');
            }
        };
        public static Appender<Date> ForDate = new Appender<Date>() {
            @Override
            public void write(Date value, MyStringBuilder sb) {
                sb.append(value.getTime());
            }
        };
        public static Appender<Calendar> ForCalendar = new Appender<Calendar>() {
            @Override
            public void write(Calendar value, MyStringBuilder sb) {
                sb.append(value.getTimeInMillis());
            }
        };
        public static Appender<File> ForFile = new Appender<File>() {
            @Override
            public void write(File value, MyStringBuilder sb) {
                sb.append(value.getPath());
            }
        };
        public static Appender<Class<?>> ForClass = new Appender<Class<?>>() {
            @Override
            public void write(Class<?> value, MyStringBuilder sb) {
                sb.append(value.getName());
            }
        };
    }

}
