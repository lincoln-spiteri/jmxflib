package com.vg.mxf;

import java.util.TreeMap;

import com.vg.mxf.Registry.ULDesc;

public class LocalTags {
    TreeMap<Integer, LocalTagKey> tag2key;
    TreeMap<String, LocalTagKey> key2key;
    TreeMap<String, Registry.ULDesc> map;

    public LocalTagKey getLtk(String key) {
        return key2key.get(key);
    }

    public LocalTagKey getLtk(int tag) {
        return tag2key.get(tag);
    }

    public Registry.ULDesc getDesc(String key) {
        return map.get(key);
    }
}