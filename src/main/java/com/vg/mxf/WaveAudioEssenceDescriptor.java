package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class WaveAudioEssenceDescriptor extends GenericSoundEssenceDescriptor {
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.48.00");
    private final static int localTags[] = new int[] { 0x3d09, 0x3d0a, 0x3d0b, 0x3d29, 0x3d2a, 0x3d2b, 0x3d2c, 0x3d2d,
            0x3d2e, 0x3d2f, 0x3d30, 0x3d31, 0x3d32 };
    Tag16 BlockAlign;
    Tag8 SequenceOffset;
    Tag32 AverageBytesPerSecond;
    TagUUID ChannelAssignment;
    Tag32 PeakEnvelopeVersion;
    Tag32 PeakEnvelopeFormat;
    Tag32 PointsPerPeakValue;
    Tag32 PeakEnvelopeBlockSize;
    Tag32 PeakChannels;
    Tag32 PeakFrames;
    Tag64 PeakOfPeaksPosition;
    Tag64 PeakEnvelopeTimestamp;
    TagValue PeakEnvelopeData;

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3d09:
                    AverageBytesPerSecond = inner(new Tag32(0x3d09));
                    break;
                case 0x3d0a:
                    BlockAlign = inner(new Tag16(0x3d0a));
                    break;
                case 0x3d0b:
                    SequenceOffset = inner(new Tag8(0x3d0b));
                    break;
                case 0x3d29:
                    PeakEnvelopeVersion = inner(new Tag32(0x3d29));
                    break;
                case 0x3d2a:
                    PeakEnvelopeFormat = inner(new Tag32(0x3d2a));
                    break;
                case 0x3d2b:
                    PointsPerPeakValue = inner(new Tag32(0x3d2b));
                    break;
                case 0x3d2c:
                    PeakEnvelopeBlockSize = inner(new Tag32(0x3d2c));
                    break;
                case 0x3d2d:
                    PeakChannels = inner(new Tag32(0x3d2d));
                    break;
                case 0x3d2e:
                    PeakFrames = inner(new Tag32(0x3d2e));
                    break;
                case 0x3d2f:
                    PeakOfPeaksPosition = inner(new Tag64(0x3d2f));
                    break;
                case 0x3d30:
                    PeakEnvelopeTimestamp = inner(new Tag64(0x3d30));
                    break;
                case 0x3d31:
                    PeakEnvelopeData = inner(new TagValue(0x3d31, sz));
                    break;
                case 0x3d32:
                    ChannelAssignment = inner(new TagUUID(0x3d32));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
