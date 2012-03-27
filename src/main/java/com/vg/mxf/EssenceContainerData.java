package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class EssenceContainerData extends InterchangeObject {
    TagUMID LinkedPackageUID;
    Tag32 IndexSID;
    Tag32 BodySID;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.23.00");

    private final static int localTags[] = new int[] { 0x2701, 0x3f06, 0x3f07 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x2701:
                    LinkedPackageUID = inner(new TagUMID(0x2701));
                    break;
                case 0x3f06:
                    IndexSID = inner(new Tag32(0x3f06));
                    break;
                case 0x3f07:
                    BodySID = inner(new Tag32(0x3f07));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
