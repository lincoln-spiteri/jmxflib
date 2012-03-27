package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;

import com.vg.mxf.Registry.ULDesc;

public class InterchangeObject extends MxfValue {
    TagUUID InstanceUID = null;
    TagUUID GenerationUID = null;
    TagUUID ObjectClass = null;

    public void parse() {
        ByteBuffer buf = getContent();
        buf.position(size());
        while (buf.hasRemaining()) {
            int localTag = buf.getShort() & 0xffff;
            int sz = buf.getShort() & 0xffff;
            if (!handleTag(localTag, sz, buf)) {
                String msg = "unhandled tag " + Integer.toHexString(localTag);
                if (ltks != null) {
                    LocalTagKey ltk = ltks.getLtk(localTag);
                    if (ltk != null) {
                        Registry.ULDesc desc = ltks.getDesc(ltk.key.toString());
                        String n = "";
                        String d = "";
                        if (desc != null) {
                            n = desc.refName;
                            d = desc.desc;
                        }
                        msg = String.format("unhandled tag %04X: %s %s %s", localTag, ltk.key.toString(), n, d);
                    }
                }
                throw new IllegalStateException(msg);
            }
            buf.position(size());
        }

    }

    private final static int localTags[] = new int[] { 0x0101, 0x0102, 0x3c0a, };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (Arrays.binarySearch(localTags, localTag) >= 0) {
            switch (localTag) {
            case 0x3c0a:
                Assert.assertEquals(16, sz);
                InstanceUID = inner(new TagUUID(0x3c0a));
                break;
            case 0x0102:
                Assert.assertEquals(16, sz);
                GenerationUID = inner(new TagUUID(0x0102));
                break;
            case 0x0101:
                Assert.assertEquals(16, sz);
                ObjectClass = inner(new TagUUID(0x0101));
                break;
            }
            return true;
        }
        return false;
    }

    private LocalTags ltks;

    public void setLocalTags(LocalTags ltks) {
        this.ltks = ltks;
    }

    @Override
    public UUID getInstanceUID() {
        return InstanceUID != null ? InstanceUID.uuid : UUID.ZERO;
    }
}
