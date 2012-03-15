package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GenericDescriptor extends InterchangeObject {
    TagUUIDList Locators;
    TagUUIDList Unknown;

    private final static int localTags[] = new int[] { 0x2f01, 0x2f02 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x2f01:
                    int count = buf.getInt();
                    Locators = inner(new TagUUIDList(count));
                    break;
                case 0x2f02:
                    count = buf.getInt();
                    Unknown = inner(new TagUUIDList(count));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
