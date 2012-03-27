package com.vg.mxf;

import java.nio.ByteBuffer;

import org.junit.Assert;

public class PartitionPack extends MxfValue {
    Unsigned16 MajorVersion = new Unsigned16();
    Unsigned16 MinorVersion = new Unsigned16();

    Unsigned32 KAGSize = new Unsigned32();
    Signed64 ThisPartition = new Signed64();
    Signed64 PreviousPartition = new Signed64();
    Signed64 FooterPartition = new Signed64();
    Signed64 HeaderByteCount = new Signed64();
    Signed64 IndexByteCount = new Signed64();
    Unsigned32 IndexSID = new Unsigned32();
    Signed64 BodyOffset = new Signed64();
    Unsigned32 BodySID = new Unsigned32();
    OperationalPattern OperationalPattern = inner(new OperationalPattern());
    KeyList EssenceContainers = null;

    public void parse() {
        ByteBuffer content = getContent();
        int count = content.getInt();
        int size = content.getInt();
        Assert.assertEquals(16, size);
        EssenceContainers = inner(new KeyList(count));
    }

    public void setIndexSID(int i) {
        IndexSID.set(i);
    }

    public void setBodySID(int i) {
        BodySID.set(i);
    }

    public void setIndexByteCount(int size) {
        IndexByteCount.set(size);
    }
    
    public void setFooterPartition(long footerOffset) {
        FooterPartition.set(footerOffset);
    }
    
    public void setPreviousPartition(long offset) {
        PreviousPartition.set(offset);
    }
    
    public void setThisPartition(long footerOffset) {
        ThisPartition.set(footerOffset);
    }
    

}
