package com.vg.mxf;

public class TagUTF16String extends BaseTag {
    UTF16String str = null;

    public TagUTF16String(int expectedTag, int stringLengthBytes) {
        super(expectedTag);
        str = new UTF16String(stringLengthBytes);
    }

    @Override
    public String toString() {
        return str.toString();
    }

    @Override
    public int getValueSize() {
        return str == null ? 0 : str.bitLength() >> 3;
    }
}
