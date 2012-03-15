package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CDCIEssenceDescriptor extends GenericPictureEssenceDescriptor {
    Tag32 ComponentDepth;
    Tag32 HorizontalSubsampling;
    Tag32 VerticalSubsampling;
    Tag8 ColorSiting;
    Tag8 ReversedByteOrder;
    Tag16 PaddingBits;
    Tag32 AlphaSampleDepth;
    Tag32 BlackRefLevel;
    Tag32 WhiteReflevel;
    Tag32 ColorRange;

    private final static int localTags[] = new int[] { 0x3301, 0x3302, 0x3303, 0x3304, 0x3305, 0x3306, 0x3307, 0x3308,
            0x3309, 0x330b, };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3301:
                    ComponentDepth = inner(new Tag32());
                    break;
                case 0x3302:
                    HorizontalSubsampling = inner(new Tag32());
                    break;
                case 0x3308:
                    VerticalSubsampling = inner(new Tag32());
                    break;
                case 0x3303:
                    ColorSiting = inner(new Tag8());
                    break;
                case 0x330b:
                    ReversedByteOrder = inner(new Tag8());
                    break;
                case 0x3307:
                    PaddingBits = inner(new Tag16());
                    break;
                case 0x3309:
                    AlphaSampleDepth = inner(new Tag32());
                    break;
                case 0x3304:
                    BlackRefLevel = inner(new Tag32());
                    break;
                case 0x3305:
                    WhiteReflevel = inner(new Tag32());
                    break;
                case 0x3306:
                    ColorRange = inner(new Tag32());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
