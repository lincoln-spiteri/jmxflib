package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UUID extends PackedStruct implements Comparable<UUID> {
    public static final UUID ZERO = new UUID();
    Signed64 hi = new Signed64();
    Signed64 lo = new Signed64();

    public UUID() {
        super();
    }

    public UUID(long hi, long lo) {
        super();
        this.hi.set(hi);
        this.lo.set(lo);
    }

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

    public int compareTo(UUID o) {
        byte[] bytes = getBytes();
        byte[] bytes2 = o.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            int b2 = bytes2[i];
            if (b != b2) {
                return (b & 0xff) - (b2 & 0xff);
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UUID) {
            return Arrays.equals(getBytes(), ((UUID) obj).getBytes());
        }
        return false;
    }

    public byte[] getBytes() {
        ByteBuffer byteBuffer = getByteBuffer().duplicate();
        byteBuffer.position(getByteBufferPosition());
        byte b[] = new byte[16];
        byteBuffer.get(b);
        return b;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBytes());
    }
}
