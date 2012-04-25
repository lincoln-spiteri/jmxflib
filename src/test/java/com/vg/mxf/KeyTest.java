package com.vg.mxf;

import org.junit.Assert;
import org.junit.Test;

public class KeyTest {
    @Test
    public void testMatches() throws Exception {
        Key dbk = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.04.00", 0xf00);
        Key ink = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.02.00");
        Assert.assertTrue(ink.matches(dbk));
        Assert.assertTrue(dbk.matches(ink));
        dbk = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.04.00");
        ink = Key.key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.02.02.00");
        Assert.assertFalse(dbk.matches(ink));
    }

}
