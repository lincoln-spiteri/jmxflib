package com.vg.mxf;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.vg.util.BER;
import com.vg.util.SeekableInputStream;

public class KLV implements Comparable<KLV> {
    public final long offset;
    public final long dataOffset;

    public final Key key;
    public final long len;

    ByteBuffer value;

    public KLV(Key k, long len, long offset, long dataOffset) {
        this.key = k;
        this.len = len;
        this.offset = offset;
        this.dataOffset = dataOffset;
    }

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

    public static KLV readKL(SeekableInputStream in) throws IOException {
        long offset = in.position();
        Key key = new Key();
        key.read(in);
        long len = BER.decodeLength(in);
        long dataOffset = in.position();
        KLV klv = new KLV(key, len, offset, dataOffset);
        return klv;
    }

    /**
     * @return byte count of BER encoded "length" field
     */
    public int getLenByteCount() {
        int berlen = (int) (dataOffset - offset - 16);
        return berlen <= 0 ? 4 : berlen;
    }
}