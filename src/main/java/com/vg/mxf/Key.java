package com.vg.mxf;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Key extends PackedStruct implements Comparable<Key> {
    Unsigned8 oid = new Unsigned8();
    Unsigned8 ulSize = new Unsigned8();
    Unsigned8 ulCode = new Unsigned8();
    Unsigned8 smpte = new Unsigned8();

    Unsigned8 category = new Unsigned8();
    Unsigned8 registry = new Unsigned8();
    Unsigned8 structure = new Unsigned8();
    Unsigned8 version = new Unsigned8();

    Unsigned8[] item = array(new Unsigned8[8]);

    long hiMask = 0;
    long loMask = 0;

    public static Key key(String ul) {
        Key k = new Key();
        String[] split = ul.split("\\.");
        Assert.assertEquals(16, split.length);
        byte b[] = new byte[16];
        for (int i = 0; i < 16; i++) {
            int parseInt = Integer.parseInt(split[i], 16);
            b[i] = (byte) parseInt;
        }
        k.setByteBuffer(ByteBuffer.wrap(b), 0);
        return k;
    }
    
    public Category getCategory() {
        short s = category.get();
        Category[] values = Category.values();
        if (s < values.length) {
            return values[s];
        } else {
            return Category.Reserved;
        }
    }

    public Group getGroup() {
        short s = registry.get();
        return Group.groupFromInt(s);
    }

    public int compareTo(Key o) {
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
    public int hashCode() {
        return Arrays.hashCode(getBytes());
    }

    public byte[] getBytes() {
        ByteBuffer byteBuffer = getByteBuffer().duplicate();
        byteBuffer.position(getByteBufferPosition());
        byte b[] = new byte[16];
        byteBuffer.get(b);
        return b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Key) {
            return Arrays.equals(getBytes(), ((Key) obj).getBytes());
        }
        return false;
    }

    private final static char[] hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F' };

    @Override
    public String toString() {
        byte[] b = getBytes();
        char[] h = new char[3];
        h[2] = '.';
        StringBuilder sb = new StringBuilder(47);
        int i = 0;
        for (i = 0; i < b.length - 1; i++) {
            h[0] = hex[(b[i] >> 4) & 0xf];
            h[1] = hex[b[i] & 0xf];
            sb.append(h);
        }
        h[0] = hex[(b[i] >> 4) & 0xf];
        h[1] = hex[b[i] & 0xf];
        sb.append(h, 0, 2);
        return sb.toString();
    }

    public boolean matches(Key o) {
        LongBuffer lb = ByteBuffer.wrap(getBytes()).asLongBuffer();
        long him = hiMask | o.hiMask;
        long lom = loMask | o.loMask;
        long hi = lb.get(0) & (~him);
        long lo = lb.get(1) & (~lom);

        LongBuffer lb2 = ByteBuffer.wrap(o.getBytes()).asLongBuffer();
        long hi2 = lb2.get(0) & (~him);
        long lo2 = lb2.get(1) & (~lom);

        return hi == hi2 && lo == lo2;
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        xml.setAttribute("ul", toString());
        return xml;
    }

    public static Key key(String string, long loMask) {
        Key key = key(string);
        key.loMask = loMask;
        return key;
    }
    
    public static Key key(String string, long hiMask, long loMask) {
        Key key = key(string);
        key.loMask = loMask;
        key.hiMask = hiMask;
        return key;
    }
}
