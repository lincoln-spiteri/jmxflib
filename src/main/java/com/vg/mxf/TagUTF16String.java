package com.vg.mxf;

public class TagUTF16String extends BaseTag {
    UTF16String str = null;

    public TagUTF16String(int stringLengthBytes) {
        str = new UTF16String(stringLengthBytes);
    }

    @Override
    public String toString() {
        return str.toString();
    }
}
