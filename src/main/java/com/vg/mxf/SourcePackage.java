package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SourcePackage extends GenericPackage {
    TagUUID Descriptor;

    private final static int localTags[] = new int[] { 0x4701 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x4701:
                    Descriptor = inner(new TagUUID());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
