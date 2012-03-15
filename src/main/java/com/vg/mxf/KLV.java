package com.vg.mxf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.vg.util.BER;
import com.vg.util.FileUtil;
import com.vg.util.SeekableInputStream;

public class KLV implements Comparable<KLV> {
    long offset;
    long dataOffset;

    Key key;
    long len;

    ByteBuffer value;

    @Override
    public String toString() {
        return "KLV [offset=" + offset + ", dataOffset=" + dataOffset + ", key=" + key + ", len=" + len + ", value="
                + value + "]";
    }

    public int compareTo(KLV o) {
        long diff = offset - o.offset;
        if (diff != 0) {
            return Long.signum(diff);
        }
        return key.compareTo(o.key);
    }

    static KLV readKLV(InputStream in) throws IOException {
        KLV klv = new KLV();
        klv.key = new Key();
        klv.key.read(in);
        klv.len = BER.decodeLength(in);
        klv.value = ByteBuffer.allocate((int) klv.len);
        FileUtil.readFullyOrDie(in, klv.value);
        klv.value.clear();
        return klv;
    }

    public static KLV readKL(InputStream in) throws IOException {
        KLV klv = new KLV();
        klv.key = new Key();
        klv.key.read(in);
        klv.len = BER.decodeLength(in);
        return klv;
    }

    public static KLV readKL(SeekableInputStream in) throws IOException {
        KLV klv = new KLV();
        klv.offset = in.position();
        klv.key = new Key();
        klv.key.read(in);
        klv.len = BER.decodeLength(in);
        klv.dataOffset = in.position();
        return klv;
    }

    /**
     * @return byte count of BER encoded "length" field
     */
    public int getLenByteCount() {
        return (int) (dataOffset - offset - 16);
    }
}