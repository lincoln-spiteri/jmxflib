package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class EssenceContainerData extends InterchangeObject {
    TagUMID LinkedPackageUID;
    Tag32 IndexSID;
    Tag32 BodySID;

    private final static int localTags[] = new int[] { 0x2701, 0x3f06, 0x3f07 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x2701:
                    LinkedPackageUID = inner(new TagUMID());
                    break;
                case 0x3f06:
                    IndexSID = inner(new Tag32());
                    break;
                case 0x3f07:
                    BodySID = inner(new Tag32());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
