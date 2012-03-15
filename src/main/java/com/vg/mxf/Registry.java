package com.vg.mxf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vg.mxf.Registry.ULDesc;

public class Registry {
    public static class ULDesc {
        public final String ul;
        public final String refName;
        public final String desc;

        public ULDesc(String ul, String refName, String desc) {
            super();
            this.ul = ul;
            this.refName = refName;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return gson().toJson(this);
        }

    }

    private final static Registry INSTANCE;
    static {
        try {
            TreeMap<String, Registry.ULDesc> map = readToMap(Registry.class.getResourceAsStream("rp210v12.js"));
            TreeMap<String, Registry.ULDesc> map210 = readToMap(Registry.class.getResourceAsStream("rp224v11.js"));
            for (Entry<String, Registry.ULDesc> entry : map210.entrySet()) {
                Assert.assertFalse(map.containsKey(entry.getKey()));
                map.put(entry.getKey(), entry.getValue());
            }
            INSTANCE = new Registry(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static TreeMap<String, Registry.ULDesc> readToMap(InputStream inputStream) throws IOException {
        Registry.ULDesc[] fromJson = gson().fromJson(new BufferedReader(new InputStreamReader(new BufferedInputStream(
                inputStream))), Registry.ULDesc[].class);
        TreeMap<String, Registry.ULDesc> map = new TreeMap<String, Registry.ULDesc>();
        for (int i = 0; i < fromJson.length; i++) {
            Registry.ULDesc ulDesc = fromJson[i];
            map.put(ulDesc.ul, ulDesc);
        }
        return map;
    }

    private static Gson gson() {
        GsonBuilder b = new GsonBuilder();
        Gson create = b.create();
        return create;
    }

    public static Registry getInstance() {
        return INSTANCE;
    }

    private final TreeMap<String, Registry.ULDesc> map;

    Registry(TreeMap<String, Registry.ULDesc> map) {
        this.map = map;
    }

    public ULDesc get(Key key) {
        return map.get(key.toString());
    }

    public ULDesc get(String ul) {
        return map.get(ul);
    }

}
