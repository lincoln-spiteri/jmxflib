package com.vg.mxf;

import java.util.TreeMap;

public class LocalTagsIndex {
    TreeMap<Integer, LocalTagKey> tag2key = new TreeMap<Integer, LocalTagKey>();
    TreeMap<String, LocalTagKey> key2key = new TreeMap<String, LocalTagKey>();

    public LocalTagKey getLtk(String key) {
        return key2key.get(key);
    }

    public LocalTagKey getLtk(int tag) {
        return tag2key.get(tag);
    }

    public static LocalTagsIndex createLocalTagsIndex(LocalTagKey[] ltks) {
        LocalTagsIndex tags = new LocalTagsIndex();
        for (LocalTagKey ltk : ltks) {
            tags.tag2key.put(ltk.localTag.get(), ltk);
            tags.key2key.put(ltk.key.toString(), ltk);
        }
        return tags;
    }
}