package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MpegVideoEssenceDescriptor extends CDCIEssenceDescriptor {
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.51.00");
    private final static int localTags[] = new int[] { 0x8000, 0x8001, 0x8002, 0x8003, 0x8004, 0x8005, 0x8006, 0x8007,
            0x8008, 0x8009 };
    Tag8 SingleSequence;
    Tag8 ConstantBframes;
    Tag8 CodedContentType;
    Tag8 LowDelay;
    Tag8 ClosedGOP;
    Tag8 IdenticalGOP;
    Tag16 MaxGOP;
    Tag16 BPictureCount;
    Tag32 BitRate;
    Tag8 ProfileAndLevel;

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x8000:
                    SingleSequence = inner(new Tag8(0x8000));
                    break;
                case 0x8001:
                    ConstantBframes = inner(new Tag8(0x8001));
                    break;
                case 0x8002:
                    CodedContentType = inner(new Tag8(0x8002));
                    break;
                case 0x8003:
                    LowDelay = inner(new Tag8(0x8003));
                    break;
                case 0x8004:
                    ClosedGOP = inner(new Tag8(0x8004));
                    break;
                case 0x8005:
                    IdenticalGOP = inner(new Tag8(0x8005));
                    break;
                case 0x8006:
                    MaxGOP = inner(new Tag16(0x8006));
                    break;
                case 0x8007:
                    BPictureCount = inner(new Tag16(0x8007));
                    break;
                case 0x8008:
                    BitRate = inner(new Tag32(0x8008));
                    break;
                case 0x8009:
                    ProfileAndLevel = inner(new Tag8(0x8009));
                    break;

                }
                return true;
            }
            return false;
        }
        return true;
    }

}
