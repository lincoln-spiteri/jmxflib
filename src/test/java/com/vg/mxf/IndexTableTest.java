package com.vg.mxf;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.vg.io.SeekableFileInputStream;
import com.vg.io.SeekableInputStream;
import com.vg.mxf.Registry.ULDesc;

public class IndexTableTest {
    @Test
    public void testTags() throws Exception {
        int[] localTags = IndexTable.handledLocalTags(IndexTable.class);
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

    @Test
    public void testIMF() throws Exception {
        File f = new File(
                "/Users/zhukov/from_colorworks/20131007/ELDORADO/IMF/VOFELD_V3_20130412_OV/vof_el_dorado_v06_00_20120329_c06_grd18_REC709_noTrim_EXR_wCAT_00.mxf");
        File f_ = new File(
                "/Users/zhukov/testdata/j2k/hd/stem/media/StemTape_2010_HD_16x9_240_2398_english_0630_JPEG2000_v0.mxf");
        SeekableInputStream in = new SeekableFileInputStream(f);
        MxfStructure mxf = MxfStructure.readStructure(in);
        long frameStreamOffset = mxf.getFrameStreamOffset(241);
        in.close();
        in = new SeekableFileInputStream(f);
        long length = in.length();
        int fn = 0;
        while (length > in.position()) {
            long pos = in.position();
            KLV klv = KLV.readKL(in);
            boolean skip = true;
            // if (BodyPartitionPack.Key.matches(klv.key)) {
            // System.out.println(pos);
            // skip = false;
            // byte b[] = new byte[(int) klv.len];
            // Assert.assertEquals(b.length, in.read(b));
            // ByteBuffer wrap = ByteBuffer.wrap(b);
            // BodyPartitionPack it = new BodyPartitionPack();
            // it.setByteBuffer(wrap, 0);
            // it.parse();
            // System.out.println(it.toDebugString());
            //
            // }
            // if (IndexTable.Key.matches(klv.key)) {
            // skip = false;
            // byte b[] = new byte[(int) klv.len];
            // Assert.assertEquals(b.length, in.read(b));
            // ByteBuffer wrap = ByteBuffer.wrap(b);
            // IndexTable it = new IndexTable();
            // it.setByteBuffer(wrap, 0);
            // it.parse();
            // // System.out.println(it.toDebugString());
            // }
            if (Registry.JPEG2000FrameWrappedPictureElement.matches(klv.key)) {
                long idxpos = mxf.getFrameStreamOffset(fn);
                System.out.printf("%3d %10d %10d %10d\n", fn, pos, idxpos, (idxpos - pos));
                fn++;
            } else {
                System.out.println(klv);

            }
            if (skip) {
                in.skip(klv.len);
            }
            if (fn > 1000) {
                break;
            }
        }
        in.close();

    }

}
