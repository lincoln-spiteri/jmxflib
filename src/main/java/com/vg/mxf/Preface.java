package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

public class Preface extends InterchangeObject {
    TagDate LastModified = null;
    Tag16 Version = null;
    Tag32 ObjectModel = null;
    TagUUID PrimaryPackage = null;
    TagUUIDList Identifications = null;
    TagUUID ContentStorage = null;
    TagKey OperationalPattern = null;
    TagKeyList EssenceContainers = null;
    TagUUIDList DMSchemes = null;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.2F.00");

    private final static int localTags[] = new int[] { 0x3b02, 0x3b03, 0x3b05, 0x3b06, 0x3b07, 0x3b08, 0x3b09, 0x3b0a,
            0x3b0b };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3b02:
                    Assert.assertEquals(8, sz);
                    LastModified = inner(new TagDate(0x3b02));
                    break;
                case 0x3b05:
                    Assert.assertEquals(2, sz);
                    Version = inner(new Tag16(0x3b05));
                    break;
                case 0x3b07:
                    Assert.assertEquals(4, sz);
                    ObjectModel = inner(new Tag32(0x3b07));
                    break;
                case 0x3b08:
                    Assert.assertEquals(16, sz);
                    PrimaryPackage = inner(new TagUUID(0x3b08));
                    break;
                case 0x3b06:
                    int elementCount = buf.getInt();
                    Identifications = inner(new TagUUIDList(0x3b06, elementCount));
                    break;
                case 0x3b03:
                    Assert.assertEquals(16, sz);
                    ContentStorage = inner(new TagUUID(0x3b03));
                    break;
                case 0x3b09:
                    Assert.assertEquals(16, sz);
                    OperationalPattern = inner(new TagKey(0x3b09));
                    break;
                case 0x3b0a:
                    elementCount = buf.getInt();
                    EssenceContainers = inner(new TagKeyList(0x3b0a, elementCount));
                    break;
                case 0x3b0b:
                    elementCount = buf.getInt();
                    DMSchemes = inner(new TagUUIDList(0x3b0b, elementCount));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public List<UUID> getReferencedUIDs() {
        List<UUID> list = new ArrayList<UUID>();
        if (PrimaryPackage != null) {
            list.addAll(Arrays.asList(PrimaryPackage.uuid));
        }
        if (Identifications != null) {
            list.addAll(Arrays.asList(Identifications.uuids));
        }
        if (ContentStorage != null) {
            list.addAll(Arrays.asList(ContentStorage.uuid));
        }
        if (DMSchemes != null) {
            list.addAll(Arrays.asList(DMSchemes.uuids));
        }
        return Collections.unmodifiableList(list);
    }
}
