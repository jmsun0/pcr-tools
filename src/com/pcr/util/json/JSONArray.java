package com.pcr.util.json;

import java.util.List;

import com.pcr.util.mine.ArrayController;
import com.pcr.util.mine.Converters;
import com.pcr.util.mine.Lists;
import com.pcr.util.mine.MyStringBuilder;

public class JSONArray extends Lists.MyArrayList<Object, Object> {
    public static final JSONArray Empty = new JSONArray();

    public JSONArray(Object arr) {
        ArrayController<Object, Object> ctr = ArrayController.valueOf(arr);
        int len = ctr.getLength(arr);
        resize(len);
        for (int i = 0; i < len; i++)
            add(ctr.get(arr, i));
    }

    public JSONArray(int cap) {
        super(cap);
    }

    public JSONArray() {}

    public <T> List<T> toJavaList(Class<T> clazz) {
        return Lists.cache(Lists.convert(this, Converters.valueOf(clazz)));
    }

    @Override
    public String toString() {
        MyStringBuilder sb = new MyStringBuilder();
        JSONWriter.writeList(this, sb);
        return sb.toString();
    }
}
