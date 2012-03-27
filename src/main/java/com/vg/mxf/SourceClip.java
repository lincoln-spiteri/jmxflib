package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SourceClip extends StructuralComponent {
    Tag64 StartPosition;
    TagUMID SourcePackageId;
    Tag32 SourceTrackId;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.11.00");

    private final static int localTags[] = new int[] { 0x1101, 0x1102, 0x1201 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1201:
                    StartPosition = inner(new Tag64(0x1201));
                    break;
                case 0x1101:
                    SourcePackageId = inner(new TagUMID(0x1101));
                    break;
                case 0x1102:
                    SourceTrackId = inner(new Tag32(0x1102));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
