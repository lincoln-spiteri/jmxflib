package com.vg.mxf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class TagValue extends BaseTag {
    public class Blob extends Member {

        private final int _length;

        public Blob(int lengthBytes) {
            super(lengthBytes << 3, 1);
            _length = lengthBytes; // Takes into account 0 terminator.
        }

        public void set(String string) {
            throw new UnsupportedOperationException();
        }

        public String get() {
            final ByteBuffer buffer = getByteBuffer();
            synchronized (buffer) {
                CharBuffer strb = CharBuffer.allocate(_length / 2);
                int index = getByteBufferPosition() + offset();
                buffer.position(index);
                for (int i = 0; i < _length / 2; i++) {
                    strb.put(buffer.getChar());
                }
                strb.flip();
                return strb.toString();
            }
        }

        public String toString() {
            return this.get();
        }
    }

    Blob value = null;

    public TagValue(int expectedTag, int valueSizeBytes) {
        super(expectedTag);
        value = new Blob(valueSizeBytes);
    }

    @Override
    public int getValueSize() {
        return value != null ? value.bitLength() >> 3 : 0;
    }

}
