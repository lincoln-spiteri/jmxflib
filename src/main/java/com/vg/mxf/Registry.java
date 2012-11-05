package com.vg.mxf;

import static com.vg.mxf.Key.key;

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

public class Registry {
    static TreeMap<Key, Class<? extends MxfValue>> m = new TreeMap<Key, Class<? extends MxfValue>>();
    static {
        m.put(HeaderPartitionPack.Key, HeaderPartitionPack.class);
        m.put(BodyPartitionPack.Key, BodyPartitionPack.class);
        m.put(FooterPartitionPack.Key, FooterPartitionPack.class);
        m.put(IndexTable.Key, IndexTable.class);
        m.put(Preface.Key, Preface.class);
        m.put(PrimerPack.Key, PrimerPack.class);
        m.put(Identification.Key, Identification.class);
        m.put(ContentStorage.Key, ContentStorage.class);
        m.put(EssenceContainerData.Key, EssenceContainerData.class);
        m.put(MaterialPackage.Key, MaterialPackage.class);
        m.put(TimelineTrack.Key, TimelineTrack.class);
        m.put(Sequence.Key, Sequence.class);
        m.put(TimecodeComponent.Key, TimecodeComponent.class);
        m.put(SourceClip.Key, SourceClip.class);
        m.put(SourcePackage.Key, SourcePackage.class);
        m.put(RGBAEssenceDescriptor.Key, RGBAEssenceDescriptor.class);
        m.put(CDCIEssenceDescriptor.Key, CDCIEssenceDescriptor.class);
        m.put(MpegVideoEssenceDescriptor.Key, MpegVideoEssenceDescriptor.class);
        m.put(JPEG2000PictureSubDescriptor.Key, JPEG2000PictureSubDescriptor.class);
        m.put(RandomIndex.Key, RandomIndex.class);
        m.put(WaveAudioEssenceDescriptor.Key, WaveAudioEssenceDescriptor.class);
    }
    /** Sequence.DataDefiniton "Picture Essence Track" */
    public final static Key PictureUL = key("06.0E.2B.34.04.01.01.01.01.03.02.02.01.00.00.00");
    public final static Key FillerKey = key("06.0E.2B.34.01.01.01.02.03.01.02.10.01.00.00.00");
    public final static Key FrameKey = key("06.0E.2B.34.01.02.01.01.0D.01.03.01.15.01.08.01");
    public final static Key FrameWrappedBroadcastWave = key("06.0E.2B.34.01.02.01.01.0D.01.03.01.16.01.01.01");

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
