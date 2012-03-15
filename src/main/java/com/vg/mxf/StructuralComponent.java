package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class StructuralComponent extends InterchangeObject {
    TagKey DataDefinition;
    Tag64 Duration;

    private final static int localTags[] = new int[] { 0x0201, 0x0202 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x0201:
                    DataDefinition = inner(new TagKey());
                    break;
                case 0x0202:
                    Duration = inner(new Tag64());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
