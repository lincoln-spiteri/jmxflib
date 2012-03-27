package com.vg.mxf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Assert;
import org.junit.Test;

import com.vg.util.SeekableFileInputStream;
import com.vg.util.SeekableInputStream;

public class MxfStructureTest {
    @Test
    public void testNtsc() throws Exception {
        MxfStructure structure = read("testdata/WOF_3058_795198_1_NTSC_4x3_KK6855_JPEG2000_v0.mxf");
        int dropFrame = structure.getTimecodeComponent().getDropFrame();
        assertEquals(1, dropFrame);
        GenericPictureEssenceDescriptor pic = structure.getPictureEssenceDescriptor();
        Assert.assertEquals(1, pic.getFrameLayout());
        Assert.assertEquals(2, pic.getFieldDominance());
    }

    @Test
    public void testUniversal() throws Exception {
        MxfStructure structure = read("testdata/DawnOfTheDead_TRAILER3_110711_01.mxf");
        int dropFrame = structure.getTimecodeComponent().getDropFrame();
        assertEquals(0, dropFrame);

    }

    @Test
    public void testUniversalAudio() throws Exception {
        String pathname = "testdata/DawnOfTheDead_TRAILER3_110711_audio_01.mxf";
        MxfStructure structure = read(pathname);

        Assert.assertNull(structure.getPictureEssenceDescriptor());
        Fraction editRate = structure.getTimelineTrack().getEditRate();
        assertEquals(Fraction.getFraction(24000, 1001), editRate);

    }

    private MxfStructure read(String pathname) throws IOException {
        SeekableInputStream in = new SeekableFileInputStream(new File(pathname));
        MxfStructure structure = MxfStructure.readStructure(in);
        in.close();
        return structure;
    }
}
