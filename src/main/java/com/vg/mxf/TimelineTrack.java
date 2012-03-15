package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TimelineTrack extends GenericTrack {
    TagRational EditRate;
    Tag64 Origin;

    private final static int localTags[] = new int[] { 0x4b01, 0x4b02 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x4b01:
                    EditRate = inner(new TagRational());
                    break;
                case 0x4b02:
                    Origin = inner(new Tag64());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
