package com.vg.mxf;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class IndexTableTest {
    @Test
    public void testTags() throws Exception {
        int[] localTags = IndexTable.getLocalTags(IndexTable.class);
        Assert.assertEquals(13, localTags.length);
        Assert.assertEquals(0x3f10, localTags[12]);
    }

    @Test
    public void testWrite() throws Exception {
        IndexTable t = IndexTable.createTable(new long[] { 1234, 5678 });
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.write(out);
        byte[] byteArray = out.toByteArray();
        IndexTable t2 = new IndexTable();
        t2.setByteBuffer(ByteBuffer.wrap(byteArray), 0);
        t2.parse();
        assertEquals(2, t2.IndexEntryArray.elementCount.get());
        assertEquals(2, t2.IndexEntryArray.getCount());
        assertEquals(1234, t2.IndexEntryArray.entries[0].StreamOffset.get());
        assertEquals(5678, t2.IndexEntryArray.entries[1].StreamOffset.get());

    }

    @Test
    public void testHuge() throws Exception {
        IndexTable createTable = IndexTable.createTable(new long[200000]);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        createTable.write(bout);
        int size = bout.size();
        Assert.assertEquals(2200120, size);
    }

}
