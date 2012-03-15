package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TimecodeComponent extends StructuralComponent {
    TagUUIDList StructuralComponents;
    Tag64 StartTimecode;
    Tag16 RoundedTimecodeBase;
    Tag8 DropFrame;

    private final static int localTags[] = new int[] { 0x1501, 0x1502, 0x1503 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1501:
                    StartTimecode = inner(new Tag64());
                    break;
                case 0x1502:
                    RoundedTimecodeBase = inner(new Tag16());
                    break;
                case 0x1503:
                    DropFrame = inner(new Tag8());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
