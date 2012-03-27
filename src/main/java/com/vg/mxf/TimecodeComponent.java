package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TimecodeComponent extends StructuralComponent {
    TagUUIDList StructuralComponents;
    Tag64 StartTimecode;
    Tag16 RoundedTimecodeBase;
    Tag8 DropFrame;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.14.00");

    private final static int localTags[] = new int[] { 0x1501, 0x1502, 0x1503 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1501:
                    StartTimecode = inner(new Tag64(0x1501));
                    break;
                case 0x1502:
                    RoundedTimecodeBase = inner(new Tag16(0x1502));
                    break;
                case 0x1503:
                    DropFrame = inner(new Tag8(0x1503));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    public int getStartTimecode() {
        return this.StartTimecode != null ? (int) this.StartTimecode.value.get() : 0;
    }

    public int getRoundedTimecodeBase() {
        return this.RoundedTimecodeBase != null ? this.RoundedTimecodeBase.value.get() : 0;
    }

    public int getDropFrame() {
        return this.DropFrame != null ? this.DropFrame.value.get() : 0;
    }

}
