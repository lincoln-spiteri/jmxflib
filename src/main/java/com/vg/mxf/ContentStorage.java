package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ContentStorage extends InterchangeObject {
    TagUUIDList Packages = null;
    TagUUIDList EssenceContainerData = null;

    private final static int localTags[] = new int[] { 0x1901, 0x1902, };

    @Override
    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1901:
                    int elementCount = buf.getInt();
                    Packages = inner(new TagUUIDList(elementCount));
                    break;
                case 0x1902:
                    elementCount = buf.getInt();
                    EssenceContainerData = inner(new TagUUIDList(elementCount));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
