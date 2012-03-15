package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class IndexTable extends MxfValue {
    TagUUID InstanceUID = inner(new TagUUID());
    TagRational IndexEditRate = inner(new TagRational());
    Tag64 IndexStartPosition = inner(new Tag64());
    Tag64 IndexDuration = inner(new Tag64());
    Tag32 EditUnitByteCount = inner(new Tag32());
    Tag32 IndexSID = inner(new Tag32());
    Tag32 BodySID = inner(new Tag32());
    Tag8 SliceCount = inner(new Tag8());
    Tag8 PosTableCount;
    TagDeltaEntryArray DeltaEntryArray;
    TagIndexEntryArray IndexEntryArray;
    Tag64 ExtStartOffset;
    Tag64 VBEByteCount;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.02.01.01.10.01.00");

    public void parse() {
        ByteBuffer buf = getAllContent();
        buf.position(size());
        while (buf.hasRemaining()) {
            int localTag = buf.getShort() & 0xffff;
            int sz = buf.getShort() & 0xffff;
            if (!handleTag(localTag, sz, buf)) {
                String msg = "unhandled tag " + Integer.toHexString(localTag);
                throw new IllegalStateException(msg);
            }
            buf.position(size());
        }

    }

    private final static int localTags[] = new int[] { 0x3f09, 0x3f0a, 0x3f0e, 0x3f0f, 0x3f10 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (Arrays.binarySearch(localTags, localTag) >= 0) {
            switch (localTag) {
            case 0x3f0e:
                PosTableCount = inner(new Tag8());
                break;
            case 0x3f09:
                int count = buf.getInt();
                DeltaEntryArray = inner(new TagDeltaEntryArray(count));
                System.out.println(DeltaEntryArray.toDebugString());
                break;
            case 0x3f0a:
                count = buf.getInt();
                IndexEntryArray = inner(new TagIndexEntryArray(count));
                break;
            case 0x3f0f:
                ExtStartOffset = inner(new Tag64());
                break;
            case 0x3f10:
                VBEByteCount = inner(new Tag64());
                break;
            }
            return true;
        }
        return false;
    }

}
