package com.vg.mxf;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vg.util.FileUtil;
import com.vg.util.SeekableFileInputStream;
import com.vg.util.SeekableInputStream;

public class MxfStructureTest {
    @Test
    @Ignore
    public void testNtsc() throws Exception {
        MxfStructure structure = read("testdata/WOF_3058_795198_1_NTSC_4x3_KK6855_JPEG2000_v0.mxf");
        int dropFrame = structure.getTimecodeComponent().getDropFrame();
        assertEquals(1, dropFrame);
        GenericPictureEssenceDescriptor pic = structure.getPictureEssenceDescriptor();
        Assert.assertEquals(1, pic.getFrameLayout());
        Assert.assertEquals(2, pic.getFieldDominance());
    }

    @Test
    @Ignore
    public void testUniversal() throws Exception {
        MxfStructure structure = read("testdata/DawnOfTheDead_TRAILER3_110711_01.mxf");
        int dropFrame = structure.getTimecodeComponent().getDropFrame();
        assertEquals(0, dropFrame);

    }

    @Test
    @Ignore
    public void testUniversalAudio() throws Exception {
        String pathname = "testdata/DawnOfTheDead_TRAILER3_110711_audio_01.mxf";
        MxfStructure structure = read(pathname);

        Assert.assertNull(structure.getPictureEssenceDescriptor());
        Fraction editRate = structure.getTimelineTrack().getEditRate();
        assertEquals(Fraction.getFraction(24000, 1001), editRate);

    }

    private MxfStructure read(String pathname) throws IOException {
        SeekableInputStream in = new SeekableFileInputStream(FileUtil.tildeExpand(pathname));
        MxfStructure structure = MxfStructure.readStructure(in);
        in.close();
        return structure;
    }

    @Test
    public void testUniversal2() throws Exception {
        String pathname = "testdata/SET342386_CHX05_EPS_NET_133_PS_ENG_01.mxf";
        SeekableInputStream in = new SeekableFileInputStream(FileUtil.tildeExpand(pathname));
        TreeMap<KLV, MxfValue> readHeader = MxfStructure.readHeader(in);
        HeaderPartitionPack value = (HeaderPartitionPack) readHeader.firstEntry().getValue();
        Assert.assertEquals(16244, value.HeaderByteCount.get());
        Dimension dim = new Dimension();
        for (Entry<KLV, MxfValue> entry : readHeader.entrySet()) {
            Key key = entry.getKey().key;
            if (JPEG2000PictureSubDescriptor.Key.equals(key)) {
                JPEG2000PictureSubDescriptor value2 = (JPEG2000PictureSubDescriptor) entry.getValue();
                dim.width = (int) value2.Xsiz.value.get();
                dim.height = (int) value2.Ysiz.value.get();
                System.out.println(key);
            }
        }
        Assert.assertEquals(1920, dim.width);
        Assert.assertEquals(1080, dim.height);
    }
}
