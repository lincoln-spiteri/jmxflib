package com.vg.mxf;

public class Tag64 extends BaseTag {
    public Tag64(int expectedTag) {
        super(expectedTag);
    }

    Signed64 value = new Signed64();

    @Override
    public String toString() {
        return "" + value.get();
    }

    @Override
    public int getValueSize() {
        return 8;
    }

}
