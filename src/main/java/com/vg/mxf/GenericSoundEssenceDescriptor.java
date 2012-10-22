package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class GenericSoundEssenceDescriptor extends FileDescriptor {
    private final static int localTags[] = new int[] { 0x3D01, 0x3D02, 0x3D03, 0x3D04, 0x3D05, 0x3D06, 0x3D07, 0x3D0C, };

    TagRational AudioSamplingRate;
    Tag8 Locked;
    Tag8 AudioRefLevel;
    Tag8 ElectroSpatialFormulation;
    Tag32 ChannelCount;
    Tag32 QuantizationBits;
    Tag8 DialNorm;
    TagUUID SoundEssenceCoding;

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3d03:
                    AudioSamplingRate = inner(new TagRational(0x3d03));
                    break;
                case 0x3d02:
                    Locked = inner(new Tag8(0x3d02));
                    break;
                case 0x3d04:
                    AudioRefLevel = inner(new Tag8(0x3d04));
                    break;
                case 0x3d05:
                    ElectroSpatialFormulation = inner(new Tag8(0x3d05));
                    break;
                case 0x3d07:
                    ChannelCount = inner(new Tag32(0x3d07));
                    break;
                case 0x3d01:
                    QuantizationBits = inner(new Tag32(0x3d01));
                    break;
                case 0x3d0c:
                    DialNorm = inner(new Tag8(0x3d0c));
                    break;
                case 0x3d06:
                    SoundEssenceCoding = inner(new TagUUID(0x3d06));
                    break;

                }
                return true;
            }
            return false;
        }
        return true;
    }

    public int getChannelCount() {
        return (int) (ChannelCount != null ? ChannelCount.value.get() : 0);
    }

    public int getBytesPerSample() {
        return (int) (QuantizationBits != null ? QuantizationBits.value.get() >> 3 : 0);
    }

    public int getBitsPerSample() {
        return (int) (QuantizationBits != null ? QuantizationBits.value.get() : 0);
    }

    public int getSampleRate() {
        return AudioSamplingRate != null ? AudioSamplingRate.num.get() : 0;
    }

}
