package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SourceClip extends StructuralComponent {
    Tag64 StartPosition;
    TagUMID SourcePackageId;
    Tag32 SourceTrackId;

    private final static int localTags[] = new int[] { 0x1101, 0x1102, 0x1201 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1201:
                    StartPosition = inner(new Tag64());
                    break;
                case 0x1101:
                    SourcePackageId = inner(new TagUMID());
                    break;
                case 0x1102:
                    SourceTrackId = inner(new Tag32());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
