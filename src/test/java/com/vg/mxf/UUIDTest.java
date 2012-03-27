package com.vg.mxf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UUIDTest {
    @Test
    public void testZero() throws Exception {
        assertTrue(UUID.ZERO.equals(UUID.ZERO));
    }

    @Test
    public void testCmp() throws Exception {
        UUID a = new UUID(1, 0);
        UUID b = new UUID(2, 0);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertTrue(a.compareTo(a) == 0);
        assertTrue(b.compareTo(b) == 0);
        assertTrue(UUID.ZERO.compareTo(UUID.ZERO) == 0);
    }

}
