package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GenericPictureEssenceDescriptor extends FileDescriptor {
    Tag8 SignalStandard;
    Tag8 FrameLayout;
    Tag32 StoredWidth;
    Tag32 StoredHeight;
    Tag32 StoredF2Offset;
    Tag32 SampledWidth;
    Tag32 SampledHeight;
    Tag32 SampledXOffset;
    Tag32 SampledYOffset;
    Tag32 DisplayHeight;
    Tag32 DisplayWidth;
    Tag32 DisplayXOffset;
    Tag32 DisplayYOffset;
    Tag32 DisplayF2Offset;
    TagRational AspectRatio;
    Tag8 ActiveFormatDescriptor;
    Tag8 AlphaTransparency;
    TagValue VideoLineMap;
    TagUUID TransferCharacteristic;
    Tag32 ImageAlignmentOffset;
    Tag32 ImageStartOffset;
    Tag32 ImageEndOffset;
    Tag8 FieldDominance;
    TagKey PictureEssenceCoding;
    TagUUID CodingEquations;
    TagUUID ColorPrimaries;

    private final static int localTags[] = new int[] { 0x3201, 0x3202, 0x3203, 0x3204, 0x3205, 0x3206, 0x3207, 0x3208,
            0x3209, 0x320a, 0x320b, 0x320c, 0x320d, 0x320e, 0x320f, 0x3210, 0x3211, 0x3212, 0x3213, 0x3214, 0x3215,
            0x3216, 0x3217, 0x3218, 0x3219, 0x321a };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3215:
                    SignalStandard = inner(new Tag8());
                    break;
                case 0x320c:
                    FrameLayout = inner(new Tag8());
                    break;
                case 0x3203:
                    StoredWidth = inner(new Tag32());
                    break;
                case 0x3202:
                    StoredHeight = inner(new Tag32());
                    break;
                case 0x3216:
                    StoredF2Offset = inner(new Tag32());
                    break;
                case 0x3205:
                    SampledWidth = inner(new Tag32());
                    break;
                case 0x3204:
                    SampledHeight = inner(new Tag32());
                    break;
                case 0x3206:
                    SampledXOffset = inner(new Tag32());
                    break;
                case 0x3207:
                    SampledYOffset = inner(new Tag32());
                    break;
                case 0x3208:
                    DisplayHeight = inner(new Tag32());
                    break;
                case 0x3209:
                    DisplayWidth = inner(new Tag32());
                    break;
                case 0x320a:
                    DisplayXOffset = inner(new Tag32());
                    break;
                case 0x320b:
                    DisplayYOffset = inner(new Tag32());
                    break;
                case 0x3217:
                    DisplayF2Offset = inner(new Tag32());
                    break;
                case 0x320e:
                    AspectRatio = inner(new TagRational());
                    break;
                case 0x3218:
                    ActiveFormatDescriptor = inner(new Tag8());
                    break;
                case 0x320d:
                    VideoLineMap = inner(new TagValue(sz));
                case 0x320f:
                    AlphaTransparency = inner(new Tag8());
                    break;
                case 0x3210:
                    TransferCharacteristic = inner(new TagUUID());
                    break;
                case 0x3211:
                    ImageAlignmentOffset = inner(new Tag32());
                    break;
                case 0x3213:
                    ImageStartOffset = inner(new Tag32());
                    break;
                case 0x3214:
                    ImageEndOffset = inner(new Tag32());
                    break;
                case 0x3212:
                    FieldDominance = inner(new Tag8());
                    break;
                case 0x3201:
                    PictureEssenceCoding = inner(new TagKey());
                    break;
                case 0x321a:
                    CodingEquations = inner(new TagUUID());
                    break;
                case 0x3219:
                    ColorPrimaries = inner(new TagUUID());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
