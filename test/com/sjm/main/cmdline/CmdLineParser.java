package com.sjm.main.cmdline;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sjm.core.util.Converters;
import com.sjm.core.util.MyStringBuilder;
import com.sjm.core.util.Reflection;

public class CmdLineParser {
    public static <T> T parse(Class<T> cls, String[] args) {
        if (args.length == 0) {
            usage(cls);
            return null;
        }
        for (String arg : args) {
            switch (arg) {
                case "--help":
                case "-h":
                    usage(cls);
                    return null;
                default:
                    break;
            }
        }
        Reflection.IClass clazz = Reflection.forClass(cls);
        @SuppressWarnings("unchecked")
        T config = (T) clazz.getCreator().newInstance();
        Set<String> keyset = new HashSet<>();
        for (String arg : args) {
            int index = arg.indexOf('=');
            if (index == -1)
                throw new IllegalArgumentException("'" + arg + "' must be 'KEY=VALUE'");
            String key = arg.substring(0, index);
            if (key.isEmpty())
                throw new IllegalArgumentException("KEY can not be empty");
            Reflection.Setter setter = clazz.getSetter(key);
            if (setter == null)
                throw new IllegalArgumentException("'" + key + "' is unrecognized");
            keyset.add(key);
            String value = arg.substring(index + 1);
            setter.set(config, Converters.convert(value, setter.getIType().getClazz()));
        }
        for (Reflection.IField field : getSortedFields(clazz)) {
            Arg ann = getAnn(field);
            String key = field.getName();
            if (ann.defaultValue().isEmpty()) {
                if (ann.required() && !keyset.contains(key))
                    throw new IllegalArgumentException("'" + key + "' is required");
            } else {
                if (!keyset.contains(key))
                    clazz.getSetter(key).set(config,
                            Converters.convert(ann.defaultValue(), field.getIType().getClazz()));
            }
        }
        return config;
    }

    private static void usage(Class<?> cls) {
        MyStringBuilder head = new MyStringBuilder();
        head.append("Usage: java -jar [JAR]");
        MyStringBuilder all = new MyStringBuilder();
        for (Reflection.IField field : getSortedFields(Reflection.forClass(cls))) {
            Arg ann = getAnn(field);
            String key = field.getName();
            String value;
            if (!ann.defaultValue().isEmpty())
                value = ann.defaultValue();
            else {
                if (ann.required())
                    value = "*";
                else
                    value = "?";
            }
            head.append(" " + key + "=" + value);
            all.append(String.format("\n        %-30s %s", key, ann.desc()));
        }
        all.insert(0, head, null, -1, -1);
        System.out.println(all);
        System.exit(0);
    }

    private static List<Reflection.IField> getSortedFields(Reflection.IClass clazz) {
        return clazz.getFieldMap().values().stream().filter(f -> getAnn(f) != null)
                .sorted((f1, f2) -> getAnn(f1).index() - getAnn(f2).index())
                .collect(Collectors.toList());
    }

    private static Arg getAnn(Reflection.IField field) {
        return field.getAnnotations().getAnnotation(Arg.class);
    }
}
