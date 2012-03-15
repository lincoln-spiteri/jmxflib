package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

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

    private final static int localTags[] = new int[] { 0x3b02, 0x3b03, 0x3b05, 0x3b06, 0x3b07, 0x3b08, 0x3b09, 0x3b0a, 0x3b0b };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3b02:
                    Assert.assertEquals(8, sz);
                    LastModified = inner(new TagDate());
                    break;
                case 0x3b05:
                    Assert.assertEquals(2, sz);
                    Version = inner(new Tag16());
                    break;
                case 0x3b07:
                    Assert.assertEquals(4, sz);
                    ObjectModel = inner(new Tag32());
                    break;
                case 0x3b08:
                    Assert.assertEquals(16, sz);
                    PrimaryPackage = inner(new TagUUID());
                    break;
                case 0x3b06:
                    int elementCount = buf.getInt();
                    Identifications = inner(new TagUUIDList(elementCount));
                    break;
                case 0x3b03:
                    Assert.assertEquals(16, sz);
                    ContentStorage = inner(new TagUUID());
                    break;
                case 0x3b09:
                    Assert.assertEquals(16, sz);
                    OperationalPattern = inner(new TagKey());
                    break;
                case 0x3b0a:
                    elementCount = buf.getInt();
                    EssenceContainers = inner(new TagKeyList(elementCount));
                    break;
                case 0x3b0b:
                    elementCount = buf.getInt();
                    DMSchemes = inner(new TagUUIDList(elementCount));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
