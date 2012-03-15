package com.vg.mxf;

public class UUID extends PackedStruct {
    Signed64 hi = new Signed64();
    Signed64 lo = new Signed64();

    public String toString() {
        long mostSigBits = hi.get();
        long leastSigBits = lo.get();
        return (digits(mostSigBits >> 32, 8) + "-" + digits(mostSigBits >> 16, 4) + "-" + digits(mostSigBits, 4) + "-"
                + digits(leastSigBits >> 48, 4) + "-" + digits(leastSigBits, 12));
    }

    /** Returns val represented by the specified number of hex digits. */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

}
