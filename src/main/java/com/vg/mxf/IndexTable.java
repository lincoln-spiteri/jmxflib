package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;

public class IndexTable extends MxfValue {
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.02.01.01.10.01.00");
    private final static int localTags[] = handledLocalTags(IndexTable.class);//new int[] { 0x3f09, 0x3f0a, 0x3f0e, 0x3f0f, 0x3f10 };

    @Tag(tag = 0x3c0a)
    TagUUID InstanceUID = inner(new TagUUID(0x3c0a));
    @Tag(tag = 0x3f0b)
    TagRational IndexEditRate = inner(new TagRational(0x3f0b));
    @Tag(tag = 0x3f0c)
    Tag64 IndexStartPosition = inner(new Tag64(0x3f0c));
    @Tag(tag = 0x3f0d)
    Tag64 IndexDuration = inner(new Tag64(0x3f0d));
    @Tag(tag = 0x3f05)
    Tag32 EditUnitByteCount = inner(new Tag32(0x3f05));
    @Tag(tag = 0x3f06)
    Tag32 IndexSID = inner(new Tag32(0x3f06));
    @Tag(tag = 0x3f07)
    Tag32 BodySID = inner(new Tag32(0x3f07));
    @Tag(tag = 0x3f08)
    Tag8 SliceCount = inner(new Tag8(0x3f08));
    @Tag(tag = 0x3f0e)
    Tag8 PosTableCount;
    @Tag(tag = 0x3f09)
    TagDeltaEntryArray DeltaEntryArray;
    @Tag(tag = 0x3f0a)
    TagIndexEntryArray IndexEntryArray;
    @Tag(tag = 0x3f0f)
    Tag64 ExtStartOffset;
    @Tag(tag = 0x3f10)
    Tag64 VBEByteCount;

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

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (Arrays.binarySearch(localTags, localTag) >= 0) {
            switch (localTag) {
            case 0x3f0e:
                PosTableCount = inner(new Tag8(0x3f0e));
                break;
            case 0x3f09:
                int count = buf.getInt();
                DeltaEntryArray = inner(new TagDeltaEntryArray(0x3f09, count));
                break;
            case 0x3f0a:
                count = buf.getInt();
                IndexEntryArray = inner(new TagIndexEntryArray(0x3f0a, count));
                break;
            case 0x3f0f:
                ExtStartOffset = inner(new Tag64(0x3f0f));
                break;
            case 0x3f10:
                VBEByteCount = inner(new Tag64(0x3f10));
                break;
            }
            return true;
        }
        return false;
    }

    @Override
    public UUID getInstanceUID() {
        return InstanceUID.uuid;
    }

    public boolean containsFrame(int fn) {
        long startFn = IndexStartPosition.value.get();
        return fn >= startFn && fn < (startFn + IndexDuration.value.get());
    }

    public long getStreamOffset(int fn) {
        long startFn = IndexStartPosition.value.get();
        IndexEntry indexEntry = IndexEntryArray.entries[(int) (fn - startFn)];
        long off = indexEntry.StreamOffset.get();
        return off;
    }

    public static IndexTable createTable(long[] streamOffsets) {
        IndexTable t = new IndexTable();
        t.PosTableCount = t.inner(new Tag8(0x3f0e));
        t.DeltaEntryArray = t.inner(new TagDeltaEntryArray(0x3f09, 1));
        t.IndexEntryArray = t.inner(new TagIndexEntryArray(0x3f0a, streamOffsets.length));
        for (int i = 0; i < streamOffsets.length; i++) {
            IndexEntry e = t.IndexEntryArray.entries[i];
            Assert.assertNotNull(e);
            e.Flags.set((short) 128);
            e.KeyFrameOffset.set((byte) 0);
            e.StreamOffset.set(streamOffsets[i]);
            e.TemporalOffset.set((byte) 0);
        }
        t.InstanceUID.uuid.hi.set(0x0001020304050607L);
        t.InstanceUID.uuid.lo.set(0x08090a0b0c0d0e0fL);
        return t;
    }

    public void setIndexSID(int i) {
        IndexSID.value.set(i);
    }

    public void setBodySID(int i) {
        BodySID.value.set(i);
    }

    public void setIndexDuration(int frameCount) {
        IndexDuration.value.set(frameCount);
    }

    public void setIndexStartPosition(int i) {
        IndexStartPosition.value.set(i);
    }

    public void setIndexEditRate(int num, int den) {
        IndexEditRate.num.set(num);
        IndexEditRate.den.set(den);
    }

    public long getIndexDuration() {
        return IndexDuration != null ? IndexDuration.value.get() : 0L;
    }
}
