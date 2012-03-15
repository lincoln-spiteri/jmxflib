package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class FileDescriptor extends GenericDescriptor {
    Tag32 LinkedTrackId;
    TagRational SampleRate;
    Tag64 ContainerDuration;
    TagKey EssenceContainer;
    TagUUID Codec;

    private final static int localTags[] = new int[] { 0x3001, 0x3002, 0x3004, 0x3005, 0x3006 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3006:
                    LinkedTrackId = inner(new Tag32());
                    break;
                case 0x3001:
                    SampleRate = inner(new TagRational());
                    break;
                case 0x3002:
                    ContainerDuration = inner(new Tag64());
                    break;
                case 0x3004:
                    EssenceContainer = inner(new TagKey());
                    break;
                case 0x3005:
                    Codec = inner(new TagUUID());
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
