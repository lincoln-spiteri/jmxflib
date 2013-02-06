package com.vg.mxf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class KeyTest {
    @Test
    public void testMatches() throws Exception {
        Key dbk = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.04.00", 0xf00);
        Key ink = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.02.00");
        assertTrue(ink.matches(dbk));
        assertTrue(dbk.matches(ink));
        dbk = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.04.00");
        ink = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.02.00");
        assertFalse(dbk.matches(ink));
    }
    
    @Test
    public void testJ2KFrame() throws Exception {
        Key j2kFrameStrict = Key.key("06.0E.2B.34.01.02.01.01.0D.01.03.01.15.01.08.00");
        Key j2kFrame = Key.key("06.0E.2B.34.01.02.01.01.0D.01.03.01.15.01.08.00", 0xff);
        Key in = Key.key("06.0E.2B.34.01.02.01.01.0D.01.03.01.15.01.08.01");
        assertTrue(j2kFrame.matches(in));
        assertFalse(j2kFrameStrict.matches(in));
    }
    
    

}
