package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GenericTrack extends InterchangeObject {
    Tag32 TrackId;
    Tag32 TrackNumber;
    TagUTF16String TrackName;
    TagUUID Sequence;

    private static int localTags[] = new int[] { 0x4801, 0x4802, 0x4803, 0x4804 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x4801:
                    TrackId = inner(new Tag32());
                    break;
                case 0x4804:
                    TrackNumber = inner(new Tag32());
                    break;
                case 0x4802:
                    TrackName = inner(new TagUTF16String(sz));
                    break;
                case 0x4803:
                    Sequence = inner(new TagUUID());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
